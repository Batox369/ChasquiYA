package Models;

import java.awt.Point;

public class Coordenada {
    private Point punto;
    private double x; // Coordenada real en el mapa
    private double y; // Coordenada real en el mapa

    public Coordenada(Point punto, double zoom, int offsetX, int offsetY) {
        this.punto = punto;
        // Convertir coordenadas de pantalla a coordenadas del mapa
        this.x = (punto.x - offsetX) / zoom;
        this.y = (punto.y - offsetY) / zoom;
    }

    public Point getPunto() {
        return punto;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    // Calcular distancia euclidiana entre dos coordenadas (en píxeles)
    public double calcularDistancia(Coordenada otra) {
        double dx = this.x - otra.x;
        double dy = this.y - otra.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Convertir coordenada a punto en pantalla con zoom y offset actuales
    public Point toPantallaPoint(double zoom, int offsetX, int offsetY) {
        int screenX = (int)(x * zoom + offsetX);
        int screenY = (int)(y * zoom + offsetY);
        return new Point(screenX, screenY);
    }
}
