package app.ui.components;

import app.infrastructure.shared.constants.Colors;
import javax.swing.*;
import java.awt.*;

public class ModernButton extends JButton {
    private boolean isSelected;
    private JLabel label;

    public ModernButton(String text, String emoji) {
        super();
        label = new JLabel(emoji + "  " + text);
        label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        label.setForeground(Colors.TEXT_PRIMARY);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        setLayout(new BorderLayout());
        add(label, BorderLayout.CENTER);
        initButton();
    }

    public ModernButton(String text) {
        this(text, "");
    }

    private void initButton() {
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setMaximumSize(new Dimension(220, 45));
        setPreferredSize(new Dimension(220, 45));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(true);
        setHorizontalAlignment(SwingConstants.LEFT);
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        setBackground(Colors.CARD_BG);

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!isSelected) {
                    setBackground(Colors.SECONDARY);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!isSelected) {
                    setBackground(Colors.CARD_BG);
                }
            }
        });
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        if (selected) {
            setBackground(Colors.PRIMARY);
            label.setForeground(Color.WHITE);
        } else {
            setBackground(Colors.CARD_BG);
            label.setForeground(Colors.TEXT_PRIMARY);
        }
    }
}

