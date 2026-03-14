package main;

import energy.Consumo;
import energy.RedEnergetica;
import energy.ConsumoEstado;

public class Tramitacion implements Runnable {
    
    private Consumo consumo;
    private RedEnergetica red;

    public Tramitacion(Consumo consumo, RedEnergetica red) { //constructor
        this.consumo = consumo;
        this.red = red;
    }

    //(VERSIÓN 3 - TAREA 2 -> modificar de tal forma que ya no procese consumo por sí misma, sino que deposite el consumo en el sistema, utilizar wait() (esperar) y continuar cuando se llame a notifyAll())
    @Override
    public void run() { //método para ejecutar el hilo de forma concurrente
    	ConsumoEstado estado = new ConsumoEstado(consumo);
        red.getZona(consumo.getZona()).getCentroControl().depositarConsumo(estado);
        estado.esperarProcesado();
        }
}
