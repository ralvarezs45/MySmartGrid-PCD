package servicio;

import java.util.ArrayList;
import java.util.HashMap;

import grpc.MonitorizacionGrpc;
import grpc.MonitorizacionProto.ConsumoReply;
import grpc.MonitorizacionProto.ConsumoRequest;
import grpc.MonitorizacionProto.DemandaReply;
import grpc.MonitorizacionProto.DemandaRequest;
import grpc.MonitorizacionProto.DireccionReply;
import grpc.MonitorizacionProto.DireccionRequest;
import io.grpc.stub.StreamObserver;
import pcd.util.Ventana;

public class MonitorizacionServicio extends MonitorizacionGrpc.MonitorizacionImplBase{
	
	private final Ventana v;
	
	private final ArrayList<ConsumoRequest> historialConsumos = new ArrayList<>();
	private final HashMap<Integer, Double> totalesPorZona = new HashMap<>();
	private double totalRedGlobal = 0.0;

    public MonitorizacionServicio(Ventana v) {
        this.v = v;
    }
	
	@Override
    public synchronized void anotarConsumo(ConsumoRequest solicitud, StreamObserver<ConsumoReply> respuestaObserver) {//la comunicación es Unary, es decir, el cliente envía un mensaje y espera una única respuesta del servidor
		//le ponemos synchronized a ambos métodos para que dos operarios no escriban a la vez 
		
		historialConsumos.add(solicitud);
		
		//primero extraemos los datos de la solicitud con los getters que genera el proto
        String id = solicitud.getIdConsumo();
        double kwh = solicitud.getKWh();
        int zona = solicitud.getIdZona();
        
        v.traza(" [ >>> Servidor ] Registrando consumo con " + id + " en zona " + zona, Ventana.VERDE);

        //actualizar Red Energética de la zona
        double totalActualZona = 0.0;
        if (totalesPorZona.containsKey(zona)) {
        	totalActualZona = totalesPorZona.get(zona);
        }
        totalesPorZona.put(zona, totalActualZona + kwh);
        totalRedGlobal += kwh;

        //construimos la respuesta del servidor
        ConsumoReply respuesta = ConsumoReply.newBuilder().setIdZona(zona).setTotalZona(totalesPorZona.get(zona)).setTotalRed(totalRedGlobal).build();
        
        //enviamos la respuesta
        respuestaObserver.onNext(respuesta);

        //como es Unary, debemos cerrar el canal cuando se envíe la respuesta
        respuestaObserver.onCompleted();
    }
	
	@Override
    public synchronized void demandaSolar(DemandaRequest solicitud, StreamObserver<DemandaReply> respuestaObserver) {//en este método la comunicación es Server Streaming, el cliente envía una única solicitud y el servidor contesta con un stream o flujo de mensajes
        
		//primero extraemos los datos de la solicitud con los getters que genera el proto
        int zona = solicitud.getIdZona();
        v.traza(" [ >>> Servidor ] Cliente solicita demanda solar en zona: " + zona, Ventana.VERDE);
        
        //Simulamos que encontramos 5 consumos en esa zona y lo enviamos como un stream
        for (int i = 0; i < historialConsumos.size(); i++) {
        	ConsumoRequest c = historialConsumos.get(i);
        	
        	if (c.getIdZona() == zona && c.getSolar() == true) {
        		DemandaReply respuesta = DemandaReply.newBuilder().setIdConsumo(c.getIdConsumo()).build();
                respuestaObserver.onNext(respuesta);
                
                try { Thread.sleep(200); } catch (InterruptedException e) {} // Efecto visual
        	}
        }

        //cerramos el canal cuando haya terminado de enviar mensajes el servidor
        v.traza("Fin del envío de demanda solar para zona " + zona, Ventana.VERDE);
        respuestaObserver.onCompleted();
    }
	
	
	@Override
    public StreamObserver<DireccionRequest> consumosDireccion(StreamObserver<DireccionReply> respuestaObserver) { //en este último método la comunicación es Client Streaming, en la que el cliente envía un flujo o stream de mensajes y el servidor responde con una única respuesta
    
        //devolvemos un observador que va a escuchar las múlitples peticiones del cliente
        return new StreamObserver<DireccionRequest>() {
        	int totalSolares = 0; 

            @Override
            public void onNext(DireccionRequest solicitud) {
                String direccionBuscada = solicitud.getDireccion();
                v.traza(" [ >>> Servidor ] Buscando dirección: " + direccionBuscada, Ventana.VERDE);
                
                synchronized (MonitorizacionServicio.this) { //aquí lo que hacemos es proteger el ArrayList compartido con el cerrojo
                    for (int i = 0; i < historialConsumos.size(); i++) {
                    	ConsumoRequest c = historialConsumos.get(i);
                    	
                    	if (c.getDireccion().equals(direccionBuscada) && c.getSolar() == true) {
                    		totalSolares++;
                    		break;
                    	}
                    }
                }
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {//una vez el cliente termine de enviar mensajes     
            	//construimos la respuesta
            	DireccionReply respuesta = DireccionReply.newBuilder().setTotal(totalSolares).build();
                
                respuestaObserver.onNext(respuesta);
                
                respuestaObserver.onCompleted(); //enviamos y cerramos el canal al terminar
            }
        };
    }
	
}
