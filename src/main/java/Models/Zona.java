package Models;

public class Zona {
    private String nombre;
    private Zona izquierda;
    private Zona derecha;
    private CircularList conductores;

    public Zona(String nombre) {
        this.nombre = nombre;
        this.conductores = new CircularList();
    }

    public String getNombre() { return nombre; }
    public Zona getIzquierda() { return izquierda; }
    public Zona getDerecha() { return derecha; }
    public void setIzquierda(Zona izquierda) { this.izquierda = izquierda; }
    public void setDerecha(Zona derecha) { this.derecha = derecha; }

    public void agregarConductor(Conductor c) { conductores.add(c); }
    public boolean eliminarConductorPorNombre(String nombre) { return conductores.removeByName(nombre); }
    public void imprimirConductores() {
        System.out.println("Zona " + nombre + ": " + conductores.toString());
    }
}
