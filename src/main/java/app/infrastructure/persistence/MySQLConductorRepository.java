package app.infrastructure.persistence;

import app.domain.model.Conductor;
import app.domain.model.enums.EstadoConductor;
import app.domain.repository.ConductorRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLConductorRepository implements ConductorRepository {

    @Override
    public boolean addConductor(String nombre, int idZona) {
        // ¡CORREGIDO! Se usan los nombres de columna correctos y se añade la placa.
        // --- ¡CORRECCIÓN! Ahora también insertamos el estado. ---
        String sql = "INSERT INTO conductores (nombre_completo, placa_vehiculo, zona_actual_id, estado) VALUES (?, ?, ?, ?)";
        
        // Creamos un conductor temporal para generar una placa automática única
        Conductor conductorTemporal = new Conductor(nombre);

        // Obtenemos la conexión compartida, pero NO la ponemos en el try-with-resources.
        try (PreparedStatement pstmt = ConexionBD.getInstance().getConnection().prepareStatement(sql)) {

            pstmt.setString(1, conductorTemporal.getNombreCompleto());
            pstmt.setString(2, conductorTemporal.getPlacaVehiculo());
            pstmt.setInt(3, idZona);
            pstmt.setString(4, EstadoConductor.DISPONIBLE.name()); // <-- NUEVO: Lo creamos como DISPONIBLE

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar conductor en la base de datos: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Conductor> getTodosLosConductores() {
        List<Conductor> conductores = new ArrayList<>();
        // ¡CORREGIDO! Se usan los nombres de columna correctos de la tabla.
        String sql = "SELECT id, nombre_completo, placa_vehiculo, estado, zona_actual_id FROM conductores";

        // Obtenemos la conexión compartida, pero NO la ponemos en el try-with-resources.
        try (Statement stmt = ConexionBD.getInstance().getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                conductores.add(new Conductor(
                        rs.getInt("id"),
                        rs.getString("nombre_completo"),
                        rs.getString("placa_vehiculo"),
                        EstadoConductor.valueOf(rs.getString("estado").toUpperCase()),
                        rs.getObject("zona_actual_id", Integer.class) // Permite nulos
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los conductores de la base de datos: " + e.getMessage());
            // Devuelve una lista vacía en caso de error
        }
        return conductores;
    }
}