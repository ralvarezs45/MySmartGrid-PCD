package operators;

import java.util.Random;

import energy.Consumo;
import energy.ZonaEnergetica;

public class CentroControl {
    private ZonaEnergetica zona;

    public CentroControl() {
        
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
        double suministrado = zona.getBateria().suministra(total);
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
