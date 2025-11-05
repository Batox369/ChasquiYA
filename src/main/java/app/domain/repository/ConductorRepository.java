package app.domain.repository;

public interface ConductorRepository {
    /**
     * Agrega un nuevo conductor a la base de datos, asociándolo a una zona.
     */
    boolean addConductor(String nombre, int idZona);
}