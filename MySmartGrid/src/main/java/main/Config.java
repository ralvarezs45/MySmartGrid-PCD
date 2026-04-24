package main;

public final class Config {
    private Config() {}

    // --- Configuracion general ---
    public static final int NUM_OPERADORES_POR_ZONA = 3;
    public static final int NUM_ZONAS = 5;
    
    public static final int NUM_OPERARIOS_RED = 2; //tarea 1 de la versión 3 (definimos cuántos operarios habrá por zona)
    
    // --- Ventanas ---
    public static final int TAMAÑO_VENTANA = 250;

    // Baterias (kWh)
    public static final double CAPACIDAD_BATERIA = 200.0;
    public static final double NIVEL_INICIAL_BATERIA = 120.0;

    // Ficheros
    public static final String FICHERO_CONSUMOS = "consumos5.bin";

    // Generacion de consumos
    public static final int NUM_CONSUMOS_A_GENERAR = 50;

    // Semilla para generacion reproducible
    public static final long SEED = 12345L;
    
    //Versión 6
    //máximo de consumos que se pueden tramitar a la vez en una misma zona
    public static final int MAX_CONSUMOS = 2;
    
    //Versión 7: tarea 2 - definimos una constante para ver si ser lanzan los pedidos de la forma tradicional o con observables (0 = Tradicional, 1 = Observables)
    public static int MODO_LANZAR_PEDIDOS = 2; 
    //MODIFICACIÓN PARA LA VERSIÓN 8 -> SI EL VALOR ES IGUAL A 2, EL LANZAMIENTO SE HACE CON UN EXECUTOR
    
    // Versión 8: tarea E - sincronización de arranque de los operarios (0 = Semáforos, 1 = CyclicBarrier)
    public static int MODO_ARRANQUE_OPERARIOS = 1;
    
}
