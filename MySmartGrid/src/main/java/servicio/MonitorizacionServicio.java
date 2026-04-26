package servicio;

import grpc.MonitorizacionGrpc;
import grpc.MonitorizacionProto.ConsumoReply;
import grpc.MonitorizacionProto.ConsumoRequest;
import grpc.MonitorizacionProto.DemandaReply;
import grpc.MonitorizacionProto.DemandaRequest;
import grpc.MonitorizacionProto.DireccionReply;
import grpc.MonitorizacionProto.DireccionRequest;
import io.grpc.stub.StreamObserver;
import pcd.util.Ventana;

public class MonitorizacionServicio extends MonitorizacionGrpc.MonitorizacionImplBase{
	
	private final Ventana v;

    public MonitorizacionServicio(Ventana v) {
        this.v = v;
    }
	
	@Override
    public void anotarConsumo(ConsumoRequest solicitud, StreamObserver<ConsumoReply> respuestaObserver) {//la comunicación es Unary, es decir, el cliente envía un mensaje y espera una única respuesta del servidor
        
		//primero extraemos los datos de la solicitud con los getters que genera el proto
        String id = solicitud.getIdConsumo();
        double kwh = solicitud.getKWh();
        int zona = solicitud.getIdZona();
        
        v.traza(" [ >>> Servidor ] Registrando consumo con " + id + " en zona " + zona, Ventana.VERDE);

        //actualizar Red Energética
        double totalZ = kwh + 50.0; 
        double totalR = kwh + 200.0;

        //construimos la respuesta del servidor
        ConsumoReply respuesta = ConsumoReply.newBuilder().setIdZona(zona).setTotalZona(totalZ).setTotalRed(totalR).build();

        //enviamos la respuesta
        respuestaObserver.onNext(respuesta);

        //como es Unary, debemos cerrar el canal cuando se envíe la respuesta
        respuestaObserver.onCompleted();
    }
	
	@Override
    public void demandaSolar(DemandaRequest solicitud, StreamObserver<DemandaReply> respuestaObserver) {//en este método la comunicación es Server Streaming, el cliente envía una única solicitud y el servidor contesta con un stream o flujo de mensajes
        
		//primero extraemos los datos de la solicitud con los getters que genera el proto
        int zona = solicitud.getIdZona();
        v.traza(" [ >>> Servidor ] Cliente solicita demanda solar en zona: " + zona, Ventana.VERDE);
        
        //Simulamos que encontramos 5 consumos en esa zona y lo enviamos como un stream
        for (int i = 1; i <= 5; i++) {
            String idConsumoEncontrado = "SOLAR_Z" + zona + "_" + i;
            
            //construimos la respuesta
            DemandaReply respuesta = DemandaReply.newBuilder().setIdConsumo(idConsumoEncontrado).build();
            
            //enciamos cada una de las respuestas por separado
            respuestaObserver.onNext(respuesta);
            
            //ponemos un pequeño retardo
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //cerramos el canal cuando haya terminado de enviar mensajes el servidor
        v.traza("Fin del envío de demanda solar para zona " + zona, Ventana.VERDE);
        respuestaObserver.onCompleted();
    }
	
	
	@Override
    public StreamObserver<DireccionRequest> consumosDireccion(StreamObserver<DireccionReply> respuestaObserver) { //en este último método la comunicación es Client Streaming, en la que el cliente envía un flujo o stream de mensajes y el servidor responde con una única respuesta
    
        //devolvemos un observador que va a escuchar las múlitples peticiones del cliente
        return new StreamObserver<DireccionRequest>() {
            int totalConsumos = 0; 

            @Override
            public void onNext(DireccionRequest solicitud) {
                String direccion = solicitud.getDireccion();
                v.traza(" [ >>> Servidor ] Recibida la dirección: " + direccion, Ventana.VERDE);
                
                //para cada dirección encontramos un consumo, por ejemplo
                totalConsumos += 1; 
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {//una vez el cliente termine de enviar mensajes     
            	//construimos la respuesta
            	DireccionReply respuesta = DireccionReply.newBuilder().setTotal(totalConsumos).build();
                
                respuestaObserver.onNext(respuesta);
                
                respuestaObserver.onCompleted(); //enviamos y cerramos el canal al terminar
            }
        };
    }
	
}
