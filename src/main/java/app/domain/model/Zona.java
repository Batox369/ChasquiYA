package app.domain.model;

import app.domain.structures.CircularList;

public class Zona {
    private int id;
    private String nombre;

    // Conexiones lógicas
    private Zona izquierda;
    private Zona derecha;
    private CircularList conductores;

    // Coordenadas espaciales (para el mapa)
    private double latitud;   // -50 a 50
    private double longitud;  // -50 a 50
    private double xMapa;     // coordenada escalada (en píxeles)
    private double yMapa;
    private boolean visible;
    private int numeroViajes;

    public Zona(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.conductores = new CircularList();
        this.visible = true; // Por defecto, una zona es visible
        this.numeroViajes = 0;
    }

    public Zona(int id, String nombre, double latitud, double longitud, boolean visible) {
        this(id, nombre);
        setLatitud(latitud);
        setLongitud(longitud);
        this.visible = visible;
    }

    public Zona(int id, String nombre, double latitud, double longitud) {
        this(id, nombre, latitud, longitud, true);
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }

    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public boolean isVisible() { return visible; }
    public int getNumeroViajes() { return numeroViajes; }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }


    public void setNumeroViajes(int numeroViajes) {
        this.numeroViajes = numeroViajes;
    }

    public void setCoordenadasMapa(double x, double y) {
        this.xMapa = x;
        this.yMapa = y;
    }

    @Override
    public String toString() {
        return nombre + " [" + latitud + ", " + longitud + "]";
    }
}
