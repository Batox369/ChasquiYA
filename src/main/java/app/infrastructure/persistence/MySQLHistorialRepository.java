package app.infrastructure.persistence;

import app.domain.model.Viaje;
import app.domain.repository.HistorialRepository;
import app.domain.structures.ListaEnlazadaSimple;

import java.sql.*;

public class MySQLHistorialRepository implements HistorialRepository {

    @Override
    public boolean guardarViaje(Viaje viaje) {
        String insertViajeSql = "INSERT INTO historial_viajes (id_usuario, id_conductor, zona_origen, zona_destino, distancia_metros, precio_calculado, fecha_viaje) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String updateConductorSql = "UPDATE conductores SET viajes_realizados = viajes_realizados + 1 WHERE id = ?";
        String updateZonaSql = "UPDATE zonas SET numero_viajes = numero_viajes + 1 WHERE nombre = ?";

        try (Connection conn = ConexionBD.getInstance().getConnection()) {
            conn.setAutoCommit(false); // Iniciar transacción

            try (PreparedStatement psInsert = conn.prepareStatement(insertViajeSql);
                 PreparedStatement psUpdateConductor = conn.prepareStatement(updateConductorSql);
                 PreparedStatement psUpdateZona = conn.prepareStatement(updateZonaSql)) {

                // 1. Insertar el viaje en el historial
                psInsert.setInt(1, viaje.getUsuarioId());
                psInsert.setInt(2, viaje.getConductorId());
                psInsert.setString(3, viaje.getNombreOrigen());
                psInsert.setString(4, viaje.getNombreDestino());
                psInsert.setDouble(5, viaje.getDistanciaMetros());
                psInsert.setDouble(6, viaje.getPrecio());
                psInsert.setTimestamp(7, new Timestamp(viaje.getFecha().getTime()));
                psInsert.executeUpdate();

                // 2. Actualizar contador del conductor
                psUpdateConductor.setInt(1, viaje.getConductorId());
                psUpdateConductor.executeUpdate();

                // 3. Actualizar contador de la zona de destino
                psUpdateZona.setString(1, viaje.getNombreDestino());
                psUpdateZona.executeUpdate();

                conn.commit(); // Confirmar todos los cambios
                return true;

            } catch (SQLException e) {
                conn.rollback(); // Revertir cambios si algo falla
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true); // Restaurar auto-commit
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

                historial.agregarAlFinal(viaje); // Se agrega al inicio para que los más recientes queden primeros
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener el historial de viajes: " + e.getMessage());
        }

        return historial;
    }
}