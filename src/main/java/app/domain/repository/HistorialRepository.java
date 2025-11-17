package app.domain.repository;

import app.domain.model.Viaje;
import app.domain.structures.ListaEnlazadaSimple;

public interface HistorialRepository {
    /**
     * Guarda un viaje completado en la base de datos.
     * @param viaje El objeto Viaje a guardar.
     * @return true si se guardó correctamente, false en caso contrario.
     */
    boolean guardarViaje(Viaje viaje);

    /**
     * Obtiene el historial de viajes de un usuario desde la base de datos.
     * @param idUsuario El ID del usuario.
     * @return Una ListaEnlazadaSimple con los viajes del usuario.
     */
    ListaEnlazadaSimple<Viaje> getHistorialPorUsuario(int idUsuario);
}