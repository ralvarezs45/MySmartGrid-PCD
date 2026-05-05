package servicio;

import grpc.FiltradoGrpc;
import grpc.FiltroOuterClass.FiltroReply;
import grpc.FiltroOuterClass.FiltroRequest;
import io.grpc.stub.StreamObserver;
import pcd.util.Ventana;

public class FiltroServicio extends FiltradoGrpc.FiltradoImplBase {

    private final Ventana v;

    public FiltroServicio(Ventana v) {
        this.v = v;
    }

    @Override
    public StreamObserver<FiltroRequest> filtrar(StreamObserver<FiltroReply> respuestaObserver) {
        
        //esto es lo que escucha el servidor
        return new StreamObserver<FiltroRequest>() {

            @Override
            public void onNext(FiltroRequest solicitud) {
                //extraemos los datos de la solicitud del cliente
                String id = solicitud.getIdConsumo();
                int kwh = solicitud.getTotalKWh();

                //filtrar los >= 5
                if (kwh >= 5) {
                    v.traza(" [ >>> Servidor ] Consumo " + id + " (" + kwh + " kWh) -> ACEPTADO", Ventana.AZUL);
                    
                    // Construimos y enviamos la respuesta 
                    FiltroReply respuesta = FiltroReply.newBuilder().setIdConsumo(id).setTotalKWh(kwh).build();
                    
                    respuestaObserver.onNext(respuesta);
                    
                } else {
                    // Si es menor que 5, descartado
                    v.traza(" [ >>> Servidor ] Consumo " + id + " (" + kwh + " kWh) -> DESCARTADO", Ventana.ROJO);
                }
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                //cuando el cliente termina de enviar
                v.traza(" [ >>> Servidor ] El cliente ha finalizado el flujo", Ventana.AZUL);
                respuestaObserver.onCompleted();
            }
        };
    }
}
