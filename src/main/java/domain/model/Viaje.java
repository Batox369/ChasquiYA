package domain.model;

public class Viaje {
    private Coordenada origen;
    private Coordenada destino;
    private double distanciaMetros;
    private String estado; // "pendiente", "en_proceso", "completado"

    public Viaje(Coordenada origen, Coordenada destino) {
        this.origen = origen;
        this.destino = destino;
        this.distanciaMetros = calcularDistanciaMetros();
        this.estado = "pendiente";
    }

    private double calcularDistanciaMetros() {
        // Por defecto, 1 píxel = 10 metros (puedes ajustarlo según tu mapa)
        double distanciaPixeles = origen.calcularDistancia(destino);
        return distanciaPixeles * 10; // Factor de conversión configurable
    }

    public Coordenada getOrigen() {
        return origen;
    }

    public Coordenada getDestino() {
        return destino;
    }

    public double getDistanciaMetros() {
        return distanciaMetros;
    }

    public String getDistanciaFormateada() {
        if (distanciaMetros >= 1000) {
            return String.format("%.2f km", distanciaMetros / 1000);
        }
        return String.format("%.0f m", distanciaMetros);
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}