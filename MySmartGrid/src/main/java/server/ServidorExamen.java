package server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import pcd.util.Ventana;
import servicio.FiltroServicio;

import java.io.IOException;

public class ServidorExamen {

    public static void main(String[] args) throws IOException, InterruptedException {
        
    	//crear ventana
        Ventana v = new Ventana(50, 50, 600, 400, "Servidor Examen - Puerto 9096");

        //construir server
        Server servidor = ServerBuilder.forPort(9096)
                .addService(new FiltroServicio(v))
                .build();

        //arrancar
        servidor.start();
        v.traza(">>> Servidor del Examen escuchando en el puerto 9096...", Ventana.AZUL);
        
        //mantener vivo el servidor
        servidor.awaitTermination();
    }
}