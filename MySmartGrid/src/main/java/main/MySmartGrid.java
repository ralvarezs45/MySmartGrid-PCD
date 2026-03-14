package main;

import energy.Consumo;
import energy.RedEnergetica;
import java.util.List;

public class MySmartGrid {
	
    public static void main(String[] args) {
        RedEnergetica red = new RedEnergetica(
                Config.NUM_ZONAS,
                Config.CAPACIDAD_BATERIA,
                Config.NIVEL_INICIAL_BATERIA
        );
        
        List<Consumo> consumos = Consumo.consumosDesdeFichero(Config.FICHERO_CONSUMOS);
        System.out.println("Leidos " + consumos.size() + " consumos desde " + Config.FICHERO_CONSUMOS);
      
        String resultado;
        for (Consumo c:consumos) { //tramitamos los consumos de manera concurrente ahora
        	Tramitacion tarea = new Tramitacion(c, red); //instanciamos la tarea Runnable
        	Thread hilo = new Thread(tarea); //creación del hilo
        	hilo.start(); //lo lanzamos
        }
        red.imprimeAuditoria(); //se imprime antes de que terminen los hilos
    }

}
