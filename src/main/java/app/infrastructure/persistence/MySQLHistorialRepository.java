package app.infrastructure.persistence;

import app.domain.model.Viaje;
import app.domain.repository.HistorialRepository;
import app.domain.structures.ListaEnlazadaSimple;

import java.sql.*;

public class MySQLHistorialRepository implements HistorialRepository {

    @Override
    public boolean guardarViaje(Viaje viaje) {
        String sql = "INSERT INTO historial_viajes (id_usuario, id_conductor, zona_origen, zona_destino, distancia_metros, precio_calculado, fecha_viaje) VALUES (?, ?, ?, ?, ?, ?, ?)";

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

            pstmt.setString(3, viaje.getNombreOrigen());
            pstmt.setString(4, viaje.getNombreDestino());
            pstmt.setDouble(5, viaje.getDistanciaMetros());
            pstmt.setDouble(6, viaje.getPrecio());
            pstmt.setTimestamp(7, new Timestamp(viaje.getFecha().getTime()));

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
        String sql = "SELECT id, id_conductor, distancia_metros, precio_calculado, fecha_viaje, zona_origen, zona_destino " +
                     "FROM historial_viajes WHERE id_usuario = ? ORDER BY fecha_viaje DESC";

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
                // Usamos el nuevo constructor para cargar desde el historial
                Viaje viaje = new Viaje(
                        rs.getString("zona_origen"),
                        rs.getString("zona_destino"),
                        rs.getDouble("distancia_metros")
                );

                // Asignamos el resto de los valores
                viaje.setId(rs.getInt("id"));
                viaje.setConductorId(rs.getInt("id_conductor"));
                viaje.setUsuarioId(idUsuario);
                viaje.setPrecio(rs.getDouble("precio_calculado"));
                viaje.setFecha(rs.getTimestamp("fecha_viaje"));

                historial.agregarAlInicio(viaje); // Se agrega al inicio para que los más recientes queden primeros
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener el historial de viajes: " + e.getMessage());
        }

        return historial;
    }
}