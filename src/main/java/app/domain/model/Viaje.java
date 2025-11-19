package app.domain.model;

import java.util.List;
import java.util.Date;

public class Viaje {
    // === Campos de la base de datos ===
    private long id;
    private String estado;
    private int clienteId;
    private Integer conductorId;
    private int usuarioId; // Para el historial
    private double precio; // Para el historial
    private int zonaOrigenId; // <-- NUEVO
    private int zonaDestinoId; // <-- NUEVO
    private Date fecha; // Para el historial

    // === Campos del modelo ===
    private Coordenada origen;
    private Coordenada destino;
    private String nombreOrigen;
    private String nombreDestino;
    private double distanciaMetros;
    private List<Zona> rutaZonas; // 🔹 Nueva propiedad: secuencia de zonas

    // === Constructor principal ===
    public Viaje(Zona origen, Zona destino, String nombreOrigen, String nombreDestino) {
        // --- ¡CORRECCIÓN! ---
        // Se crea una nueva instancia de Coordenada a partir de los datos de la Zona.
        this.origen = new Coordenada(origen.getLongitud(), origen.getLatitud());
        this.destino = new Coordenada(destino.getLongitud(), destino.getLatitud());
        this.nombreOrigen = nombreOrigen;
        this.nombreDestino = nombreDestino;
        this.zonaOrigenId = origen.getId(); // Ahora es seguro y directo
        this.zonaDestinoId = destino.getId(); // Ahora es seguro y directo
        this.estado = "PENDIENTE";
        this.fecha = new Date();
    }

    public Viaje(int idOrigen, String nombreOrigen, double lonOrigen, double latOrigen,
                 int idDestino, String nombreDestino, double lonDestino, double latDestino,
                 double distanciaMetros) {
        this.fecha = new Date();
        this.origen = new Coordenada(lonOrigen, latOrigen);
        this.destino = new Coordenada(lonDestino, latDestino);
        this.nombreOrigen = nombreOrigen;
        this.nombreDestino = nombreDestino;
        this.zonaOrigenId = idOrigen;
        this.zonaDestinoId = idDestino;
        this.distanciaMetros = distanciaMetros; // <-- ¡CORRECCIÓN! Asegurarse de que se asigna.
    }

    // === Métodos auxiliares existentes ===
    public String getDistanciaFormateada() {
        if (distanciaMetros == 0) return "-- km";
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

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public Integer getConductorId() { return conductorId; }
    public void setConductorId(Integer conductorId) { this.conductorId = conductorId; }

    public int getZonaOrigenId() { return zonaOrigenId; }
    public void setZonaOrigenId(int zonaOrigenId) { this.zonaOrigenId = zonaOrigenId; }

    public int getZonaDestinoId() { return zonaDestinoId; }
    public void setZonaDestinoId(int zonaDestinoId) { this.zonaDestinoId = zonaDestinoId; }

    public Coordenada getOrigen() { return origen; }
    public void setOrigen(Coordenada origen) { this.origen = origen; }

    public Coordenada getDestino() { return destino; }
    public void setDestino(Coordenada destino) { this.destino = destino; }

    public double getDistanciaMetros() { return distanciaMetros; }
    public void setDistanciaMetros(double distanciaMetros) { this.distanciaMetros = distanciaMetros; }

    public String getNombreOrigen() { return nombreOrigen; }
    public void setNombreOrigen(String nombreOrigen) { this.nombreOrigen = nombreOrigen; }

    public String getNombreDestino() { return nombreDestino; }
    public void setNombreDestino(String nombreDestino) { this.nombreDestino = nombreDestino; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
}
