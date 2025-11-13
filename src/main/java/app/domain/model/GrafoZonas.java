package app.domain.model;

import java.util.*;

public class GrafoZonas {
    private final Map<Integer, Zona> zonas = new HashMap<>();
    // Usamos Map<Zona, List<Conexion>> para representar el grafo
    private final Map<Zona, List<Conexion>> adyacencias = new HashMap<>();

    public void agregarZona(Zona zona) {
        zonas.put(zona.getId(), zona);
        adyacencias.putIfAbsent(zona, new ArrayList<>());
    }

    public void conectarZonas(Zona a, Zona b, double distancia) {
        // Grafo no dirigido: la conexión es en ambos sentidos
        adyacencias.get(a).add(new Conexion(b, distancia));
        adyacencias.get(b).add(new Conexion(a, distancia));
    }

    public Zona getZona(int id) {
        return zonas.get(id);
    }

    public Collection<Zona> getZonas() {
        return zonas.values();
    }

    public List<Conexion> getConexiones(Zona zona) {
        // Devuelve una lista vacía si la zona no tiene conexiones para evitar errores
        return adyacencias.getOrDefault(zona, new ArrayList<>());
    }

    public static class Conexion {
        public final Zona destino;
        public final double distancia;
        public Conexion(Zona destino, double distancia) {
            this.destino = destino;
            this.distancia = distancia;
        }
    }

    public double getDistanciaEntre(Zona a, Zona b) {
        if (adyacencias.containsKey(a)) {
            for (Conexion con : adyacencias.get(a)) {
                if (con.destino.equals(b)) {
                    return con.distancia;
                }
            }
        }
        return 0.0; // No hay conexión directa
    }

    /**
     * Devuelve una lista de todas las aristas que salen de una zona dada.
     * Este método es útil para el simulador de movimiento.
     * @param origen La zona de origen.
     * @return Una lista de objetos Arista.
     */
    public List<Arista> getAristasSalientes(Zona origen) {
        List<Arista> aristasSalientes = new ArrayList<>();
        List<Conexion> conexiones = getConexiones(origen);
        for (Conexion con : conexiones) {
            aristasSalientes.add(new Arista(origen.getId(), con.destino.getId(), con.distancia));
        }
        return aristasSalientes;
    }
}
