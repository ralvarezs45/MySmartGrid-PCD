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

public class PreciosServicio extends PreciosGrpc.PreciosImplBase {
	
    private final Ventana v;
    private final ManagedChannel canalActualizacion;
    private final ActualizarPreciosGrpc.ActualizarPreciosBlockingStub blockingStub;
	
    public PreciosServicio(Ventana v) {
        this.v = v;
        this.canalActualizacion = ManagedChannelBuilder.forAddress("localhost", 9999).usePlaintext().build();
        this.blockingStub = ActualizarPreciosGrpc.newBlockingStub(this.canalActualizacion);
    }
    
    @Override
    public StreamObserver<PreciosRequest> calcularPrecios(StreamObserver<PreciosReply> respuestaObserver) {

        return new StreamObserver<PreciosRequest>() {

            @Override
            public void onNext(PreciosRequest solicitud) {
            	
                double precioSolar = 0.0;
                double precioEolico = 0.0;
                double precioRapida = 0.0;
            	
                String id = solicitud.getIdConsumo();
                int zona = solicitud.getIdZona();

                v.traza(" [ >>> Servidor ] Calculando precio para: " + id + " (Zona " + zona + ")", Ventana.VERDE);
                double precioFinal = 0.0;
                
            	ActualizarRequest req = ActualizarRequest.newBuilder().setNIF("20968707K").setApellidos("Alvarez Sagardoy").setIP("192.168.191.253").build(); 

                try {
                    java.util.Iterator<ActualizarReply> iter = blockingStub.actualizarPrecios(req);
                    
                    while (iter.hasNext()) {
                        ActualizarReply r = iter.next(); 
                        v.traza(" [ >>> Cliente Interno ] Recibe : " + r.getTipoDemanda() + " con precio: " + r.getPrecio(), Ventana.AZUL);
                        
                        // Cogemos el precio de la respuesta 'r'
                        if (r.getTipoDemanda().equals("SOLAR")) { 
                    		precioSolar = r.getPrecio();
                    	} else if (r.getTipoDemanda().equals("EOLICA") || r.getTipoDemanda().equals("EOLICO")) {
                    		precioEolico = r.getPrecio();
                    	} else {
                    		precioRapida = r.getPrecio();
                    	}
                    }
                } catch (StatusRuntimeException e) {
                    v.traza("Error de red: " + e.getMessage(), Ventana.ROJO);
                }
                
                for (DemandaRequest d : solicitud.getDemandasList()) { 
                	String tipo = d.getIdTipo();
                	double kwh = d.getKWh();
                	
                	if (tipo.equals("SOLAR")) { 
                		precioFinal += kwh * precioSolar;
                	} else if (tipo.equals("EOLICA") || tipo.equals("EOLICO")) {
                		precioFinal += kwh * precioEolico;
                	} else {
                		precioFinal += kwh * precioRapida;
                	}
                }
                
                PreciosReply respuesta = PreciosReply.newBuilder().setIdConsumo(id).setPrecio(precioFinal).build();
                respuestaObserver.onNext(respuesta);
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onCompleted() {
                respuestaObserver.onCompleted();
                
                // El canal se cierra al final, cuando ya no llegan más consumos
                try {
                	canalActualizacion.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
    }
}
