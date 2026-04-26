package server;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import pcd.util.Ventana;
import servicio.MonitorizacionServicio;
import servicio.PreciosServicio;

public class Servidor {
	public static void main(String[] args) throws IOException, InterruptedException {
		
		//creamos una ventana para cada servidor
		Ventana v1 = new Ventana(30, 30, 500, 400, "Servidor Monitorización MySmartGrid gRPC- Puerto 9002");
        Ventana v2 = new Ventana(550, 30, 500, 400, "Servidor Precios MySmartGrid gRPC- Puerto 9004");    	
        
      
        //configuramos los servidores 
    	Server serverMonitorizacion = ServerBuilder.forPort(9002).addService(new MonitorizacionServicio(v)).build();
        Server serverPrecios = ServerBuilder.forPort(9004).addService(new PreciosServicio(v)).build();

        //arrancamos los servidores
        serverMonitorizacion.start();
        v1.traza(">>> Servidor Escuchando en puerto 9002...", Ventana.VERDE);
        
        serverPrecios.start();
        v2.traza(">>> Servidor Escuchando en puerto 9004...", Ventana.VERDE);
       
        //mantenemos los servidores en ejecución
        serverMonitorizacion.awaitTermination();
        serverPrecios.awaitTermination();
    }
}
