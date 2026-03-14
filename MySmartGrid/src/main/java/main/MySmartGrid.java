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
      
        // Tramitamos los consumos de manera secuencial
        String resultado;
        for (Consumo c:consumos) {
        	resultado = red.getZona(c.getZona()).tramitarConsumo(c);
            red.getZona(c.getZona()).getVentana().traza (c.getIdConsumo()+ " - Tramitado: "+resultado);
        }
        red.imprimeAuditoria();
    }

}
