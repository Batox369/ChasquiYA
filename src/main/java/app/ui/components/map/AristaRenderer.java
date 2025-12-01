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
     * @param conexion La conexión a dibujar.
     */
    public static void drawArista(Graphics2D g2d, Zona origen, GrafoZonas.Conexion conexion) {
        Zona destino = conexion.getDestino();

        // Coordenadas de los centros de las zonas
        double x1 = origen.getLongitud();
        double y1 = origen.getLatitud();
        double x2 = destino.getLongitud();
        double y2 = destino.getLatitud();

        // Dibuja la línea de la conexión según el estado del tráfico
        if (conexion.tieneTrafico()) {
            g2d.setColor(new Color(245, 158, 11)); // Color amarillo/naranja para el tráfico
        } else {
            g2d.setColor(Colors.PRIMARY); // Color azul normal
        }

        g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); // Un poco más gruesa
        g2d.draw(new Line2D.Double(x1, y1, x2, y2));
    }
}