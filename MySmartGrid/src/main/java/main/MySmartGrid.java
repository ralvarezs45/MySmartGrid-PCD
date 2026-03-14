package main;

import energy.Consumo;

import energy.RedEnergetica;
import java.util.List;
import java.util.ArrayList;


public class MySmartGrid {
	
    public static void main(String[] args) {
        RedEnergetica red = new RedEnergetica(
                Config.NUM_ZONAS,
                Config.CAPACIDAD_BATERIA,
                Config.NIVEL_INICIAL_BATERIA
        );
        
        List<Consumo> consumos = Consumo.consumosDesdeFichero(Config.FICHERO_CONSUMOS);
        System.out.println("Leidos " + consumos.size() + " consumos desde " + Config.FICHERO_CONSUMOS);
      
        List<Thread> listaHilos = new ArrayList<>(); //creamos una lista de hilos
        
        for (Consumo c:consumos) { //tramitamos los consumos de manera concurrente ahora
        	Tramitacion tarea = new Tramitacion(c, red); //instanciamos la tarea Runnable
        	Thread hilo = new Thread(tarea); //creación del hilo
        	listaHilos.add(hilo); //añadimos cada hilo en la lista
        	hilo.start(); //lo lanzamos
        }
        
        for (Thread hilo : listaHilos) { //para cada hilo existente dentro de la lista
            try {
                hilo.join(); //esperamos a que terminen todos los hilos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        red.imprimeAuditoria(); //una vez terminan todos los hilos, se imprime la auditoría cuando esté completamente hecho el trabajo
    }

}
