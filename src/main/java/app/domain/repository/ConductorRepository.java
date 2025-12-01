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

    /**
     * Encuentra todos los conductores asignados a una zona específica.
     * @param zonaId El ID de la zona.
     * @return Una lista de conductores en esa zona.
     */
    List<Conductor> findByZonaId(int zonaId);

    /**
     * Actualiza la zona asignada de un conductor.
     * @param conductorId El ID del conductor a actualizar.
     * @param nuevaZonaId El ID de la nueva zona.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    boolean updateZona(int conductorId, int nuevaZonaId);

    /**
     * Elimina un conductor de la base de datos.
     * @param conductorId El ID del conductor a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    boolean delete(int conductorId);
}