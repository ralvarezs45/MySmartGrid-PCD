package energy;

public class ConsumoEstado { //tarea 4: bloqueo selectivo por consumo (el hilo productor debe bloquearse esperanod a su propio consumo)
    private Consumo consumo;
    private boolean procesado;

    public ConsumoEstado(Consumo consumo) {
        this.consumo = consumo;
        this.procesado = false;
    }

    public Consumo getConsumo() {
        return consumo;
    }

    public synchronized void esperarProcesado() {
        while (!procesado) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void notificarProcesado() {
        procesado = true;
        notifyAll();
    }
}