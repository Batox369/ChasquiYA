package Components;

import Constants.Colors;
import javax.swing.*;
import java.awt.*;

public class ModernButton extends JButton {
    private boolean isSelected;

    public ModernButton(String text, String icon) {
        super(icon + " " + text);
        initButton();
    }

    public ModernButton(String text) {
        super(text);
        initButton();
    }

    private void initButton() {
        setFont(new Font("Segoe UI", Font.PLAIN, 15));
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
        setForeground(Colors.TEXT_PRIMARY);

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
            setForeground(Color.WHITE);
        } else {
            setBackground(Colors.CARD_BG);
            setForeground(Colors.TEXT_PRIMARY);
        }
    }
}
