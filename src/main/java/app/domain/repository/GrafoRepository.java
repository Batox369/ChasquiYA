package app.domain.repository;

import app.domain.model.Arista; // Asegúrate de tener este modelo
import app.domain.model.Zona;
import java.util.List;

public interface GrafoRepository {

    List<Zona> getTodasLasZonas();

    List<Arista> getTodasLasAristas();

    boolean addZona(String nombre, double x, double y, boolean visible);

    boolean addConexion(int idZonaA, int idZonaB, double peso);

    boolean deleteConexion(int idZonaA, int idZonaB);

    boolean deleteZona(int idZona);
    // (Aquí podríamos añadir métodos para addArista, deleteZona, etc. en el futuro)
}