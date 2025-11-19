package app.ui.components;

import app.domain.model.Coordenada;
import app.domain.model.Conductor;
import app.domain.model.Zona;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ConductorRenderer {

    private static final Map<Integer, Color> conductorColors = new HashMap<>();
    private static final Random random = new Random();

    /**
     * Dibuja un conductor en el mapa en la posición de su zona actual.
     * @param g2d El contexto gráfico 2D.
     * @param conductor El conductor a dibujar.
     */
    public static void drawConductor(Graphics2D g2d, Conductor conductor) {
        Coordenada pos = conductor.getPosicionActual();
        if (pos == null) return; // No dibujar si el conductor no tiene posición

        Color color = conductorColors.computeIfAbsent(conductor.getId(), id -> {
            // Genera un color aleatorio pero consistente para cada conductor
            float r = random.nextFloat();
            float g = random.nextFloat();
            float b = random.nextFloat();
            return new Color(r, g, b);
        });

        int x = (int) pos.getX();
        int y = (int) pos.getY();
        int size = 12; // Tamaño del cuadrado que representa al conductor

        g2d.setColor(color);
        g2d.fillRect(x - size / 2, y - size / 2, size, size);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x - size / 2, y - size / 2, size, size);
    }
}