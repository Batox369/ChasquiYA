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

    // --- ¡SOLUCIÓN! Constructor sobrecargado que asume 'visible = true' ---
    public Zona(int id, String nombre, double latitud, double longitud) {
        this(id, nombre, latitud, longitud, true);
    }

    // --- Getters y Setters ---
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

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setNumeroViajes(int numeroViajes) {
        this.numeroViajes = numeroViajes;
    }

    public double getXMapa() { return xMapa; }
    public double getYMapa() { return yMapa; }
    public void setCoordenadasMapa(double x, double y) {
        this.xMapa = x;
        this.yMapa = y;
    }

    // --- Enlaces lógicos ---
    public Zona getIzquierda() { return izquierda; }
    public Zona getDerecha() { return derecha; }
    public void setIzquierda(Zona izquierda) { this.izquierda = izquierda; }
    public void setDerecha(Zona derecha) { this.derecha = derecha; }

    // --- Conductores ---
    public void agregarConductor(Conductor c) { conductores.add(c); }
    public boolean eliminarConductorPorNombre(String nombre) { return conductores.removeByName(nombre); }

    public void imprimirConductores() {
        System.out.println("Zona " + id + " (" + nombre + "): " + conductores.toString());
    }

    @Override
    public String toString() {
        return nombre + " [" + latitud + ", " + longitud + "]";
    }
}
