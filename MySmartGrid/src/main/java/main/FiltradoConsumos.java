package main;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

import energy.Consumo;

//Versión 8 - tarea D: filtrar la lista de consumos para quedarnos solo con los que superen los 20 kWh

@SuppressWarnings("serial")
public class FiltradoConsumos extends RecursiveTask<List<Consumo>> {
    
    private List<Consumo> lista;
    private int inicio;
    private int fin;
    private static final int TRIVIAL = 10; 

    public FiltradoConsumos(List<Consumo> lista, int inicio, int fin) {
        this.lista = lista;
        this.inicio = inicio;
        this.fin = fin;
    }

    @Override
    protected List<Consumo> compute() {
        if ((fin - inicio) < TRIVIAL) { //si ese trozo de la lista tiene menos de 10 elementos
            return lista.subList(inicio, fin).stream()
                    .filter(c -> c.getTotalKWh() > 20.0)
                    .collect(Collectors.toList());
        } 
        //si hay 10 o más elementos, partimos por la mitad.
        else {
            int mitad = (inicio + fin) / 2;
            
            FiltradoConsumos tarea1 = new FiltradoConsumos(lista, inicio, mitad);
            FiltradoConsumos tarea2 = new FiltradoConsumos(lista, mitad, fin);
            
            invokeAll(tarea1, tarea2);
            
            List<Consumo> resultadoFinal = new ArrayList<>();
            resultadoFinal.addAll(tarea1.join());
            resultadoFinal.addAll(tarea2.join());
            
            return resultadoFinal;
        }
    }
}