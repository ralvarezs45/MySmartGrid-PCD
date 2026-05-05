package cliente;

import energy.Consumo;
import grpc.FiltradoGrpc;
import grpc.FiltroOuterClass.FiltroReply;
import grpc.FiltroOuterClass.FiltroRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pcd.util.Ventana;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ClienteExamen {//MISMA ESTRUCTURA QUE MÉTODO BIDIRECTIONAL DE CALCULARPRECIOS()

    public static void examenAbril(List<FiltroReply> listaRecibida, Ventana v) {
        v.traza("\n --- Iniciando método examenAbril ---", Ventana.AZUL);
        
        //construir un Observable a partir de la lista
        Observable<FiltroReply> observable = Observable.fromIterable(listaRecibida);

        //suscribir un observador concurrente
        observable
            .subscribeOn(Schedulers.computation()) 
            .blockingSubscribe(respuesta -> {
                v.traza("[RxJava - Hilo: " + Thread.currentThread().getName() + "] ID Aceptado: " + respuesta.getIdConsumo(), Ventana.VERDE);
            });
    }

    public static void main(String[] args) {
        
        //crear ventana
        Ventana vCliente = new Ventana(700, 50, 600, 450, "Cliente Examen - Puerto 9096");

        //leer consumos desde fichero
        List<Consumo> consumosLeidos = Consumo.consumosDesdeFichero("consumos5.bin");

        //conectar al servidor
        ManagedChannel canal = ManagedChannelBuilder.forAddress("192.168.191.25", 9096).usePlaintext().build();
        FiltradoGrpc.FiltradoStub asyncStub = FiltradoGrpc.newStub(canal);

        //creamos lista para respuestas
        List<FiltroReply> consumosFiltrados = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch cerrojo = new CountDownLatch(1);

        // StreamObserver para recibir por parte del servidor
        StreamObserver<FiltroReply> respuestaObserver = new StreamObserver<FiltroReply>() { //esto es lo que escucha el cliente
            @Override
            public void onNext(FiltroReply respuesta) {
                vCliente.traza(" [ >>> Cliente ] Recibido del Servidor: " + respuesta.getIdConsumo() + " (Aceptado)", Ventana.VERDE);
                consumosFiltrados.add(respuesta);
            }

            @Override
            public void onError(Throwable t) {
                cerrojo.countDown();
            }

            @Override
            public void onCompleted() {
                vCliente.traza(" [ >>> Cliente ] Servidor ha cerrado su flujo", Ventana.AZUL);
                cerrojo.countDown();
            }
        };

        //llamada al método gRPC
        StreamObserver<FiltroRequest> solicitudObserver = asyncStub.filtrar(respuestaObserver);

        try {
            vCliente.traza("Enviando " + consumosLeidos.size() + " consumos al servidor...", Ventana.VERDE);
            
            for (Consumo c : consumosLeidos) { //para cada uno de los consumos leídos desde fichero, enviamos al ser bidirectional stream
                vCliente.traza(" -> Enviando: " + c.getIdConsumo() + " (" + (int)c.getTotalKWh() + " kWh)", Ventana.GRIS);
                
                FiltroRequest peticion = FiltroRequest.newBuilder().setIdConsumo(c.getIdConsumo()).setDireccion(c.getDireccion()).setTotalKWh((int) c.getTotalKWh()).build();
                
                solicitudObserver.onNext(peticion);
                Thread.sleep(100); 
            }
            
            solicitudObserver.onCompleted();
            cerrojo.await(20, TimeUnit.SECONDS);
            canal.shutdown().awaitTermination(5, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            vCliente.traza("Error: " + e.getMessage(), Ventana.ROJO);
        }

        examenAbril(consumosFiltrados, vCliente);
        
        vCliente.traza("\n--- Fin ---", Ventana.AZUL);
    }
}