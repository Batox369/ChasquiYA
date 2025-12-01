package app.domain.repository;

import app.domain.model.Arista; // Asegúrate de tener este modelo
import app.domain.structures.ListaEnlazadaSimple;
import app.domain.model.Zona;
import java.util.List;

public interface GrafoRepository {

    List<Zona> getTodasLasZonas();

    List<Arista> getTodasLasAristas();

    boolean addZona(String nombre, double x, double y, boolean visible);

    boolean addConexion(int idZonaA, int idZonaB, double peso);

    boolean deleteConexion(int idZonaA, int idZonaB);

    boolean deleteZona(int idZona);

    /**
     * Obtiene una lista de las zonas con más viajes (como destino), ordenadas de mayor a menor.
     * @param limit El número máximo de zonas a devolver.
     * @return Una lista de zonas.
     */
    ListaEnlazadaSimple<Zona> getTopZonas(int limit);
    // (Aquí podríamos añadir métodos para addArista, deleteZona, etc. en el futuro)
}