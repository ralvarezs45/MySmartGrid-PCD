package servicio;


import java.util.concurrent.TimeUnit;

import grpc.ActualizarPreciosGrpc;
import grpc.ActualizarPreciosProto.ActualizarReply;
import grpc.ActualizarPreciosProto.ActualizarRequest;
import grpc.PreciosGrpc;
import grpc.PreciosProto.DemandaRequest;
import grpc.PreciosProto.PreciosReply;
import grpc.PreciosProto.PreciosRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import pcd.util.Ventana;

public class PreciosServicio extends PreciosGrpc.PreciosImplBase{
	
	private final Ventana v;
	private final ManagedChannel canalActualizacion;
    private final ActualizarPreciosGrpc.ActualizarPreciosBlockingStub blockingStub;
	
    public PreciosServicio(Ventana v) {
        this.v = v;
        this.canalActualizacion = ManagedChannelBuilder.forAddress("localhost", 9999).usePlaintext().build();
        this.blockingStub = ActualizarPreciosGrpc.newBlockingStub(this.canalActualizacion);
    }
    
    
    
	@Override
    public StreamObserver<PreciosRequest> calcularPrecios(StreamObserver<PreciosReply> respuestaObserver) { //en este método la comunicación es bidireccional, es decir, el cliente envía un flujo de mensajes y recibe del servidor un flujo de mensajes también

        return new StreamObserver<PreciosRequest>() { //este es el observador que va a recibir el servidor desde el cliente

            @Override
            public void onNext(PreciosRequest solicitud) {
            	
            	double precioSolar = 0.0;
            	double precioEolico = 0.0;
            	double precioRapida = 0.0;
            	
                String id = solicitud.getIdConsumo();
                int zona = solicitud.getIdZona();

                v.traza(" [ >>> Servidor ] Calculando precio para: " + id + " (Zona " + zona + ")", Ventana.VERDE);

                double precioFinal = 0.0;
                
                
                
                
                //MODIFICACIÓN DE LA DEFENSA DEL PROYECTO, AQUÍ SE LLAMA AHORA AL MÉTODO ACTUALIZARPRECIOS Y ESTE PASA A SER EL CLIENTE,
                //CON UNA COMUNICACIÓN SERVER STREAMING
                
                
            	//primero construimos la solicitud del cliente
            	ActualizarRequest req = ActualizarRequest.newBuilder().setNIF("20968707K").setApellidos("Alvarez Sagardoy").setIP("192.168.191.253").build(); //construimos una única solicitud

                try {
                    java.util.Iterator<ActualizarReply> iter = blockingStub.actualizarPrecios(req);
                    
                    while (iter.hasNext()) {
                        ActualizarReply r = iter.next(); //cliente recibe múltiples respuestas por parte del servidor
                        v.traza(" [ >>> Cliente ] Cliente recibe : " + r.getTipoDemanda() + " con precio: " + r.getPrecio(), Ventana.VERDE);
                        if (r.getTipoDemanda().equals("SOLAR")) { //calculamos el nuevo precio tras actualizar
                    		precioSolar = 0.02;
                    	} else if (r.getTipoDemanda().equals("EOLICA") || r.getTipoDemanda().equals("EOLICO")) {
                    		precioEolico = 0.02;
                    	} else {
                    		precioRapida = 0.2;
                    	}
                    }
                    v.traza(" [ >>> Cliente ] Fin del flujo de datos enviados por el servidor ", Ventana.VERDE);
                    
                } catch (StatusRuntimeException e) {
                	v.traza("Error de conexión gRPC: " + e.getMessage(), Ventana.ROJO);
                }
                
                
                //aquí aplicamos la lógica que se pide
                for (DemandaRequest d : solicitud.getDemandasList()) { //para cada una de las demandas de la solicitud aplicamos las tarifas según el tipo de demanda y sus nuevos precios
                	String tipo = d.getIdTipo();
                	double kwh = d.getKWh();
                	
                	if (tipo.equals("SOLAR")) { //aplicamos las tarifas
                		precioFinal += kwh * precioSolar;
                	} else if (tipo.equals("EOLICA") || tipo.equals("EOLICO")) {
                		precioFinal += kwh * precioEolico;
                	} else {
                		precioFinal += kwh * precioRapida;
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
                
                
                //cerrar el canal una vez termina el cliente
                try {
                	canalActualizacion.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
    }
}
