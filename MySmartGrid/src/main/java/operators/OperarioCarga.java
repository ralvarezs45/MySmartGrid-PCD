package operators;

import energy.ZonaEnergetica;

public class OperarioCarga implements Runnable { //tarea 5: Recarga concurrente de energía. Operarios de carga
    
    private ZonaEnergetica zona;

    public OperarioCarga(ZonaEnergetica zona) {
        this.zona = zona;
    }

    @Override
    public void run() {
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
