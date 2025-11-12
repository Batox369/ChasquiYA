package app.domain.model;

import java.util.Random;

public class Conductor {
    private int id;
    private String nombreCompleto;
    private String placaVehiculo;
    private EstadoConductor estado;
    private Integer zonaActualId; // Se usa Integer para permitir valores nulos (null)

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
