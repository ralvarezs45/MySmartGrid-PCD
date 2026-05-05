package main;

import java.util.List;
import java.util.concurrent.Callable;

import energy.Consumo;

//Versión 8 - tarea C, usar Callable para mostrar el consumo más alto después de que todos los tramitadores acaben, pero antes de imprimir la auditoría.

/**
 * private final HashMap<Integer, List<String>> rechazadosPorZona = new HashMap<>();
 * if (!exito) {
            synchronized (this) { // Cerrojo para proteger la escritura
                if (!rechazadosPorZona.containsKey(zona)) {
                    rechazadosPorZona.put(zona, new ArrayList<>());
                }
                rechazadosPorZona.get(zona).add(id);
            }
            System.out.println("[Servidor 9095] RECHAZADO: " + id + " (Importe: " + importe + ")");
        } else {
            System.out.println("[Servidor 9095] ACEPTADO: " + id);
        }
*/

public class ConsumoMasAlto implements Callable<Consumo> {//el objeto que vamos a devolver es de tipo 'Consumo'
    
    private List<Consumo> consumos;

    public ConsumoMasAlto(List<Consumo> consumos) {
        this.consumos = consumos;
    }

    @Override
    public Consumo call() throws Exception {
        return consumos.stream()
                .reduce((a, b) -> a.getTotalKWh() < b.getTotalKWh() ? b : a) //usamos .reduce para calcular el consumo más alto
                .orElse(null);
    }
}

