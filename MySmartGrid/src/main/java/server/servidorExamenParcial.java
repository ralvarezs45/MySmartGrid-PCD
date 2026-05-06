package server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import pcd.util.Ventana;
import servicio.ActualizarPreciosServicio;
import java.io.IOException;

public class servidorExamenParcial {

    public static void main(String[] args) throws IOException, InterruptedException {
        Ventana v = new Ventana(50, 50, 600, 400, "Servidor Examen Parcial - Puerto 9999");

        // AQUÍ ESTABA EL FALLO: Ahora sí ponemos el 9999
        Server servidor = ServerBuilder.forPort(9999)
                .addService(new ActualizarPreciosServicio(v))
                .build();

        servidor.start();
        v.traza(">>> Servidor del Examen Parcial escuchando en el puerto 9999...", Ventana.AZUL);
        servidor.awaitTermination();
    }
}
