package app.ui.components.modern;

import app.infrastructure.shared.constants.Colors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public class ModernCheckBox extends JCheckBox {

    private final int boxSize = 18;
    private final int cornerRadius = 4;
    private boolean isHovered = false;

    public ModernCheckBox(String text) {
        super(text);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFont(new Font("SansSerif", Font.PLAIN, 14));
        setForeground(Colors.TEXT_PRIMARY);
        setIconTextGap(12);

        // Usaremos nuestro propio icono personalizado para dibujar el checkbox
        setIcon(new CheckBoxIcon());

        // Eliminamos el pintado de foco por defecto
        setFocusPainted(false);

        // Añadimos un listener para el efecto hover
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    private class CheckBoxIcon implements Icon {

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Centramos la caja verticalmente
            int boxY = y + (getIconHeight() - boxSize) / 2;

            RoundRectangle2D box = new RoundRectangle2D.Float(x, boxY, boxSize, boxSize, cornerRadius, cornerRadius);

            if (isSelected()) {
                // --- Estado Marcado ---
                g2.setColor(Colors.PRIMARY);
                g2.fill(box);

                // Dibujamos el check (✓)
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                Path2D.Float check = new Path2D.Float();
                check.moveTo(x + 5, boxY + 9);
                check.lineTo(x + 8, boxY + 12);
                check.lineTo(x + 13, boxY + 6);
                g2.draw(check);

            } else {
                // --- Estado No Marcado ---
                g2.setColor(isHovered ? Colors.PRIMARY : Colors.BORDER);
                g2.setStroke(new BasicStroke(2f));
                g2.draw(box);
            }

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return boxSize;
        }

        @Override
        public int getIconHeight() {
            return boxSize;
        }
    }
}