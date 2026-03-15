package operators;

import java.util.Random;
import java.util.LinkedList;
import java.util.Queue;
import energy.Consumo;
import energy.ZonaEnergetica;
import energy.ConsumoEstado;

public class CentroControl {
    private ZonaEnergetica zona;
    private Queue<ConsumoEstado> consumosPendientes;
    private boolean fin = false;

    public CentroControl() {
    	this.consumosPendientes = new LinkedList<>();
    }
    
    public synchronized void depositarConsumo(ConsumoEstado ce) {
        consumosPendientes.add(ce);
        notifyAll();
    }

    public synchronized ConsumoEstado recogerConsumo() {
        while (consumosPendientes.isEmpty() && !fin) {
            try {
                wait(); //el operario se bloquea si no hay consumos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        if (consumosPendientes.isEmpty() && fin) {
            return null;
        }
        
        return consumosPendientes.poll(); //saca y devuelve el primer consumo de la cola
    }
    
    
    public synchronized void detenerOperarios() {
        fin = true;
        notifyAll(); //despierta a los operarios de red dormidos
    }
    
    
    public void setZona(ZonaEnergetica zona) {
    	this.zona=zona;
    }

    public int getIdZona() { return zona.getIdZona(); }

    public String enviarTrabajo(Consumo c) {

    	Random r = new Random();
        // Simulamos un tiempo de tramitación del consumo
        try {
			Thread.sleep(r.nextInt(200));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        double total = c.getTotalKWh();
        double suministrado = 0.0;
        
        //VERSIÓN 5 tramitación diferenciada de energía ---
        for (energy.Demanda d : c.getDemandas()) {
            String tipo = d.getIdTipo();
            double cantidad = d.getKWh();
            
            if (tipo.equals("SOLAR")) {
                zona.getBateriaSolar().retirar(cantidad); //usa el monitor explícito (bloquea si no hay)
                suministrado += cantidad; 
                
            } else if (tipo.equals("EOLICA")) {
                zona.getBateriaEolica().retirar(cantidad); //usa el monitor explícito (bloquea si no hay)
                suministrado += cantidad;
                
            } else {
                suministrado += zona.getBateria().suministra(cantidad);
            }
        }
        
        //Versiones 4 y anteriores
       /*
        double total = c.getTotalKWh();
        double suministrado = zona.getBateria().suministra(total);
        
        */
        zona.getCuenta().anotaConsumo(suministrado);
        
        String resultado = (suministrado >= total)
                ? "OK: suministrados " + fmt(total) + " kWh"
                : "PARCIAL: suministrados " + fmt(suministrado) + " kWh (faltan " + fmt(total - suministrado) + " kWh)";
    	traza ("Trabajo completado para: "+c.getIdConsumo());
        return resultado;
    }
    
    private String fmt(double x) {
        return String.format(java.util.Locale.ROOT, "%.2f", x);
    }

    public void traza(String msg) {
        System.out.println("[CentroControl Z" + zona.getIdZona() + "] " + msg);
    }
}
