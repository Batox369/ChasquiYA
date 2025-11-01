package app.domain.service;

import app.domain.model.GrafoZonas;
import app.domain.model.Zona;

import java.util.*;

public class GestorRutas {
    public List<Zona> calcularRutaMasCorta(GrafoZonas grafo, Zona origen, Zona destino) {
        Map<Zona, Double> dist = new HashMap<>();
        Map<Zona, Zona> prev = new HashMap<>();
        PriorityQueue<Zona> cola = new PriorityQueue<>(Comparator.comparing(dist::get));

        for (Zona z : grafo.getZonas()) dist.put(z, Double.POSITIVE_INFINITY);
        dist.put(origen, 0.0);
        cola.add(origen);

        while (!cola.isEmpty()) {
            Zona actual = cola.poll();
            if (actual.equals(destino)) break;

            for (GrafoZonas.Conexion con : grafo.getConexiones(actual)) {
                double nuevaDist = dist.get(actual) + con.distancia;
                if (nuevaDist < dist.get(con.destino)) {
                    dist.put(con.destino, nuevaDist);
                    prev.put(con.destino, actual);
                    cola.add(con.destino);
                }
            }
        }

        List<Zona> ruta = new ArrayList<>();
        Zona paso = destino;
        while (paso != null) {
            ruta.add(0, paso);
            paso = prev.get(paso);
        }
        return ruta;
    }
}
