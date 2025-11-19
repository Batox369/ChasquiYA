package app.ui.components;

import app.infrastructure.shared.constants.Colors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class SecondaryButton extends JButton {

    private Color defaultColor = new Color(226, 232, 240); // Light gray
    private Color hoverColor = new Color(203, 213, 224);   // Darker gray
    private Color pressedColor = new Color(160, 174, 192); // Even darker
    private int cornerRadius = 8;
    private boolean isHovered = false;

    public SecondaryButton(String text) {
        super(text);
        setFont(new Font("SansSerif", Font.BOLD, 14));
        setForeground(Colors.TEXT_PRIMARY); // Dark text
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);

        setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));

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

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bgColor;
        if (getModel().isPressed()) {
            bgColor = pressedColor;
        } else if (isHovered) {
            bgColor = hoverColor;
        } else {
            bgColor = defaultColor;
        }

        g2.setColor(bgColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

        g2.dispose();
        super.paintComponent(g);
    }
}