package app.infrastructure.persistence;

import app.domain.model.Zona;
import app.domain.model.Viaje;
import app.domain.repository.HistorialRepository;
import app.domain.structures.ListaEnlazadaSimple;

import java.sql.*;

public class MySQLHistorialRepository implements HistorialRepository {

    @Override
    public boolean guardarViaje(Viaje viaje) {
        String sql = "INSERT INTO historial_viajes (id_usuario, id_conductor, id_zona_origen, id_zona_destino, distancia_metros, precio_calculado, fecha_viaje) VALUES (?, ?, ?, ?, ?, ?, ?)";

        // Obtenemos la conexión compartida ANTES del try-with-resources
        Connection connection = null;

        try {
            connection = ConexionBD.getInstance().getConnection();
        } catch (SQLException e) {
            System.err.println("Error al obtener conexión con la BD: " + e.getMessage());
        }


        // Solo el PreparedStatement (que es temporal) va dentro del try-with-resources
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, viaje.getUsuarioId());
            pstmt.setInt(2, viaje.getConductorId());

            // --- ¡CORRECCIÓN! --- Usamos los nuevos getters para los IDs.
            pstmt.setInt(3, viaje.getZonaOrigenId());
            pstmt.setInt(4, viaje.getZonaDestinoId());

            pstmt.setDouble(5, viaje.getDistanciaMetros());
            pstmt.setDouble(6, viaje.getPrecio());
            pstmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error al guardar el viaje en la base de datos: " + e.getMessage());
            return false;
        }
    }

    @Override
    public ListaEnlazadaSimple<Viaje> getHistorialPorUsuario(int idUsuario) {
        ListaEnlazadaSimple<Viaje> historial = new ListaEnlazadaSimple<>();
        // Se ordena por fecha descendente para que los viajes más recientes aparezcan primero
        String sql = "SELECT hv.id, hv.id_conductor, hv.distancia_metros, hv.precio_calculado, hv.fecha_viaje, " +
                     "zo.id as id_origen, zo.nombre as nombre_origen, zo.longitud as lon_origen, zo.latitud as lat_origen, " +
                     "zd.id as id_destino, zd.nombre as nombre_destino, zd.longitud as lon_destino, zd.latitud as lat_destino " +
                     "FROM historial_viajes hv " +
                     "JOIN zonas zo ON hv.id_zona_origen = zo.id " +
                     "JOIN zonas zd ON hv.id_zona_destino = zd.id " +
                     "WHERE hv.id_usuario = ? ORDER BY hv.fecha_viaje DESC";

        // Obtenemos la conexión compartida ANTES del try-with-resources
        Connection connection = null;

        try {
            connection = ConexionBD.getInstance().getConnection();
        } catch (SQLException e) {
            System.err.println("Error al obtener conexión con la BD: " + e.getMessage());
        }

        // El PreparedStatement y el ResultSet van dentro del try-with-resources
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // --- ¡CORRECCIÓN! ---
                // 1. Creamos los objetos Zona de origen y destino por separado.
                Zona origen = new Zona(rs.getInt("id_origen"), rs.getString("nombre_origen"), rs.getDouble("lon_origen"), rs.getDouble("lat_origen"));
                Zona destino = new Zona(rs.getInt("id_destino"), rs.getString("nombre_destino"), rs.getDouble("lon_destino"), rs.getDouble("lat_destino"));

                // --- ¡CORRECCIÓN! ---
                // 1. Usamos el constructor principal, que es más simple y seguro.
                Viaje viaje = new Viaje(origen, destino, origen.getNombre(), destino.getNombre());
                System.out.println("→ fecha_viaje en BD: " + rs.getString("fecha_viaje"));

                // 2. Asignamos TODOS los valores recuperados de la BD usando los setters.
                viaje.setId(rs.getInt("id"));
                viaje.setConductorId(rs.getInt("id_conductor"));
                viaje.setUsuarioId(idUsuario);
                viaje.setDistanciaMetros(rs.getDouble("distancia_metros"));
                viaje.setPrecio(rs.getDouble("precio_calculado"));
                viaje.setFecha(rs.getTimestamp("fecha_viaje"));

                // Agregamos el viaje al final de la lista para mantener el orden de la consulta SQL.
                historial.agregarAlFinal(viaje);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener el historial de viajes: " + e.getMessage());
        }

        return historial;
    }
}