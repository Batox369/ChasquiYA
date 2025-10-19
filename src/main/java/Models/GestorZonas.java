package Models;

import java.util.ArrayList;
import java.util.List;

public class GestorZonas {
    private Zona raiz;

    public GestorZonas() { }

    // Inserta una nueva zona (BST por nombre, sin duplicados)
    public void insertarZona(String nombre) {
        raiz = insertarRec(raiz, nombre);
    }

    private Zona insertarRec(Zona nodo, String nombre) {
        if (nodo == null) return new Zona(nombre);
        int cmp = nombre.compareToIgnoreCase(nodo.getNombre());
        if (cmp < 0) {
            nodo.setIzquierda(insertarRec(nodo.getIzquierda(), nombre));
        } else if (cmp > 0) {
            nodo.setDerecha(insertarRec(nodo.getDerecha(), nombre));
        }
        return nodo;
    }

    // Busca una zona por nombre (case-insensitive)
    public Zona buscarZona(String nombre) {
        return buscarRec(raiz, nombre);
    }

    private Zona buscarRec(Zona nodo, String nombre) {
        if (nodo == null) return null;
        int cmp = nombre.compareToIgnoreCase(nodo.getNombre());
        if (cmp == 0) return nodo;
        if (cmp < 0) return buscarRec(nodo.getIzquierda(), nombre);
        return buscarRec(nodo.getDerecha(), nombre);
    }

    // Agrega un conductor a una zona (crea la zona si no existe)
    public void agregarConductorAZona(String nombreZona, Conductor conductor) {
        Zona z = buscarZona(nombreZona);
        if (z == null) {
            insertarZona(nombreZona);
            z = buscarZona(nombreZona);
        }
        z.agregarConductor(conductor);
        System.out.println("✅ " + conductor.getNombre() + " agregado a zona " + nombreZona);
        z.imprimirConductores();
    }

    // Imprime zonas en orden
    public void imprimirZonasInOrden() {
        System.out.println("Zonas (in-order):");
        imprimirInOrden(raiz);
    }

    private void imprimirInOrden(Zona nodo) {
        if (nodo == null) return;
        imprimirInOrden(nodo.getIzquierda());
        System.out.println("• " + nodo.getNombre());
        imprimirInOrden(nodo.getDerecha());
    }

    // Imprime árbol completo con conductores
    public void imprimirArbolYConductores() {
        System.out.println("=== Estado de Zonas y Conductores ===");
        imprimirArbolRec(raiz);
        System.out.println("======================================");
    }

    private void imprimirArbolRec(Zona nodo) {
        if (nodo == null) return;
        imprimirArbolRec(nodo.getIzquierda());
        nodo.imprimirConductores();
        imprimirArbolRec(nodo.getDerecha());
    }

    // 🔹 NUEVO MÉTODO: obtener lista de nombres de zonas
    public List<String> obtenerNombresZonas() {
        List<String> nombres = new ArrayList<>();
        recolectarNombres(raiz, nombres);
        return nombres;
    }

    private void recolectarNombres(Zona nodo, List<String> nombres) {
        if (nodo == null) return;
        recolectarNombres(nodo.getIzquierda(), nombres);
        nombres.add(nodo.getNombre());
        recolectarNombres(nodo.getDerecha(), nombres);
    }
}
