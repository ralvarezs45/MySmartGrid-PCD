package storage;

import java.util.concurrent.locks.*; //primera tarea de la versión 5

public class tipoBateria {
    
    private double capacidadMax;
    private double nivelActual;

    private final Lock monitor = new ReentrantLock();
    private final Condition lleno = monitor.newCondition();
    private final Condition vacio = monitor.newCondition();

    public tipoBateria(double capacidadMaxKWh) {
        this.capacidadMax = capacidadMaxKWh;
        this.nivelActual = 0.0; 
    }

    public void depositar(double cantidad) { //método para que los robots depositen
        monitor.lock();
        try {
            while (nivelActual + cantidad > capacidadMax) {//si no hay espacio suficiente para depositar, el hilo debe bloquearse
                lleno.await(); 
            }
            nivelActual += cantidad;
            
            vacio.signal();
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            monitor.unlock(); 
        }
    }

    public void retirar(double cantidad) {//método para los Operarios de Red
        monitor.lock();
        try {
            while (nivelActual < cantidad) {// si no hay suficiente energía para retirar, el hilo debe bloquearse
                vacio.await();
            }
            nivelActual -= cantidad;
            
            lleno.signal();
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            monitor.unlock(); 
        }
    }

    public double getNivelActualKWh() {//devuelve el nivelActual de batería
        monitor.lock();
        try {
            return nivelActual;
        } finally {
            monitor.unlock();
        }
    }
}
