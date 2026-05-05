package operators;

import java.util.concurrent.TimeUnit;

import energy.Consumo;
import energy.ConsumoEstado;
import energy.ZonaEnergetica;
import grpc.MonitorizacionGrpc;
import grpc.MonitorizacionProto.ConsumoReply;
import grpc.MonitorizacionProto.ConsumoRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import main.Config;
import pcd.util.Ventana;

//tarea 1 de la versión 3 (implementamos la clase OperarioRed, que será un hilo que continuamnete recoge y procesa consumos)
public class OperarioRed implements Runnable {
    
    private ZonaEnergetica zona;
    private int idOperario;
    
    //variables gRPC para el cliente en la versión 9
    private final ManagedChannel canal;
    private final MonitorizacionGrpc.MonitorizacionBlockingStub blockingStub;

    public OperarioRed(ZonaEnergetica zona, int idOperario) {
        this.zona = zona;
        this.idOperario = idOperario;
        
        canal = ManagedChannelBuilder.forAddress("localhost", 9002).usePlaintext().build(); //construimos el canal en el constructor
        
        blockingStub = MonitorizacionGrpc.newBlockingStub(canal);//inicializamos el stub bloqueante, al ser comunicación unary
    }

    //método para enviar la solicitud de consumo al servidor
    public void ejecutarAnotarConsumo(Consumo consumo) {
        //vemos si el consumo tiene alguna demanda de tipo solar
    	boolean esSolar = false;
        for (int i = 0; i < consumo.getDemandas().size(); i++) {
            if (consumo.getDemandas().get(i).getIdTipo().equals("SOLAR")) {
                esSolar = true;
                break; 
            }
        }

        ConsumoRequest req = ConsumoRequest.newBuilder().setIdConsumo(consumo.getIdConsumo()).setIdZona(consumo.getZona()).setKWh(consumo.getTotalKWh()).setDireccion(consumo.getDireccion()).setSolar(esSolar).build();

        try {
        	zona.getVentana().traza(" [ Cliente >>> ] Enviando anotación de consumo: " + req.getIdConsumo(), Ventana.VERDE);
        	
            ConsumoReply resp = blockingStub.anotarConsumo(req);
            
            zona.getVentana().traza(" [ >>> Cliente ] Recibida respuesta del Servidor (Zona:" + resp.getIdZona() + ")", Ventana.VERDE);
            zona.getVentana().traza(" Total Zona: " + resp.getTotalZona() + " | Red: " + resp.getTotalRed(), Ventana.VERDE);
        } catch (StatusRuntimeException e) {
        	
        }
    }
    
    
    public void shutdown() {
        try {
            canal.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @Override
    public void run() { //modificado en la tarea E de la Versión 8
    	
    	try {
            if (Config.MODO_ARRANQUE_OPERARIOS == 0) {
                //modo con semáforos V6
                zona.getArranque().acquire(); 
            } else if (Config.MODO_ARRANQUE_OPERARIOS == 1) {
                //modo con CyclicBarrier V8
                zona.getBarreraArranque().await(); //el hilo se queda esperando aquí hasta que lleguen los demás
            }
            
            zona.getLatchArranque().countDown();//tarea F de la v8, notificamos cuando este operario ya ha arrancado y decrementamos el contador
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
        while (true) {
            ConsumoEstado estadoAsignado = zona.getCentroControl().recogerConsumo(); //recoge el estado del consumo
            if (estadoAsignado == null) {
                break; //si es null quiere decir que el centro de control ha cerrado
            }
            if (estadoAsignado != null) {
                Consumo consumo = estadoAsignado.getConsumo();//extrae el consumo real y lo tramita
                try {
                	zona.getSemaforoCapacidad().acquire(); //si ya hay max_consumos operarios, se bloquea. Nunca va a haber ahora más de max_consumos operarios por zona
                	String resultado = zona.tramitarConsumo(consumo);
                    zona.getVentana().traza("Operario " + idOperario + " tramita: " + consumo.getIdConsumo() + " - " + resultado);
                    
                    ejecutarAnotarConsumo(consumo); //llamada al método Unary con el método definido arriba
                    
                    estadoAsignado.notificarProcesado();
                } catch (InterruptedException e) {
                	e.printStackTrace();
                } finally {
                	zona.getSemaforoCapacidad().release(); //siempre liberamos al terminar
                }
                
            }
        }
        shutdown(); //una vez termina le turno del operario y sale del bucle, cerramos el canal
    }
}