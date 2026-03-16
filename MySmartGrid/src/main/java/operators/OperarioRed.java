package operators;

import energy.Consumo;
import energy.ConsumoEstado;
import energy.ZonaEnergetica;

//tarea 1 de la versión 3 (implementamos la clase OperarioRed, que será un hilo que continuamnete recoge y procesa consumos)
public class OperarioRed implements Runnable {
    
    private ZonaEnergetica zona;
    private int idOperario;

    public OperarioRed(ZonaEnergetica zona, int idOperario) {
        this.zona = zona;
        this.idOperario = idOperario;
    }

    @Override
    public void run() {
    	try {
            zona.getArranque().acquire(); //acquire para bloquear porque el semáforo está inicialmente a 0 (V6)
        } catch (InterruptedException e) {
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
                    estadoAsignado.notificarProcesado();
                } catch (InterruptedException e) {
                	e.printStackTrace();
                } finally {
                	zona.getSemaforoCapacidad().release(); //siempre liberamos al terminar
                }
                
            }
        }
    }
}