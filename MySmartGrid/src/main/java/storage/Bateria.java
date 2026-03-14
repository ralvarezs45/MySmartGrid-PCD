package storage;

import pcd.util.Ventana;

public class Bateria { //tarea 5 de la versión 3: recarga concurrente de energía (Operarios de red: si piden energía y no hay, se quedan dormidos y piden ayuda; Operarios de carga: despiertan si alguien pide ayuda)
    private final double capacidadMaxKWh;
    private double nivelActualKWh;
    
    private boolean necesitacargar;
    Ventana v;
    
    private boolean fin = false;

    public Bateria(double capacidadMaxKWh, double nivelInicialKWh) {
        if (capacidadMaxKWh <= 0) throw new IllegalArgumentException("capacidadMaxKWh debe ser > 0");
        if (nivelInicialKWh < 0) throw new IllegalArgumentException("nivelInicialKWh debe ser >= 0");
        this.capacidadMaxKWh = capacidadMaxKWh;
        this.nivelActualKWh = Math.min(nivelInicialKWh, capacidadMaxKWh);
        this.necesitacargar = false;
    }

    
    public synchronized double getCapacidadMaxKWh() { return capacidadMaxKWh; }
    public synchronized double getNivelActualKWh() { return nivelActualKWh; }

    public synchronized boolean puedeSuministrar(double kWh) {
        return kWh <= nivelActualKWh;
    }
    
    public synchronized boolean getFin() {
        return fin;
    }

    public synchronized double suministra(double kWh) {
    	while (nivelActualKWh < kWh) {
            necesitacargar = true;
            notifyAll(); //despierta al operario de carga 
            try {
                wait(); //el operario de red duerme hasta que haya energía
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        nivelActualKWh -= kWh;
        return kWh;
    }
    
    public synchronized void esperarPeticion() {
        while (!necesitacargar && !fin) {
            try {
                wait(); //duerme si nadie necesita carga
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public synchronized void detenerCarga() {
        fin = true;
        notifyAll(); //despierta al operario de carga
    }

    public synchronized void carga(double kWh) {
        nivelActualKWh = Math.min(capacidadMaxKWh, nivelActualKWh + kWh);
        necesitacargar = false; //ya no es neceasrio cargar
        notifyAll(); // despierta a los operarios de red que estaban esperando energía
    }
    
    public void setVentana (Ventana _v) {
    	v=_v;
    }

    @Override
    public String toString() {
        return "Bateria{capacidadMaxKWh=" + capacidadMaxKWh + ", nivelActualKWh=" + nivelActualKWh + "}";
    }
}
