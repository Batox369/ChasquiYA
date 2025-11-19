package app.domain.repository;

import app.domain.model.Conductor;

import java.util.List;

public interface ConductorRepository {
    /**
     * Agrega un nuevo conductor a la base de datos, asociándolo a una zona.
     */
    boolean addConductor(String nombre, int idZona);

    /**
     * Obtiene todos los conductores de la base de datos.
     */
    List<Conductor> getTodosLosConductores();
}