package app.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Un componente simple que dibuja un círculo de un color específico.
 * Usado para mostrar el color asignado a un conductor.
 */
public class ColorSwatch extends JComponent {
    private Color color = Color.GRAY;

    public ColorSwatch() {
        setPreferredSize(new Dimension(12, 12));
    }

    public void setColor(Color color) {
        this.color = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fillOval(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}