package app.ui.components;

import app.domain.model.Zona;
import app.infrastructure.shared.constants.Colors;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.QuadCurve2D;
import java.util.List;

public class RouteRenderer {

    public static void drawRuta(Graphics2D g2d, List<Zona> ruta, double zoom) {
        if (ruta == null || ruta.size() < 2) return;

        for (int i = 0; i < ruta.size() - 1; i++) {
            Zona a = ruta.get(i);
            Zona b = ruta.get(i + 1);

            // --- ¡LÓGICA CORREGIDA! ---
            // Leemos las coordenadas X/Y (lon/lat) de la imagen
            int x1 = (int) a.getLongitud();
            int y1 = (int) a.getLatitud();
            int x2 = (int) b.getLongitud();
            int y2 = (int) b.getLatitud();

            // Pasamos el zoom SÓLO para el estilo/grosor
            drawCurvedSegment(g2d, x1, y1, x2, y2, zoom, i);
        }
    }

    // (El resto de tu clase: drawCurvedSegment y drawArrow...
    //  necesitan ser ajustados para el nuevo zoom)

    private static void drawCurvedSegment(Graphics2D g2d, int x1, int y1, int x2, int y2, double zoom, int index) {
        int ctrlX = (x1 + x2) / 2 + (int)(Math.sin(index * Math.PI / 3) * 20);
        int ctrlY = (y1 + y2) / 2 - (int)(Math.cos(index * Math.PI / 3) * 20);

        GradientPaint grad = new GradientPaint(x1, y1, Colors.PRIMARY,
                x2, y2, new Color(30, 136, 229), true);

        // --- AJUSTE DE GROSOR ---
        // Dividimos por el zoom para que el grosor sea constante
        float grosorSombra = (float)(6 / zoom);
        float grosorLinea = (float)(3.5 / zoom);
        float grosorPunteado = (float)(1.2 / zoom);

        // 🔹 Sombra
        g2d.setStroke(new BasicStroke(grosorSombra, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setColor(new Color(0, 0, 0, 40));
        g2d.draw(new QuadCurve2D.Double(x1 + 2, y1 + 2, ctrlX + 2, ctrlY + 2, x2 + 2, y2 + 2));

        // 🔹 Línea principal
        g2d.setStroke(new BasicStroke(grosorLinea, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setPaint(grad);
        g2d.draw(new QuadCurve2D.Double(x1, y1, ctrlX, ctrlY, x2, y2));

        // 🔹 Línea punteada
        float[] dashPattern = {12.0f, 12.0f}; // Patrón base
        g2d.setStroke(new BasicStroke(grosorPunteado,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f,
                dashPattern, 0));
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.draw(new QuadCurve2D.Double(x1, y1, ctrlX, ctrlY, x2, y2));

        // 🔹 Flecha direccional
        drawArrow(g2d, new Point(x1, y1), new Point(x2, y2), zoom);
    }

    private static void drawArrow(Graphics2D g2d, Point from, Point to, double zoom) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double angle = Math.atan2(dy, dx);

        // --- AJUSTE DE TAMAÑO ---
        int arrowSize = (int)(12 / zoom); // Flecha se re-escala
        float grosorFlecha = (float)(1 / zoom); // Borde se re-escala

        int arrowX = (int)(from.x + dx * 0.75);
        int arrowY = (int)(from.y + dy * 0.75);

        Path2D.Double arrow = new Path2D.Double();
        arrow.moveTo(arrowX, arrowY);
        arrow.lineTo(arrowX - arrowSize * Math.cos(angle - Math.PI / 6),
                arrowY - arrowSize * Math.sin(angle - Math.PI / 6));
        arrow.lineTo(arrowX - arrowSize * Math.cos(angle + Math.PI / 6),
                arrowY - arrowSize * Math.sin(angle + Math.PI / 6));
        arrow.closePath();

        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.fill(arrow);
        g2d.setColor(Colors.PRIMARY);
        g2d.fill(arrow);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(grosorFlecha));
        g2d.draw(arrow);
    }
}