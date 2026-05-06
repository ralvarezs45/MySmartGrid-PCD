package servicio;


import grpc.ActualizarPreciosGrpc;
import grpc.ActualizarPreciosProto.ActualizarRequest;
import grpc.ActualizarPreciosProto.ActualizarReply;
import io.grpc.stub.StreamObserver;
import pcd.util.Ventana;

public class ActualizarPreciosServicio extends ActualizarPreciosGrpc.ActualizarPreciosImplBase{
	
	private final Ventana v;

    public ActualizarPreciosServicio(Ventana v) {
        this.v = v;
    }
    
	@Override
    public synchronized void actualizarPrecios(ActualizarRequest solicitud, StreamObserver<ActualizarReply> respuestaObserver) {//en este método la comunicación es Server Streaming, el cliente envía una única solicitud y el servidor contesta con un stream o flujo de mensajes
        
		//primero extraemos los datos de la solicitud con los getters que genera el proto
		String NIF = solicitud.getNIF();
		String Apellidos = solicitud.getApellidos();
		String IP = solicitud.getIP();
		//Cliente envía una única solicitud
        v.traza(" [ >>> Servidor ] Cliente con datos:" + "NIF: " + NIF + " y Apellidos: " + Apellidos + " con IP: " + IP + " solicita actualización de precios", Ventana.VERDE);
        
        String[] tipos = {"SOLAR", "EOLICA", "RAPIDA"};
        double[] nuevosPrecios = {0.02, 0.02, 0.20};
        
        // SERVER STREAMING: Enviamos múltiples respuestas mediante un bucle
        for (int i = 0; i < tipos.length; i++) {
            ActualizarReply respuesta = ActualizarReply.newBuilder()
                    .setTipoDemanda(tipos[i])
                    .setPrecio(nuevosPrecios[i])
                    .build();
            
            v.traza(" -> Enviando actualización: " + tipos[i] + " a " + nuevosPrecios[i] + " €", Ventana.AZUL);
            respuestaObserver.onNext(respuesta);
        }
        
        //cuando el servidor termina de enviar mensajes
        v.traza("Fin del envío de respuestas por parte del servidor", Ventana.VERDE);
        respuestaObserver.onCompleted();
    } 
}
