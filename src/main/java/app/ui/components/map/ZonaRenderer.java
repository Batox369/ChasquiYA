package app.ui.components.map;

import app.domain.model.Zona;
import java.awt.*;

public class ZonaRenderer {
    public static void drawZona(Graphics2D g2d, Zona zona, boolean esSeleccionada, boolean esForzadoVisible) {

        // ¡LÓGICA CORREGIDA!
        // Leemos las coordenadas X/Y (que están en las propiedades lon/lat)
        int x = (int) zona.getLongitud();
        int y = (int) zona.getLatitud();

        // --- ¡NUEVO! ---
        // Si la zona es invisible pero se está forzando su visualización, la pintamos diferente.
        Color colorPrincipal = esSeleccionada ? new Color(230, 57, 70) : new Color(33, 150, 243);
        if (esForzadoVisible) {
            // Hacemos el color semi-transparente para indicar que es un nodo invisible
            colorPrincipal = new Color(colorPrincipal.getRed(), colorPrincipal.getGreen(), colorPrincipal.getBlue(), 100);
        }

        // El resto de tu código de dibujo
        g2d.setColor(new Color(0, 0, 0, 40));
        g2d.fillOval(x - 7, y - 7, 14, 14);

        g2d.setColor(colorPrincipal);
        g2d.fillOval(x - 6, y - 6, 12, 12);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawOval(x - 6, y - 6, 12, 12);

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2d.setColor(Color.BLACK);
        g2d.drawString(zona.getNombre(), x + 10, y - 10);
    }
}