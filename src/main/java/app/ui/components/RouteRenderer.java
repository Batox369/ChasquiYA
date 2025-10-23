package app.ui.components;

import app.domain.model.Coordenada;
import app.infrastructure.shared.constants.Colors;
import java.awt.*;
import java.awt.geom.Path2D;

public class RouteRenderer {

    public static void drawRoute(Graphics2D g2d, Coordenada origen,
                                 Coordenada destino, double zoom,
                                 int offsetX, int offsetY) {
        Point p1 = origen.toPantallaPoint(zoom, offsetX, offsetY);
        Point p2 = destino.toPantallaPoint(zoom, offsetX, offsetY);

        // Línea de la ruta con efecto de sombra
        g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        g2d.setColor(new Color(0, 0, 0, 30));
        g2d.drawLine(p1.x + 2, p1.y + 2, p2.x + 2, p2.y + 2);

        // Línea principal de la ruta
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        g2d.setColor(Colors.PRIMARY);
        g2d.drawLine(p1.x, p1.y, p2.x, p2.y);

        // Línea punteada interna
        float[] dashPattern = {10, 10};
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND, 1.0f,
                dashPattern, 0));
        g2d.setColor(Color.WHITE);
        g2d.drawLine(p1.x, p1.y, p2.x, p2.y);

        // Flecha direccional
        drawArrow(g2d, p1, p2);
    }

    private static void drawArrow(Graphics2D g2d, Point from, Point to) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double angle = Math.atan2(dy, dx);
        int arrowSize = 12;

        // Posición de la flecha (70% del camino)
        int arrowX = (int)(from.x + dx * 0.7);
        int arrowY = (int)(from.y + dy * 0.7);

        Path2D.Double arrow = new Path2D.Double();
        arrow.moveTo(arrowX, arrowY);
        arrow.lineTo(arrowX - arrowSize * Math.cos(angle - Math.PI / 6),
                arrowY - arrowSize * Math.sin(angle - Math.PI / 6));
        arrow.lineTo(arrowX - arrowSize * Math.cos(angle + Math.PI / 6),
                arrowY - arrowSize * Math.sin(angle + Math.PI / 6));
        arrow.closePath();

        g2d.setColor(Colors.PRIMARY);
        g2d.fill(arrow);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1));
        g2d.draw(arrow);
    }
}
