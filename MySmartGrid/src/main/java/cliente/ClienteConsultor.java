package cliente;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import grpc.MonitorizacionGrpc;
import grpc.MonitorizacionProto.DemandaReply;
import grpc.MonitorizacionProto.DemandaRequest;
import grpc.MonitorizacionProto.DireccionReply;
import grpc.MonitorizacionProto.DireccionRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import pcd.util.Ventana;

public class ClienteConsultor {
	
	private final ManagedChannel canal;
	private final MonitorizacionGrpc.MonitorizacionBlockingStub blockingStub;
	private final MonitorizacionGrpc.MonitorizacionStub asyncStub;

	private final Ventana v;
	
	public ClienteConsultor(String host, int port) {
         canal = ManagedChannelBuilder.forAddress(host, port).usePlaintext() .build();

        blockingStub = MonitorizacionGrpc.newBlockingStub(canal);
        asyncStub = MonitorizacionGrpc.newStub(canal);
        
        v = new Ventana(800, 30, 500, 500, "Cliente Consultor - Versión 9 MySmartGrid");
    }
	
	//este método es server streaming
	public void consultarDemandaSolar(int idZona) {
        v.traza("\n--- Server Streaming: demandaSolar ---", Ventana.VERDE);
        
        DemandaRequest req = DemandaRequest.newBuilder().setIdZona(idZona).build();

        try {
            java.util.Iterator<DemandaReply> iter = blockingStub.demandaSolar(req);
            
            while (iter.hasNext()) {
                DemandaReply r = iter.next();
                v.traza(" [ >>> Cliente ] Consumo Solar encontrado: " + r.getIdConsumo(), Ventana.VERDE);
            }
            v.traza(" [ >>> Cliente ] 'demandaSolar' finalizada.", Ventana.VERDE);
            
        } catch (StatusRuntimeException e) {

        }
    }
	
	//este método es client streaming
	public void enviarConsumosDireccion(List<String> direcciones) {
        v.traza("\n--- Client Streaming: consumosDireccion ---", Ventana.VERDE);
        
        final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);

        StreamObserver<DireccionReply> responseObserver = new StreamObserver<DireccionReply>() {
            @Override
            public void onNext(DireccionReply r) {
                v.traza(" [ >>> Cliente ] Respuesta final del servidor: Total demandas solares = " + r.getTotal(), Ventana.VERDE);
            }
            
            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }
            
            @Override
            public void onCompleted() {
                v.traza(" [ >>> Cliente ] 'consumosDireccion' completado por el servidor.", Ventana.VERDE);
                latch.countDown();
            }
        };

        StreamObserver<DireccionRequest> requestObserver = asyncStub.consumosDireccion(responseObserver);
        
        try {
            for (String dir : direcciones) {
                v.traza(" [ Cliente >>> ] -> " + dir, Ventana.VERDE);
                DireccionRequest req = DireccionRequest.newBuilder().setDireccion(dir).build();
                requestObserver.onNext(req);
                
                Thread.sleep(300);
            }
        } catch (RuntimeException | InterruptedException e) {
            requestObserver.onError(e);
            Thread.currentThread().interrupt();
        }
        
        requestObserver.onCompleted();

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        v.traza("--- Fin Client Streaming ---\n", Ventana.VERDE);
    }
	
	public void shutdown() throws InterruptedException { //método para cerrar el canal
        v.traza(" [ >>> Cliente ] Cerrando canal...", Ventana.VERDE);
        canal.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        v.traza(" [ >>> Cliente ] Canal cerrado.", Ventana.VERDE);
    }
	
	public static void main(String[] args) {
        ClienteConsultor cliente = new ClienteConsultor("localhost", 9002);
        
        try {
            cliente.consultarDemandaSolar(1);
            
            List<String> listaDirecciones = Arrays.asList("Sagitario, 24", "Berna, 11", "Goya, 5");
            cliente.enviarConsumosDireccion(listaDirecciones);
            
            
        } finally {
            try {
                cliente.shutdown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
