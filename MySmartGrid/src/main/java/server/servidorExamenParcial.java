package server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import pcd.util.Ventana;
import servicio.ActualizarPreciosServicio;

import java.io.IOException;

public class servidorExamenParcial {

    public static void main(String[] args) throws IOException, InterruptedException {
        
    	//crear ventana
        Ventana v = new Ventana(50, 50, 600, 400, "Servidor Examen Parcial - Puerto 9999");

        //construir server
        Server servidor = ServerBuilder.forPort(9999)
                .addService(new ActualizarPreciosServicio(v))
                .build();

        //arrancar
        servidor.start();
        v.traza(">>> Servidor del Examen Parcial escuchando en el puerto 9999...", Ventana.AZUL);
        
        //mantener vivo el servidor
        servidor.awaitTermination();
    }
}
