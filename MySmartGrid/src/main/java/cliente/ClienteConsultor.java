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

/*
 * Me gustaría hacer una breve explicación sobre la implementación completa de la versión 9 para diferenciar claramente el código anterior del nuevo.
 * Para transformar nuestro programa MySmartGrid en una aplicación distribuida hemos implementado la siguiente estructura:
 * Contamos con dos servidores: uno de monitorización en el puerto 9002 ; y otro para calcularPrecios en el puerto 9004.
 * Los stubs que empleamos son bloqueantes para llamadas síncronas.
 * Diferenciamos entre 4 tipos de comunicación empleados:
 * 1. Unary para el método anotarConsumo (Operario de red informa de cada tramitación y espera respuesta única del servidor).
 * 2. Server Stream para demandaSolar (Consultor recibe un flujo de consumos solares tras una única solicitud).
 * 3. Client Stream para consumosDireccion (Consultor envía un flujo de direcciones, el servidor las procesa y devuelve un único total al finalizar el stream).
 * 4. Bidirectional Stream para calcularPrecios (Cliente MySmartGrid y servidor del puerto 9004 intercambian datos simultáneamente y de forma asíncrona).
 * La estructura seguida para cada cliente, servidor y servicios se puede observar como documentación interna; idéntica a la de los vídeos y vista en guiones de prácticas.
 * Finalmente, también cabe mencionar que se ha empleado el uso de ventanas desde el paquete pcd.util para una visión más estructurada de implementación gRPC.
 * Los textos que diferencian de los anteriores están en color verde, para una interpretación más eficaz. 
 */



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

        StreamObserver<DireccionReply> respuestaObserver = new StreamObserver<DireccionReply>() {
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

        StreamObserver<DireccionRequest> solicitudObserver = asyncStub.consumosDireccion(respuestaObserver);
        
        try {
            for (String dir : direcciones) {
                v.traza(" [ Cliente >>> ] -> " + dir, Ventana.VERDE);
                DireccionRequest req = DireccionRequest.newBuilder().setDireccion(dir).build();
                solicitudObserver.onNext(req);
                
                Thread.sleep(300);
            }
        } catch (RuntimeException | InterruptedException e) {
            solicitudObserver.onError(e);
            Thread.currentThread().interrupt();
        }
        
        solicitudObserver.onCompleted();

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
