package app.infrastructure.persistence;

import app.domain.model.Conductor;
import app.domain.model.enums.EstadoConductor;
import app.domain.structures.ListaEnlazadaSimple;
import app.domain.repository.ConductorRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

public class MySQLConductorRepository implements ConductorRepository {

    @Override
    public boolean addConductor(String nombre, int idZona) {
        // --- ¡CORRECCIÓN! --- Se añade la columna placa_vehiculo a la consulta.
        String sql = "INSERT INTO conductores (nombre_completo, zona_actual_id, estado, placa_vehiculo) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, idZona);
            ps.setString(3, EstadoConductor.DISPONIBLE.name());
            ps.setString(4, generarPlacaAleatoria()); // <-- ¡AQUÍ ESTÁ LA MAGIA!
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

    @Override
    public ListaEnlazadaSimple<Conductor> getTopConductores(int limit) {
        ListaEnlazadaSimple<Conductor> topConductores = new ListaEnlazadaSimple<>();
        String sql = "SELECT id, nombre_completo, placa_vehiculo, estado, zona_actual_id, viajes_realizados " +
                     "FROM conductores ORDER BY viajes_realizados DESC LIMIT ?";
        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Conductor c = new Conductor(
                            rs.getInt("id"),
                            rs.getString("nombre_completo"),
                            rs.getString("placa_vehiculo"),
                            EstadoConductor.valueOf(rs.getString("estado")),
                            rs.getInt("zona_actual_id")
                    );
                    c.setViajesRealizados(rs.getInt("viajes_realizados"));
                    topConductores.agregarAlFinal(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topConductores;
    }

    /**
     * Genera una placa de vehículo aleatoria con el formato LLL-NNN.
     * @return Una cadena de texto con la placa generada.
     */
    private String generarPlacaAleatoria() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        // Genera 3 letras aleatorias
        for (int i = 0; i < 3; i++) {
            char c = (char) (random.nextInt(26) + 'A');
            sb.append(c);
        }
        sb.append('-');
        // Genera 3 números aleatorios (formato 000-999)
        sb.append(String.format("%03d", random.nextInt(1000)));
        return sb.toString();
    }
}