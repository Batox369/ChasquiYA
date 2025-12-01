package app.ui.components.modern;

import app.infrastructure.shared.constants.Colors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.RoundRectangle2D;

public class ModernTextField extends JTextField {

    private String placeholder = "";
    private Color borderColor = Colors.BORDER;
    private final Color focusColor = Colors.PRIMARY;
    private final int cornerRadius = 8;

    public ModernTextField(int columns) {
        super(columns);
        setOpaque(false); // We will paint our own background
        setBackground(new Color(241, 245, 249)); // Un fondo ligeramente gris
        setForeground(Colors.TEXT_PRIMARY);
        setCaretColor(Colors.TEXT_PRIMARY);
        setFont(new Font("SansSerif", Font.PLAIN, 14));
        setBorder(new EmptyBorder(12, 15, 12, 15)); // Internal padding

        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                borderColor = focusColor;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                borderColor = Colors.BORDER;
                repaint();
            }
        });
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Paint background
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

        // Paint placeholder if needed
        if (getPlaceholder() != null && !getPlaceholder().isEmpty() && getText().isEmpty()) {
            g2.setColor(Colors.TEXT_SECONDARY);
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(getPlaceholder(), getInsets().left, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
        }

        g2.dispose();

        // Let the original component paint the text and caret
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(borderColor);
        // The '1' is the stroke width. We subtract 1 from width/height to keep the border inside.
        g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));
        g2.dispose();
    }
}