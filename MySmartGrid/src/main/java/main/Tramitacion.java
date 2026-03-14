package main;

import energy.Consumo;
import energy.RedEnergetica;

public class Tramitacion implements Runnable {
    
    private Consumo consumo;
    private RedEnergetica red;

    public Tramitacion(Consumo consumo, RedEnergetica red) { //constructor
        this.consumo = consumo;
        this.red = red;
    }

    @Override
    public void run() { //método para ejecutar el hilo de forma concurrente
        String resultado = red.getZona(consumo.getZona()).tramitarConsumo(consumo);
        red.getZona(consumo.getZona()).getVentana().traza(consumo.getIdConsumo() + " - Tramitado: " + resultado);
    }
}
