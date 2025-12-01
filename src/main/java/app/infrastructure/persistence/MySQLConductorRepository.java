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
        String sql = "INSERT INTO conductores (nombre_completo, zona_actual_id, estado) VALUES (?, ?, ?)";
        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, idZona);
            ps.setString(3, EstadoConductor.DISPONIBLE.name());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Conductor> getTodosLosConductores() {
        List<Conductor> conductores = new ArrayList<>();
        String sql = "SELECT id, nombre_completo, placa_vehiculo, estado, zona_actual_id FROM conductores";
        try (Connection conn = ConexionBD.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                conductores.add(new Conductor(
                        rs.getInt("id"),
                        rs.getString("nombre_completo"),
                        rs.getString("placa_vehiculo"),
                        EstadoConductor.valueOf(rs.getString("estado")),
                        rs.getInt("zona_actual_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conductores;
    }

    @Override
    public List<Conductor> findByZonaId(int zonaId) {
        List<Conductor> conductores = new ArrayList<>();
        String sql = "SELECT id, nombre_completo, placa_vehiculo, estado, zona_actual_id FROM conductores WHERE zona_actual_id = ?";
        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, zonaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    conductores.add(new Conductor(
                            rs.getInt("id"),
                            rs.getString("nombre_completo"),
                            rs.getString("placa_vehiculo"),
                            EstadoConductor.valueOf(rs.getString("estado")),
                            rs.getInt("zona_actual_id")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conductores;
    }

    @Override
    public boolean updateZona(int conductorId, int nuevaZonaId) {
        String sql = "UPDATE conductores SET zona_actual_id = ? WHERE id = ?";
        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nuevaZonaId);
            ps.setInt(2, conductorId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int conductorId) {
        String sql = "DELETE FROM conductores WHERE id = ?";
        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, conductorId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}