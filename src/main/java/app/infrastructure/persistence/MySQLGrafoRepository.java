package app.infrastructure.persistence;

import app.domain.model.Arista;
import app.domain.model.Zona;
import app.domain.repository.GrafoRepository;

import java.sql.Connection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLGrafoRepository implements GrafoRepository {

    @Override
    public List<Zona> getTodasLasZonas() {
        List<Zona> zonas = new ArrayList<>();
        String sql = "SELECT id, nombre, latitud, longitud, visible FROM zonas";

        // --- ¡CORRECCIÓN! ---
        // Obtenemos la conexión compartida, pero NO la ponemos en el try-with-resources.
        try (Statement stmt = ConexionBD.getInstance().getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                zonas.add(new Zona(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("latitud"),
                        rs.getDouble("longitud"),
                        rs.getBoolean("visible")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return zonas;
    }

    @Override
    public List<Arista> getTodasLasAristas() {
        List<Arista> aristas = new ArrayList<>();
        String sql = "SELECT zona_origen_id, zona_destino_id, peso FROM zona_adyacencia";

        // Obtenemos la conexión compartida, pero NO la ponemos en el try-with-resources.
        try (Statement stmt = ConexionBD.getInstance().getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                aristas.add(new Arista(
                        rs.getInt("zona_origen_id"), // Corregido: usa los nombres de columna correctos
                        rs.getInt("zona_destino_id"), // Corregido: usa los nombres de columna correctos
                        rs.getDouble("peso")         // Corregido: usa el nombre de columna correcto
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return aristas;
    }

    @Override
    public boolean addZona(String nombre, double x, double y, boolean visible) {
        String sql = "INSERT INTO zonas (nombre, longitud, latitud, visible) VALUES (?, ?, ?, ?)";
        // Obtenemos la conexión compartida, pero NO la ponemos en el try-with-resources.
        try (PreparedStatement ps = ConexionBD.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setDouble(2, x); // Longitud = X (DOUBLE)
            ps.setDouble(3, y); // Latitud = Y (DOUBLE)
            ps.setBoolean(4, visible);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean addConexion(int idZonaA, int idZonaB, double peso) {
        // Inserta la conexión en ambos sentidos para grafo no dirigido
        String sql = "INSERT INTO zona_adyacencia (zona_origen_id, zona_destino_id, peso) VALUES (?, ?, ?), (?, ?, ?)";
        // Obtenemos la conexión compartida, pero NO la ponemos en el try-with-resources.
        try (PreparedStatement ps = ConexionBD.getInstance().getConnection().prepareStatement(sql)) {
            // Conexión A -> B
            ps.setInt(1, idZonaA);
            ps.setInt(2, idZonaB);
            ps.setDouble(3, peso);
            // Conexión B -> A
            ps.setInt(4, idZonaB);
            ps.setInt(5, idZonaA);
            ps.setDouble(6, peso);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteConexion(int idZonaA, int idZonaB) {
        // Elimina la conexión en ambas direcciones para asegurar consistencia
        String sql = "DELETE FROM zona_adyacencia WHERE (zona_origen_id = ? AND zona_destino_id = ?) OR (zona_origen_id = ? AND zona_destino_id = ?)";
        try (PreparedStatement ps = ConexionBD.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, idZonaA);
            ps.setInt(2, idZonaB);
            ps.setInt(3, idZonaB);
            ps.setInt(4, idZonaA);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0; // Devuelve true si se eliminó al menos una fila
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteZona(int idZona) {
        String deleteAdyacenciaSql = "DELETE FROM zona_adyacencia WHERE zona_origen_id = ? OR zona_destino_id = ?";
        String deleteZonaSql = "DELETE FROM zonas WHERE id = ?";

        try (Connection conn = ConexionBD.getInstance().getConnection()) {
            // Desactivar auto-commit para manejar la transacción manualmente
            conn.setAutoCommit(false);

            try (PreparedStatement psAdyacencia = conn.prepareStatement(deleteAdyacenciaSql);
                 PreparedStatement psZona = conn.prepareStatement(deleteZonaSql)) {

                // Eliminar todas las conexiones relacionadas con la zona
                psAdyacencia.setInt(1, idZona);
                psAdyacencia.setInt(2, idZona);
                psAdyacencia.executeUpdate();

                // Eliminar la zona en sí
                psZona.setInt(1, idZona);
                int affectedRows = psZona.executeUpdate();

                conn.commit(); // Confirmar la transacción
                return affectedRows > 0;
            } catch (SQLException e) {
                conn.rollback(); // Revertir en caso de error
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
}
