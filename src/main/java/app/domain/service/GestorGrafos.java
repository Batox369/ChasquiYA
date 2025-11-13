package app.domain.service;

import app.domain.model.Arista;
import app.domain.model.GrafoZonas; // <-- USA TU CLASE
import app.domain.model.Zona;
import app.domain.repository.GrafoRepository;
import app.infrastructure.persistence.MySQLGrafoRepository; // La implementación

import java.util.List;
/**
 * Servicio Singleton que gestiona la carga y el acceso
 * al grafo de zonas en memoria.
 */
public class GestorGrafos {

    private static GestorGrafos instancia;
    private GrafoZonas grafo; // <-- Almacena TU GrafoZonas
    private final GrafoRepository repo; // El repositorio para acceder a la BD

    /**
     * Constructor privado para el Singleton.
     * Carga el grafo desde la BD al iniciar.
     */
    private GestorGrafos() {
        this.repo = new MySQLGrafoRepository(); // Usa la implementación de MySQL
        // No inicializamos grafo aquí, lo hacemos en cargarGrafoDesdeBD
        cargarGrafoDesdeBD(); // Llama al método de carga inicial
    }

    /**
     * Obtiene la única instancia del GestorGrafo (Singleton).
     * @return La instancia única.
     */
    public static synchronized GestorGrafos getInstancia() {
        if (instancia == null) {
            instancia = new GestorGrafos();
        }
        return instancia;
    }

    /**
     * Carga los nodos (Zonas) y aristas (Conexiones) desde la base de datos
     * usando el GrafoRepository y construye el objeto GrafoZonas.
     * Este método REEMPLAZA el grafo existente si se llama de nuevo.
     */
    private void cargarGrafoDesdeBD() {
        // 1. Crea una NUEVA instancia de GrafoZonas para asegurar limpieza
        this.grafo = new GrafoZonas();

        // 2. Obtener Zonas (Nodos) del repositorio
        List<Zona> zonas = repo.getTodasLasZonas();
        for (Zona z : zonas) {
            // Usa el método de TU clase para añadir nodos
            grafo.agregarZona(z);
        }

        // 3. Obtener Aristas (Conexiones) del repositorio
        List<Arista> aristas = repo.getTodasLasAristas();
        for (Arista arista : aristas) {
            // Busca los objetos Zona usando el método de TU clase
            Zona a = grafo.getZona(arista.getIdZonaA());
            Zona b = grafo.getZona(arista.getIdZonaB());

            if (a != null && b != null) {
                // Usa el método de TU clase para conectar
                grafo.conectarZonas(a, b, arista.getDistancia());
            } else {
                System.err.println("Advertencia: No se encontraron zonas para la arista entre IDs " + arista.getIdZonaA() + " y " + arista.getIdZonaB());
            }
        }

        // Verifica si se cargaron zonas antes de imprimir el mensaje
        if (grafo.getZonas() != null) {
            System.out.println("✅ Grafo cargado en memoria con " + grafo.getZonas().size() + " nodos.");
        } else {
            System.out.println("✅ Grafo inicializado (vacío o error al cargar nodos).");
        }
    }

    /**
     * Devuelve la instancia del grafo ya construido y cargado.
     * @return El objeto GrafoZonas listo para usar.
     */
    public GrafoZonas getGrafo() {
        // Asegura que el grafo nunca sea null, incluso si la carga inicial falla
        if (this.grafo == null) {
            System.err.println("Error: El grafo no se inicializó correctamente. Intentando recargar...");
            cargarGrafoDesdeBD(); // Intenta cargar de nuevo
            if(this.grafo == null) { // Si aún es null después de recargar
                this.grafo = new GrafoZonas(); // Devuelve uno vacío para evitar NullPointerException
                System.err.println("Error crítico: No se pudo cargar el grafo. Se devuelve un grafo vacío.");
            }
        }
        return this.grafo;
    }

    /**
     * Fuerza la recarga completa del grafo desde la base de datos.
     * Útil si el admin añade/modifica zonas o conexiones.
     */
    public void recargarGrafo() {
        System.out.println("Recargando grafo desde la base de datos...");
        cargarGrafoDesdeBD();
    }
}