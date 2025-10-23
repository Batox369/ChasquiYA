package app.domain.service;

import app.domain.model.Conductor;
import app.domain.model.Zona;

import java.util.ArrayList;
import java.util.List;

public class GestorZonas {
    private Zona raiz;

    public GestorZonas() { }

    // Inserta una nueva zona en el ABB usando el ID como criterio
    public void insertarZona(int id, String nombre) {
        raiz = insertarRec(raiz, id, nombre);
    }

    private Zona insertarRec(Zona nodo, int id, String nombre) {
        if (nodo == null) return new Zona(id, nombre);
        if (id < nodo.getId()) {
            nodo.setIzquierda(insertarRec(nodo.getIzquierda(), id, nombre));
        } else if (id > nodo.getId()) {
            nodo.setDerecha(insertarRec(nodo.getDerecha(), id, nombre));
        }
        return nodo;
    }

    // Buscar zona por ID
    public Zona buscarZonaPorId(int id) {
        return buscarRec(raiz, id);
    }

    private Zona buscarRec(Zona nodo, int id) {
        if (nodo == null) return null;
        if (id == nodo.getId()) return nodo;
        if (id < nodo.getId()) return buscarRec(nodo.getIzquierda(), id);
        return buscarRec(nodo.getDerecha(), id);
    }

    // Buscar zona por nombre (solo para conveniencia)
    public Zona buscarZonaPorNombre(String nombre) {
        return buscarPorNombreRec(raiz, nombre);
    }

    private Zona buscarPorNombreRec(Zona nodo, String nombre) {
        if (nodo == null) return null;
        if (nodo.getNombre().equalsIgnoreCase(nombre)) return nodo;
        Zona izq = buscarPorNombreRec(nodo.getIzquierda(), nombre);
        if (izq != null) return izq;
        return buscarPorNombreRec(nodo.getDerecha(), nombre);
    }

    // Agrega un conductor a una zona existente (por ID)
    public void agregarConductorAZona(String nombreZona, Conductor conductor) {
        Zona z = buscarZonaPorNombre(nombreZona);
        if (z == null) {
            System.out.println("⚠ No existe zona con nombre " + nombreZona);
            return;
        }
        z.agregarConductor(conductor);
        System.out.println("✅ " + conductor.getNombre() + " agregado a zona " + z.getNombre());
    }

    // Imprimir zonas en orden
    public void imprimirZonasInOrden() {
        System.out.println("Zonas (in-order por ID):");
        imprimirInOrden(raiz);
    }

    private void imprimirInOrden(Zona nodo) {
        if (nodo == null) return;
        imprimirInOrden(nodo.getIzquierda());
        System.out.println("• ID: " + nodo.getId() + " - " + nodo.getNombre());
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

    // Obtener lista de nombres (para el combo visual)
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
