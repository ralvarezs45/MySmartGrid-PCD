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
        
        for (int i = 0; i < Config.NUM_ZONAS; i++) {
            red.getZona(i).getCentroControl().detenerOperarios();
            red.getZona(i).getBateria().detenerCarga();
        }
        
        red.imprimeAuditoria(); //una vez terminan todos los hilos, se imprime la auditoría cuando esté completamente hecho el trabajo
        
        //Implementación de la versión 4: 
        
        //imprimir los consumos menores a 5kWh
        System.out.println("Consumos con kwh < 5 para " + Config.FICHERO_CONSUMOS);
        
        consumos.stream()
        		.parallel()
                .filter(c -> c.getTotalKWh() < 5) //se queda solamente con los elementos con menos de 5 kWh
                .forEach(c -> System.out.println(c.getIdConsumo())); //para cada uno de los elementos lo imprimimos
        
        
        consumos.stream()
                .parallel()
                .map(c -> c.getTotalKWh())
                .max((a, b) -> Double.compare(a, b))
                .ifPresent(max -> System.out.println("Consumo más alto: " + max)); //mostramos el consumo más alto
        
        
        //"Sagitario, 24"
        if (consumos.stream()
        		.parallel()
        		.anyMatch(c -> c.getDireccion().equals("Sagitario, 24"))) {
            System.out.println("Encontrado"); //no lo muestra porque no lo encuentra
        }

        //"Berna, 11"
        if (consumos.stream()
        		.parallel()
        		.anyMatch(c -> c.getDireccion().equals("Berna, 11"))) {
            System.out.println("Encontrado"); //se imprime "Encontrado" porque lo encuentra
        }
    }

}
