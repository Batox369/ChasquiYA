package app.domain.model;

import java.util.*;

public class GrafoZonas {
    private final Map<Integer, Zona> zonas = new HashMap<>();
    private final Map<Zona, List<Conexion>> adyacencias = new HashMap<>();

    public void agregarZona(Zona zona) {
        zonas.put(zona.getId(), zona);
        adyacencias.putIfAbsent(zona, new ArrayList<>());
    }

    public void conectarZonas(Zona a, Zona b, double distancia) {
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
}
