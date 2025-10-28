package app.infrastructure.persistence;

import app.domain.model.Arista;
import app.domain.model.Zona;
import app.domain.repository.GrafoRepository;

import java.sql.Connection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLGrafoRepository implements GrafoRepository {

    private final Connection connection;

    public MySQLGrafoRepository() {
        // Obtiene la conexión del Singleton
        this.connection = ConexionBD.getInstance().getConnection();
    }

    @Override
    public List<Zona> getTodasLasZonas() {
        List<Zona> zonas = new ArrayList<>();
        String sql = "SELECT id, nombre, latitud, longitud FROM zonas";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                zonas.add(new Zona(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("latitud"), // Usamos DOUBLE ahora
                        rs.getDouble("longitud") // Usamos DOUBLE ahora
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

        try (Statement stmt = connection.createStatement();
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
    public boolean addZona(String nombre, double x, double y) {
        String sql = "INSERT INTO zonas (nombre, longitud, latitud) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setDouble(2, x); // Longitud = X (DOUBLE)
            ps.setDouble(3, y); // Latitud = Y (DOUBLE)
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
