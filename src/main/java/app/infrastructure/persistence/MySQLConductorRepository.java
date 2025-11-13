package app.infrastructure.persistence;

import app.domain.model.Conductor;
import app.domain.model.EstadoConductor;
import app.domain.repository.ConductorRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLConductorRepository implements ConductorRepository {

    @Override
    public boolean addConductor(String nombre, int idZona) {
        // ¡CORREGIDO! Se usan los nombres de columna correctos y se añade la placa.
        String sql = "INSERT INTO conductores (nombre_completo, placa_vehiculo, zona_actual_id) VALUES (?, ?, ?)";
        
        // Creamos un conductor temporal para generar una placa automática única
        Conductor conductorTemporal = new Conductor(nombre);

        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, conductorTemporal.getNombreCompleto());
            pstmt.setString(2, conductorTemporal.getPlacaVehiculo());
            pstmt.setInt(3, idZona);

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

        try (Connection conn = ConexionBD.getInstance().getConnection();
             Statement stmt = conn.createStatement();
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