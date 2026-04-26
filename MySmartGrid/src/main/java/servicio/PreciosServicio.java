package servicio;

import grpc.PreciosGrpc;
import grpc.PreciosProto.DemandaRequest;
import grpc.PreciosProto.PreciosReply;
import grpc.PreciosProto.PreciosRequest;
import io.grpc.stub.StreamObserver;
import pcd.util.Ventana;

public class PreciosServicio extends PreciosGrpc.PreciosImplBase{
	
	private final Ventana v;

    public PreciosServicio(Ventana v) {
        this.v = v;
    }
	@Override
    public StreamObserver<PreciosRequest> calcularPrecios(StreamObserver<PreciosReply> respuestaObserver) { //en este método la comunicación es bidireccional, es decir, el cliente envía un flujo de mensajes y recibe del servidor un flujo de mensajes también

        return new StreamObserver<PreciosRequest>() { //este es el observador que va a recibir el servidor desde el cliente

            @Override
            public void onNext(PreciosRequest solicitud) {
            	
                String id = solicitud.getIdConsumo();
                int zona = solicitud.getIdZona();

                v.traza(" [ >>> Servidor ] Calculando precio para: " + id + " (Zona " + zona + ")", Ventana.VERDE);

                double precioFinal = 0.0;
                
                //aquí aplicamos la lógica que se pide
                for (DemandaRequest d : solicitud.getDemandasList()) { //para cada una de las demandas de la solicitud
                	String tipo = d.getIdTipo();
                	double kwh = d.getKWh();
                	
                	if (tipo.equals("SOLAR")) { //aplicamos las tarifas
                		precioFinal += kwh * 0.08;
                	} else if (tipo.equals("EOLICA") || tipo.equals("EOLICO")) {
                		precioFinal += kwh * 0.10;
                	} else {
                		precioFinal += kwh * 0.15; 
                	}
                }

                //construimos la respuesta del servidor
                PreciosReply respuesta = PreciosReply.newBuilder().setIdConsumo(id).setPrecio(precioFinal).build();

                respuestaObserver.onNext(respuesta);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                respuestaObserver.onCompleted();
            }
        };
    }
}
