package servicio;

import grpc.ActualizarPreciosGrpc;
import grpc.ActualizarPreciosProto.ActualizarRequest;
import grpc.ActualizarPreciosProto.ActualizarReply;
import io.grpc.stub.StreamObserver;
import pcd.util.Ventana;

public class ActualizarPreciosServicio extends ActualizarPreciosGrpc.ActualizarPreciosImplBase {
	
    private final Ventana v;

    public ActualizarPreciosServicio(Ventana v) {
        this.v = v;
    }
    
    @Override
    public synchronized void actualizarPrecios(ActualizarRequest solicitud, StreamObserver<ActualizarReply> respuestaObserver) {
        
        String NIF = solicitud.getNIF();
        String Apellidos = solicitud.getApellidos();
        String IP = solicitud.getIP();
        
        // 1. MUESTRA LA PETICIÓN
        v.traza(" [ >>> Servidor ] Cliente con NIF: " + NIF + " y Apellidos: " + Apellidos + " con IP: " + IP + " solicita actualización de precios", Ventana.VERDE);
        
        String[] tipos = {"SOLAR", "EOLICA", "RAPIDA"};
        double[] nuevosPrecios = {0.02, 0.02, 0.20};
        
        // 2. MUESTRA Y ENVÍA LOS 3 MENSAJES
        for (int i = 0; i < tipos.length; i++) {
            ActualizarReply respuesta = ActualizarReply.newBuilder()
                    .setTipoDemanda(tipos[i])
                    .setPrecio(nuevosPrecios[i])
                    .build();
            
            v.traza(" -> Enviando actualización: " + tipos[i] + " a " + nuevosPrecios[i] + " €", Ventana.AZUL);
            respuestaObserver.onNext(respuesta);
        }
        
        v.traza("Fin del envío de respuestas por parte del servidor", Ventana.VERDE);
        respuestaObserver.onCompleted();
    }
}
