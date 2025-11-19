package app.domain.service;

import app.domain.model.Usuario;
import app.domain.model.Viaje;
import app.domain.repository.HistorialRepository;
import app.domain.structures.ListaEnlazadaSimple;
import app.infrastructure.persistence.MySQLHistorialRepository;
import app.infrastructure.shared.SessionManager;

/**
 * Servicio Singleton para gestionar el historial de viajes de un usuario.
 */
public class GestorHistorial {
    private static GestorHistorial instancia;
    private final HistorialRepository repo;
    private ListaEnlazadaSimple<Viaje> historialEnMemoria;

    private GestorHistorial() {
        this.repo = new MySQLHistorialRepository();
        this.historialEnMemoria = new ListaEnlazadaSimple<>();
    }

    public static synchronized GestorHistorial getInstancia() {
        if (instancia == null) {
            instancia = new GestorHistorial();
        }
        return instancia;
    }

    /**
     * Carga el historial de viajes para un usuario específico desde la BD.
     * @param usuario El usuario cuyo historial se cargará.
     */
    public void cargarHistorial(Usuario usuario) {
        if (usuario != null) {
            this.historialEnMemoria = repo.getHistorialPorUsuario(usuario.getId());
            System.out.println("✅ Historial de " + this.historialEnMemoria.getTamano() + " viajes cargado para el usuario " + usuario.getNombreUsuario());
        }
    }

    /**
     * Guarda un nuevo viaje en la BD y lo añade a la lista en memoria.
     * @param viaje El viaje a guardar.
     */
    public void guardarViaje(Viaje viaje) {
        if (repo.guardarViaje(viaje)) {
            historialEnMemoria.agregarAlInicio(viaje);
            System.out.println("✅ Viaje guardado en el historial.");
        }
    }

    public ListaEnlazadaSimple<Viaje> getHistorialEnMemoria() {
        return historialEnMemoria;
    }
}