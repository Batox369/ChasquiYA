package app.domain.repository;

import app.domain.model.Zona;
import app.domain.model.GrafoZonas;
import app.domain.service.MapUtils;
import app.infrastructure.persistence.ConexionBD;

import java.sql.*;

public class ZonaRepository {
    private final Connection connection;

    public ZonaRepository() {
        try {
            this.connection = ConexionBD.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo conexión", e);
        }
    }

    public GrafoZonas cargarGrafo(double anchoMapa, double altoMapa) {
        GrafoZonas grafo = new GrafoZonas();

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, nombre, latitud, longitud FROM zonas");

            while (rs.next()) {
                Zona z = new Zona(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("latitud"),
                        rs.getDouble("longitud")
                );
                MapUtils.escalarZona(z, anchoMapa, altoMapa);
                grafo.agregarZona(z);
            }

            rs = stmt.executeQuery("SELECT zona_origen_id, zona_destino_id, peso FROM zona_adyacencia");
            while (rs.next()) {
                Zona a = grafo.getZona(rs.getInt("zona_origen_id"));
                Zona b = grafo.getZona(rs.getInt("zona_destino_id"));
                if (a != null && b != null) {
                    grafo.conectarZonas(a, b, rs.getDouble("peso"));
                }
            }

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return grafo;
    }

}
