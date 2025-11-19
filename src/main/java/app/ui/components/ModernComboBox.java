package app.ui.components;

import app.infrastructure.shared.constants.Colors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ModernComboBox<E> extends JComboBox<E> {

    private Color borderColor = Colors.BORDER;
    private final Color focusColor = Colors.PRIMARY;
    private final int cornerRadius = 8;

    public ModernComboBox() {
        super();
        setOpaque(false);
        setBackground(new Color(241, 245, 249));
        setForeground(Colors.TEXT_PRIMARY);
        setFont(new Font("SansSerif", Font.PLAIN, 14));
        setBorder(new EmptyBorder(12, 15, 12, 15));

        setUI(new ModernComboBoxUI());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (isFocusOwner()) {
            g2.setColor(focusColor);
        } else {
            g2.setColor(borderColor);
        }
        g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));
        g2.dispose();
    }

    // Inner class for the custom UI
    private static class ModernComboBoxUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            // Create a custom arrow button
            JButton button = new JButton("▼"); // Simple text arrow
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setContentAreaFilled(false);
            button.setForeground(Colors.TEXT_SECONDARY);
            button.setFont(new Font("SansSerif", Font.BOLD, 12));
            return button;
        }

        @Override
        protected ComboPopup createPopup() {
            BasicComboPopup popup = (BasicComboPopup) super.createPopup();
            popup.setBorder(BorderFactory.createLineBorder(Colors.BORDER, 1));
            return popup;
        }
    }
}