package ui.components;

import domain.model.Coordenada;
import infrastructure.shared.constants.Colors;
import java.awt.*;

public class MapMarker {
    private Coordenada coordenada;
    private String tipo; // "origen" o "destino"
    private Color color;
    private int radio = 8;

    public MapMarker(Coordenada coordenada, String tipo) {
        this.coordenada = coordenada;
        this.tipo = tipo;
        this.color = tipo.equals("origen") ? new Color(34, 197, 94) : Colors.ERROR;
    }

    public void draw(Graphics2D g2d, double zoom, int offsetX, int offsetY) {
        Point screenPoint = coordenada.toPantallaPoint(zoom, offsetX, offsetY);

        // Sombra
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillOval(screenPoint.x - radio - 1, screenPoint.y - radio - 1,
                radio * 2 + 4, radio * 2 + 4);

        // Círculo exterior
        g2d.setColor(color);
        g2d.fillOval(screenPoint.x - radio, screenPoint.y - radio,
                radio * 2, radio * 2);

        // Círculo interior blanco
        g2d.setColor(Color.WHITE);
        g2d.fillOval(screenPoint.x - radio/2, screenPoint.y - radio/2,
                radio, radio);

        // Borde del marcador
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(screenPoint.x - radio, screenPoint.y - radio,
                radio * 2, radio * 2);

        // Etiqueta
        drawLabel(g2d, screenPoint);
    }

    private void drawLabel(Graphics2D g2d, Point point) {
        String label = tipo.equals("origen") ? "Origen" : "Destino";
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));
        FontMetrics fm = g2d.getFontMetrics();
        int labelWidth = fm.stringWidth(label);
        int labelHeight = fm.getHeight();

        // Fondo de la etiqueta
        int padding = 4;
        int labelX = point.x - labelWidth/2 - padding;
        int labelY = point.y - radio - labelHeight - 5;

        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(labelX, labelY, labelWidth + padding * 2,
                labelHeight + padding, 4, 4);

        // Texto de la etiqueta
        g2d.setColor(Color.WHITE);
        g2d.drawString(label, point.x - labelWidth/2,
                point.y - radio - 10);
    }

    public Coordenada getCoordenada() {
        return coordenada;
    }

    public String getTipo() {
        return tipo;
    }
}