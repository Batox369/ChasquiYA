package app.domain.structures;

/**
 * Implementación de una lista enlazada simple.
 * Los nuevos elementos se agregan al inicio.
 * @param <T> El tipo de dato que almacena la lista.
 */
public class ListaEnlazadaSimple<T> {
    private Nodo<T> cabeza;
    private int tamano;

    public ListaEnlazadaSimple() {
        this.cabeza = null;
        this.tamano = 0;
    }

    /**
     * Agrega un nuevo elemento al inicio de la lista.
     * @param dato El dato a agregar.
     */
    public void agregarAlInicio(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato);
        nuevoNodo.siguiente = this.cabeza;
        this.cabeza = nuevoNodo;
        this.tamano++;
    }
    public void agregarAlFinal(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            Nodo<T> actual = cabeza;
            // Recorremos la lista hasta encontrar el último nodo
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            // Enlazamos el nuevo nodo al final
            actual.siguiente = nuevoNodo;
        }
        this.tamano++;
    }

    /**
     * Devuelve el primer nodo (cabeza) de la lista.
     * @return El nodo cabeza.
     */
    public Nodo<T> getCabeza() {
        return cabeza;
    }

    public int getTamano() {
        return tamano;
    }
}