package app.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public class Viaje {
    // === Campos de la base de datos ===
    private long id;
    private String estado;
    private int clienteId;
    private Integer conductorId;
    private int zonaOrigenId;
    private int zonaDestinoId;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaCompletado;

    // === Campos del modelo ===
    private Coordenada origen;
    private Coordenada destino;
    private double distanciaMetros;
    private List<Zona> rutaZonas; // 🔹 Nueva propiedad: secuencia de zonas

    // === Constructor principal ===
    public Viaje(Coordenada origen, Coordenada destino) {
        this.origen = origen;
        this.destino = destino;
        this.distanciaMetros = calcularDistanciaMetros();
        this.estado = "PENDIENTE";
        this.fechaSolicitud = LocalDateTime.now();
    }

    // === Constructor extendido (desde BD) ===
    public Viaje(long id, String estado, int clienteId, Integer conductorId,
                 int zonaOrigenId, int zonaDestinoId,
                 LocalDateTime fechaSolicitud, LocalDateTime fechaCompletado,
                 Coordenada origen, Coordenada destino, List<Zona> rutaZonas) {

        this.id = id;
        this.estado = estado;
        this.clienteId = clienteId;
        this.conductorId = conductorId;
        this.zonaOrigenId = zonaOrigenId;
        this.zonaDestinoId = zonaDestinoId;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaCompletado = fechaCompletado;
        this.origen = origen;
        this.destino = destino;
        this.rutaZonas = rutaZonas;
        this.distanciaMetros = calcularDistanciaMetros();
    }

    // === Cálculo de distancia ===
    private double calcularDistanciaMetros() {
        double distanciaPixeles = origen.calcularDistancia(destino);
        return distanciaPixeles * 10;
    }

    // === Métodos auxiliares existentes ===
    public String getDistanciaFormateada() {
        if (distanciaMetros >= 1000) {
            return String.format("%.2f km", distanciaMetros / 1000);
        }
        return String.format("%.0f m", distanciaMetros);
    }

    // === Nuevo método para la ruta ===
    public List<Zona> getRutaZonas() {
        return rutaZonas;
    }

    public void setRutaZonas(List<Zona> rutaZonas) {
        this.rutaZonas = rutaZonas;
    }

    // === Getters y setters ===
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public Integer getConductorId() { return conductorId; }
    public void setConductorId(Integer conductorId) { this.conductorId = conductorId; }

    public int getZonaOrigenId() { return zonaOrigenId; }
    public void setZonaOrigenId(int zonaOrigenId) { this.zonaOrigenId = zonaOrigenId; }

    public int getZonaDestinoId() { return zonaDestinoId; }
    public void setZonaDestinoId(int zonaDestinoId) { this.zonaDestinoId = zonaDestinoId; }

    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public LocalDateTime getFechaCompletado() { return fechaCompletado; }
    public void setFechaCompletado(LocalDateTime fechaCompletado) { this.fechaCompletado = fechaCompletado; }

    public Coordenada getOrigen() { return origen; }
    public void setOrigen(Coordenada origen) { this.origen = origen; }

    public Coordenada getDestino() { return destino; }
    public void setDestino(Coordenada destino) { this.destino = destino; }

    public double getDistanciaMetros() { return distanciaMetros; }
    public void setDistanciaMetros(double distanciaMetros) { this.distanciaMetros = distanciaMetros; }
}
