package app.infrastructure.persistence;

import app.domain.model.Arista;
import app.domain.model.Conductor;
import app.domain.model.Coordenada;
import app.domain.repository.ConductorRepository;
import app.domain.structures.ListaEnlazadaSimple;
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
                        rs.getDouble("peso")
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
        ConductorRepository conductorRepo = new MySQLConductorRepository();

        try (Connection conn = ConexionBD.getInstance().getConnection()) {
            // Desactivar auto-commit para manejar la transacción manualmente
            conn.setAutoCommit(false);

            try (PreparedStatement psAdyacencia = conn.prepareStatement(deleteAdyacenciaSql);
                 PreparedStatement psZona = conn.prepareStatement(deleteZonaSql)) {

                // --- ¡NUEVA LÓGICA DE REUBICACIÓN! ---
                // 1. Encontrar conductores en la zona a eliminar.
                List<Conductor> conductoresAfectados = conductorRepo.findByZonaId(idZona);
                List<Zona> zonasRestantes = getTodasLasZonas();
                zonasRestantes.removeIf(z -> z.getId() == idZona); // Quita la zona que se va a eliminar

                if (!conductoresAfectados.isEmpty()) {
                    if (zonasRestantes.isEmpty()) {
                        // Caso extremo: no hay más zonas, se eliminan los conductores.
                        System.out.println("[WARN] No hay zonas restantes. Eliminando conductores de la zona " + idZona);
                        for (Conductor conductor : conductoresAfectados) {
                            conductorRepo.delete(conductor.getId());
                        }
                    } else {
                        // Caso normal: reubicar conductores a la zona más cercana.
                        Zona zonaEliminada = getTodasLasZonas().stream().filter(z -> z.getId() == idZona).findFirst().orElse(null);
                        if (zonaEliminada != null) {
                            for (Conductor conductor : conductoresAfectados) {
                                Zona zonaMasCercana = encontrarZonaMasCercana(zonaEliminada, zonasRestantes);
                                if (zonaMasCercana != null) {
                                    System.out.printf("[LOGIC] Reubicando conductor %s de zona eliminada %d a la zona más cercana %d%n",
                                            conductor.getNombreCompleto(), idZona, zonaMasCercana.getId());
                                    conductorRepo.updateZona(conductor.getId(), zonaMasCercana.getId());
                                }
                            }
                        }
                    }
                }
                // --- FIN DE LA LÓGICA DE REUBICACIÓN ---

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

    /**
     * Método de ayuda para encontrar la zona más cercana a una zona dada,
     * basado en la distancia euclidiana de sus coordenadas.
     */
    private Zona encontrarZonaMasCercana(Zona origen, List<Zona> candidatas) {
        Zona masCercana = null;
        double distanciaMinima = Double.MAX_VALUE;
        Coordenada coordOrigen = new Coordenada(origen.getLongitud(), origen.getLatitud());

        for (Zona candidata : candidatas) {
            double distancia = coordOrigen.calcularDistancia(new Coordenada(candidata.getLongitud(), candidata.getLatitud()));
            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                masCercana = candidata;
            }
        }
        return masCercana;
    }

    @Override
    public ListaEnlazadaSimple<Zona> getTopZonas(int limit) {
        ListaEnlazadaSimple<Zona> topZonas = new ListaEnlazadaSimple<>();
        // --- ¡AQUÍ ESTÁ LA MAGIA! ---
        // Añadimos un filtro para que solo considere las zonas visibles.
        String sql = "SELECT id, nombre, latitud, longitud, visible, numero_viajes " + "FROM zonas WHERE visible = TRUE " + "ORDER BY numero_viajes DESC LIMIT ?";
        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Zona z = new Zona(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getDouble("latitud"),
                            rs.getDouble("longitud"),
                            rs.getBoolean("visible")
                    );
                    z.setNumeroViajes(rs.getInt("numero_viajes"));
                    topZonas.agregarAlFinal(z);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topZonas;
    }
}
