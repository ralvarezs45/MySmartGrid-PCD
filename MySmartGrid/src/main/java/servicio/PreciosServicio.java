package servicio;

import grpc.PreciosGrpc;
import grpc.PreciosProto.PreciosReply;
import grpc.PreciosProto.PreciosRequest;
import io.grpc.stub.StreamObserver;

public class PreciosServicio extends PreciosGrpc.PreciosImplBase{
	@Override
    public StreamObserver<PreciosRequest> calcularPrecios(StreamObserver<PreciosReply> respuestaObserver) { //en este método la comunicación es bidireccional, es decir, el cliente envía un flujo de mensajes y recibe del servidor un flujo de mensajes también
        
        return new StreamObserver<PreciosRequest>() { //este es el observador que va a recibir el servidor desde el cliente

            @Override
            public void onNext(PreciosRequest solicitud) {
            	
                String id = solicitud.getIdConsumo();
                int zona = solicitud.getIdZona();
                int numDemandas = solicitud.getDemandasCount();

                System.out.println(" [ >>> Servidor ] Calculando precio para: " + id + " (Zona " + zona + ")");

                //el precio depende del número de demandas
                double precioFinal = 0.15 * numDemandas;

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
