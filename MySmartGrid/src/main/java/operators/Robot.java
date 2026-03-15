package operators;

import energy.ZonaEnergetica;
import storage.tipoBateria;

public class Robot implements Runnable {
    
    private ZonaEnergetica zona;
    private tipoBateria bateria;
    private String tipoEnergia;

    public Robot(ZonaEnergetica zona, tipoBateria bateria, String tipoEnergia) {
        this.zona = zona;
        this.bateria = bateria;
        this.tipoEnergia = tipoEnergia;
    }

    @Override
    public void run() {
        while (true) { //bucle que produce energía de forma indefinida (1kWh por producción)
            bateria.depositar(1.0);
        }
    }
}
