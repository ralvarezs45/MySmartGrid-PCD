package main;

import energy.Consumo;


import energy.RedEnergetica;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
      
        //Versión 7: tarea A, creación de dos observables
        Observable<Consumo> observable = Observable.fromIterable(consumos); //definimos primero al observable desde la colección consumos
        
        Observer<Consumo> observer1 = new Observer<Consumo>() { //definimos el primer observador, que es el que calcula la suma de todos los consumos
            double suma = 0.0; 

            @Override
            public void onSubscribe(Disposable d) {}

            @Override
            public void onNext(Consumo c) {
                suma += c.getTotalKWh(); //sumamos cada vez que llega un consumo
            }

            @Override
            public void onError(Throwable e) {}

            @Override
            public void onComplete() {
                //imprimimos el resultado y el hilo cuando termina de leer toda la colección que hay en el observable
                System.out.println("[Observer 1 (suma)] Hilo: " + Thread.currentThread().getName() + " | Suma total calculada: " + String.format("%.2f", suma) + " kWh");
            }
        };
        
        //Ahora tenemos que suscribir a este observador
        observable.subscribeOn(Schedulers.computation()).subscribe(observer1);
        
        //Ahora creamos el observador 2, que imprime los consumos mayores a 20kWh
        observable.subscribeOn(Schedulers.computation())
        	.filter(c -> c.getTotalKWh() > 20.0) //filtramos de la colección los mayores a 20 kWh
        	.subscribe(c -> {
        		System.out.println("[Observer 2 (consumos > 20kWh)] Hilo: " + Thread.currentThread().getName() + " | Consumo >20kWh: " + c.getIdConsumo() + " (" + String.format("%.2f", c.getTotalKWh()) + " kWh)");
        }); //suscribimos con una lambda que implementa el comportamiento del método onNext()
        
        
        
        List<Thread> listaHilos = new ArrayList<>(); //creamos una lista de hilos
        
        //Versión 7: tarea 2 - diferenciamos entre el modo de lanzamiento de los pedidos
        
        
        if (Config.MODO_LANZAR_PEDIDOS == 0) {
        	System.out.println("Lanzando pedidos de forma tradicional");
        	
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
        } else if (Config.MODO_LANZAR_PEDIDOS == 1) { //Implementación de la tarea 2 de la versión 7
            System.out.println("Lanzando pedidos con Observables");
            
            Observable<Consumo> observableLectura = Consumo.consumosDesdeFicheroObservable(Config.FICHERO_CONSUMOS);
            
            observableLectura
            .flatMap(c -> Observable.just(c)
                    .subscribeOn(Schedulers.computation())
                    .map(c2 -> {
                        // creamos y lanzamos el thread
                        Tramitacion tarea = new Tramitacion(c2, red);
                        Thread hilo = new Thread(tarea);
                        
                        //como Schedulers.computation lanza hilos concurrentes,
                        //protegemos la lista echando el cerrojo
                        synchronized(listaHilos) {
                            listaHilos.add(hilo);
                        }
                        
                        hilo.start();
                        return c2; 
                    })
                )
                .blockingSubscribe(); //esperamos a que termine de leer todo el fichero y arranca el thread main
            
            //esperamos a que los hilos Tramitacion terminen 
            for (Thread hilo : listaHilos) { 
                try {
                    hilo.join(); 
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (Config.MODO_LANZAR_PEDIDOS == 2) { //Versión 8 - tarea A - modo executor
        	System.out.println("Lanzando pedidos con Executor");
        	
        	int n = Runtime.getRuntime().availableProcessors(); //guardamos el número de procesadores
        	
        	ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(n); //usamos fixed ya que se pide un número fijo y estricto de hilos en la tarea, catched sería dinámico
        	
        	for (Consumo c : consumos) { //para cada uno de los consumos lo mandamos al pool de threads
                Tramitacion tarea = new Tramitacion(c, red);
                executor.execute(tarea); 
            }
        	
        	executor.shutdown(); //apagamos el executor y esperamos a que termine todo
        	
        	try {
                executor.awaitTermination(1, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        
        for (int i = 0; i < Config.NUM_ZONAS; i++) {
            red.getZona(i).getCentroControl().detenerOperarios();
            red.getZona(i).getBateria().detenerCarga();
            
            red.getZona(i).getBateriaSolar().apagar();
            red.getZona(i).getBateriaEolica().apagar(); //apagamos ambas baterías
        }
        
        
        //Versión 8 - tarea C: mostrar el consumo más alto
        ExecutorService executorConsumoMasAlto = Executors.newSingleThreadExecutor(); //creamos un pool pero para una única tarea
        
        Future<Consumo> futureConsumoMasAlto = executorConsumoMasAlto.submit(new ConsumoMasAlto(consumos)); //enviamos la tarea con Callable y obtenemos el Future
        
        try {
            //obtenemos el resultado (get() bloquea hasta que esté listo, por eso no ponemos el awaitTermination)
            Consumo maximo = futureConsumoMasAlto.get();
            if (maximo != null) {
                System.out.println("\n Identificador del consumo más alto: " + maximo.getIdConsumo() + " | Valor del consumo: " + String.format("%.2f", maximo.getTotalKWh()) + " kWh");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	executorConsumoMasAlto.shutdown();
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
