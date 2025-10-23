package ui.components;

import infrastructure.shared.constants.Colors;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernActionButton extends JButton {

    public ModernActionButton(String text) {
        super(text);
        initButton();
    }

    private void initButton() {
        setFont(new Font("Segoe UI", Font.PLAIN, 16));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(true);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBackground(Colors.PRIMARY);
        setForeground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        setPreferredSize(new Dimension(150, 45));
        setMaximumSize(new Dimension(200, 45));
        setAlignmentX(Component.CENTER_ALIGNMENT);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(Colors.PRIMARY_DARK);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(Colors.PRIMARY);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(Colors.SECONDARY);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setBackground(Colors.PRIMARY);
            }
        });
    }
}
