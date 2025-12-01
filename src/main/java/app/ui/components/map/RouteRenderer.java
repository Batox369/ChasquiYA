package app.ui.components.map;

import app.domain.model.Zona;
import app.infrastructure.shared.constants.Colors;
import java.awt.geom.Line2D;
import java.awt.*;
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
            drawStraightSegment(g2d, x1, y1, x2, y2, zoom);
        }
    }

    private static void drawStraightSegment(Graphics2D g2d, int x1, int y1, int x2, int y2, double zoom) {
        // --- DISEÑO SIMPLIFICADO ---
        // Se dibuja una única línea azul sólida.
        
        // 1. Definimos el color de la línea.
        g2d.setColor(Colors.PRIMARY);
        
        // 2. Calculamos el grosor para que se vea constante en pantalla, sin importar el zoom.
        // Un grosor base de 3.5px que se ajusta inversamente al zoom.
        float grosor = (float) (3.5 / zoom);
        g2d.setStroke(new BasicStroke(grosor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        // 3. Dibujamos la línea recta.
        g2d.draw(new Line2D.Double(x1, y1, x2, y2));
    }
}