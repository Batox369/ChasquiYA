package app.domain.service;

import app.domain.model.Conductor;
import app.domain.repository.ConductorRepository;
import app.infrastructure.persistence.MySQLConductorRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio Singleton que gestiona la carga y el acceso
 * a la lista de conductores en memoria.
 */
public class GestorConductores {

    private static GestorConductores instancia;
    private List<Conductor> conductores;
    private final ConductorRepository repo;

    private GestorConductores() {
        this.repo = new MySQLConductorRepository();
        this.conductores = new ArrayList<>();
        cargarConductoresDesdeBD();
    }

    /**
     * Obtiene la única instancia del GestorConductores (Singleton).
     */
    public static synchronized GestorConductores getInstancia() {
        if (instancia == null) {
            instancia = new GestorConductores();
        }
        return instancia;
    }

    /**
     * Carga o recarga la lista de conductores desde la base de datos.
     */
    public void cargarConductoresDesdeBD() {
        this.conductores = repo.getTodosLosConductores();
        System.out.println("✅ " + this.conductores.size() + " conductores cargados en memoria.");
    }

    /**
     * Devuelve la lista de conductores cargada.
     * @return Una lista de objetos Conductor.
     */
    public List<Conductor> getConductores() {
        return conductores;
    }
}