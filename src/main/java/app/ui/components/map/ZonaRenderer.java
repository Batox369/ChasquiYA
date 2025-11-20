package app.ui.components.map;

import app.domain.model.Zona;
import java.awt.*;

public class ZonaRenderer {

    public static void drawZona(Graphics2D g2d, Zona zona, boolean esSeleccionada) {

        // ¡LÓGICA CORREGIDA!
        // Leemos las coordenadas X/Y (que están en las propiedades lon/lat)
        int x = (int) zona.getLongitud();
        int y = (int) zona.getLatitud();

        // El resto de tu código de dibujo
        g2d.setColor(new Color(0, 0, 0, 40));
        g2d.fillOval(x - 7, y - 7, 14, 14);

        g2d.setColor(esSeleccionada ? new Color(230, 57, 70) : new Color(33, 150, 243));
        g2d.fillOval(x - 6, y - 6, 12, 12);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawOval(x - 6, y - 6, 12, 12);

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2d.setColor(Color.BLACK);
        g2d.drawString(zona.getNombre(), x + 10, y - 10);
    }
}