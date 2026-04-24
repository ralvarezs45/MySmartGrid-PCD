package operators;

import energy.ZonaEnergetica;

public class OperarioCarga implements Runnable { //tarea 5: Recarga concurrente de energía. Operarios de carga
    
    private ZonaEnergetica zona;

    public OperarioCarga(ZonaEnergetica zona) {
        this.zona = zona;
    }

    @Override
    public void run() {
    	
    	try {
            //el operario de carga espera aquí hasta que el contador del latch sea 0, garantizando que todos los operarios de red han arrancado
            zona.getLatchArranque().await();
            
            zona.getVentana().traza("Operario de Carga de la zona " + zona.getIdZona() + " comienza su servicio."); //para ver que comienza después de los de red
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    	
        while (true) {
            zona.getBateria().esperarPeticion(); //dormido hasta que necesitacargar = true
            if (zona.getBateria().getFin()) {
                break; 
            }
            zona.getBateria().carga(zona.getBateria().getCapacidadMaxKWh()); //hace esto si le despiertan, es decir, le han pedido cargar
            zona.getVentana().traza("Operario de carga - energía recargada al máximo");
        }
    }
}
