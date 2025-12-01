package app.ui.components.map;

import app.domain.model.GrafoZonas;
import app.infrastructure.shared.constants.Colors;
import app.domain.model.Zona;

import java.awt.*;
import java.awt.geom.Line2D;

public class AristaRenderer {

    /**
     * Dibuja una representación visual de una conexión (arista) en el mapa.
     * @param g2d El contexto gráfico donde se dibujará.
     * @param origen La zona de origen de la conexión.
     * @param conexion La conexión a dibujar
     * @param mostrarPeso Si es true, dibujará el texto con la distancia.
     */
    public static void drawArista(Graphics2D g2d, Zona origen, GrafoZonas.Conexion conexion, boolean mostrarPeso) {
        Zona destino = conexion.getDestino();

        // Coordenadas de los centros de las zonas
        double x1 = origen.getLongitud();
        double y1 = origen.getLatitud();
        double x2 = destino.getLongitud();
        double y2 = destino.getLatitud();

        // Usamos un color gris neutral para no confundir con la ruta del viaje
        g2d.setColor(new Color(100, 116, 139, 180));
        g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(new Line2D.Double(x1, y1, x2, y2));

        if (mostrarPeso) {
            // Dibuja la distancia en el punto medio de la línea
            g2d.setFont(new Font("SansSerif", Font.BOLD, 10));

            // Calcula el punto medio
            float midX = (float) ((x1 + x2) / 2);
            float midY = (float) ((y1 + y2) / 2);

            // Dibuja un pequeño fondo para la etiqueta de texto para que sea legible
            String text = String.format("%.0f m", conexion.getDistancia());
            int textWidth = g2d.getFontMetrics().stringWidth(text);
            g2d.setColor(new Color(241, 245, 249, 200)); // Fondo blanco semi-transparente
            g2d.fillRect((int) (midX - textWidth / 2.0 - 2), (int) (midY - 10), textWidth + 4, 12);

            // Dibuja el texto de la distancia
            g2d.setColor(new Color(51, 65, 85));
            g2d.drawString(text, midX - textWidth / 2.0f, midY);
        }
    }
}