package app.domain.service;

import app.domain.model.*;
import app.domain.model.enums.EstadoConductor;

import java.util.*;


public class AsignadorDeViajes {

    public Conductor asignarConductorMasCercano(Zona origen, List<Conductor> todosLosConductores, GrafoZonas grafo) {
        System.out.println("\nIniciando asignación de conductor para origen: " + origen.getNombre());
        // 1. Filtrar solo conductores DISPONIBLES que tengan una ubicación.
        List<Conductor> conductoresDisponibles = new ArrayList<>();
        for (Conductor c : todosLosConductores) {
            if (c.getEstado() == EstadoConductor.DISPONIBLE && c.getZonaActualId() != null) {
                conductoresDisponibles.add(c);
            }
        }

        if (conductoresDisponibles.isEmpty()) {
            System.out.println("No hay conductores disponibles en este momento.");
            return null;
        }
        System.out.println("Conductores disponibles encontrados: " + conductoresDisponibles.size());

        // 2. Ejecutar Dijkstra para encontrar las distancias desde el origen a todas las demás zonas.
        System.out.println("Calculando distancias");
        Map<Zona, Double> distancias = calcularDistanciasDijkstra(origen, grafo);

        // 3. Encontrar el conductor cuya zona actual tenga la menor distancia calculada.
        Conductor conductorMasCercano = null;
        double distanciaMinima = Double.POSITIVE_INFINITY;

        for (Conductor conductor : conductoresDisponibles) {
            Zona zonaConductor = grafo.getZona(conductor.getZonaActualId());
            if (zonaConductor != null && distancias.containsKey(zonaConductor)) {
                System.out.println("Evaluando a " + conductor.getNombreCompleto() + " en zona '" + zonaConductor.getNombre() + "' a distancia: " + distancias.get(zonaConductor));
                double distancia = distancias.get(zonaConductor);
                if (distancia < distanciaMinima) {
                    distanciaMinima = distancia;
                    conductorMasCercano = conductor;
                }
            }
        }

        // 4. Asignar el viaje al conductor encontrado (si existe).
        if (conductorMasCercano != null) {
            conductorMasCercano.setEstado(EstadoConductor.OCUPADO); // ¡Importante! El conductor ya no está disponible.
            System.out.println("Conductor asignado: " + conductorMasCercano.getNombreCompleto() + " a una distancia de " + distanciaMinima + "m.");
        } else {
            System.out.println("No se pudo encontrar una ruta a ningún conductor disponible.");
        }

        return conductorMasCercano;
    }

    private Map<Zona, Double> calcularDistanciasDijkstra(Zona origen, GrafoZonas grafo) {
        Map<Zona, Double> distancias = new HashMap<>();
        // Usamos una PriorityQueue para obtener siempre el nodo con la menor distancia de forma eficiente.
        PriorityQueue<Map.Entry<Zona, Double>> pq = new PriorityQueue<>(Map.Entry.comparingByValue());

        // Inicialización: todas las distancias son infinitas, excepto el origen que es 0.
        for (Zona zona : grafo.getZonas()) {
            distancias.put(zona, Double.POSITIVE_INFINITY);
        }
        distancias.put(origen, 0.0);
        pq.add(new AbstractMap.SimpleEntry<>(origen, 0.0));

        while (!pq.isEmpty()) {
            Zona actual = pq.poll().getKey();

            for (GrafoZonas.Conexion conexion : grafo.getConexiones(actual)) {
                Zona vecino = conexion.getDestino();
                double pesoArista = conexion.getDistancia();
                double nuevaDistancia = distancias.get(actual) + pesoArista;

                if (nuevaDistancia < distancias.get(vecino)) {
                    distancias.put(vecino, nuevaDistancia);
                    pq.add(new AbstractMap.SimpleEntry<>(vecino, nuevaDistancia));
                }
            }
        }
        return distancias;
    }
}