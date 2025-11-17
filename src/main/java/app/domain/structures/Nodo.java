package app.domain.structures;

/**
 * Representa un nodo en una lista enlazada.
 * Contiene un dato y una referencia al siguiente nodo.
 * @param <T> El tipo de dato que almacena el nodo.
 */
public class Nodo<T> {
    public T dato;
    public Nodo<T> siguiente;

    public Nodo(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }
}