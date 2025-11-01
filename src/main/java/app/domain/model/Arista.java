package app.domain.model;

public class Arista {
    private int idZonaA; // ID de la zona origen
    private int idZonaB; // ID de la zona destino
    private double distancia; // El 'peso' de la arista

    public Arista(int idZonaA, int idZonaB, double distancia) {
        this.idZonaA = idZonaA;
        this.idZonaB = idZonaB;
        this.distancia = distancia;
    }

    public int getIdZonaA() {
        return idZonaA;
    }

    public int getIdZonaB() {
        return idZonaB;
    }

    public double getDistancia() {
        return distancia;
    }
}