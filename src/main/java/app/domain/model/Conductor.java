package app.domain.model;

import app.domain.model.enums.EstadoConductor;
import app.domain.model.enums.TripPhase;

import java.util.List;
import java.util.Random;

public class Conductor {
    private int id;
    private List<Zona> rutaAsignada;
    private String nombreCompleto;
    private String placaVehiculo;
    private EstadoConductor estado;
    private Integer zonaActualId; // Se usa Integer para permitir valores nulos (null)

    // --- Atributos para la simulación de movimiento ---
    private Coordenada posicionActual; // Posición X,Y en el mapa en tiempo real
    private Zona zonaOrigenViaje;
    private Zona zonaDestinoViaje;
    private long tiempoInicioViajeMs; // System.currentTimeMillis() al empezar un tramo
    private long duracionViajeMs;     // Duración total calculada para el tramo

    // --- Atributos para el ciclo de vida del viaje ---
    private TripPhase tripPhase = TripPhase.NONE;
    private long waitStartTimeMs;
    private int viajesRealizados;
    
    // --- Fin de atributos de simulación ---


    public Conductor(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
        this.placaVehiculo = generarPlacaAutomatica(); // Se llama al generador automático
        this.estado = EstadoConductor.INACTIVO; // Por defecto, un nuevo conductor está inactivo.
    }

    public Conductor(int id, String nombreCompleto, String placaVehiculo, EstadoConductor estado, Integer zonaActualId) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.placaVehiculo = placaVehiculo;
        this.estado = estado;
        this.zonaActualId = zonaActualId;
        this.viajesRealizados = 0; // Inicializamos en 0
    }

    // --- Getters y Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getPlacaVehiculo() {
        return placaVehiculo;
    }

    public void setPlacaVehiculo(String placaVehiculo) {
        this.placaVehiculo = placaVehiculo;
    }

    public EstadoConductor getEstado() {
        return estado;
    }

    public void setEstado(EstadoConductor estado) {
        this.estado = estado;
    }

    public Integer getZonaActualId() {
        return zonaActualId;
    }

    public void setZonaActualId(Integer zonaActualId) {
        this.zonaActualId = zonaActualId;
    }

    public List<Zona> getRutaAsignada() {
        return rutaAsignada;
    }

    public void setRutaAsignada(List<Zona> rutaAsignada) {
        this.rutaAsignada = rutaAsignada;
    }

    // --- Getters y Setters para la simulación ---

    public Coordenada getPosicionActual() {
        return posicionActual;
    }

    public void setPosicionActual(Coordenada posicionActual) {
        this.posicionActual = posicionActual;
    }

    public Zona getZonaOrigenViaje() {
        return zonaOrigenViaje;
    }

    public Zona getZonaDestinoViaje() {
        return zonaDestinoViaje;
    }

    public long getTiempoInicioViajeMs() {
        return tiempoInicioViajeMs;
    }

    public long getDuracionViajeMs() {
        return duracionViajeMs;
    }

    public TripPhase getTripPhase() {
        return tripPhase;
    }

    public void setTripPhase(TripPhase tripPhase) {
        this.tripPhase = tripPhase;
    }

    public long getWaitStartTimeMs() {
        return waitStartTimeMs;
    }

    public void setWaitStartTimeMs(long waitStartTimeMs) {
        this.waitStartTimeMs = waitStartTimeMs;
    }

    public int getViajesRealizados() {
        return viajesRealizados;
    }

    public void setViajesRealizados(int viajesRealizados) {
        this.viajesRealizados = viajesRealizados;
    }
    
    /**
     * Inicia un nuevo tramo de viaje entre dos zonas.
     * @param origen La zona de partida.
     * @param destino La zona de llegada.
     * @param duracionMs La duración total que tardará el viaje en milisegundos.
     */
    public void iniciarTramo(Zona origen, Zona destino, long duracionMs) {
        this.zonaOrigenViaje = origen;
        this.zonaDestinoViaje = destino;
        this.tiempoInicioViajeMs = System.currentTimeMillis();
        this.duracionViajeMs = duracionMs;
        // --- ¡CORRECCIÓN! ---
        // Ya no se cambia el estado aquí. El estado OCUPADO solo se asigna
        // cuando un viaje REAL es solicitado, no para el movimiento de simulación.
    }

    @Override
    public String toString() {
        return nombreCompleto + " (" + placaVehiculo + ")";
    }

    private static String generarPlacaAutomatica() {
        Random random = new Random();
        StringBuilder placa = new StringBuilder();

        // Genera 3 letras aleatorias
        for (int i = 0; i < 3; i++) {
            char letra = (char) ('A' + random.nextInt(26));
            placa.append(letra);
        }

        placa.append("-");

        // Genera 3 números aleatorios
        int numero = 100 + random.nextInt(900); // Números entre 100 y 999
        placa.append(numero);

        return placa.toString();
    }
}
