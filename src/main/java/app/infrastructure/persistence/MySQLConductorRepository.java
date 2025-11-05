package app.infrastructure.persistence;

import app.domain.repository.ConductorRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLConductorRepository implements ConductorRepository {

    @Override
    public boolean addConductor(String nombre, int idZona) {
        String sql = "INSERT INTO conductores (nombre, id_zona_actual) VALUES (?, ?)";

        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setInt(2, idZona);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar conductor en la base de datos: " + e.getMessage());
            return false;
        }
    }
}