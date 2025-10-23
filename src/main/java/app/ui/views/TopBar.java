package app.ui.views;

import app.infrastructure.shared.constants.Colors;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class TopBar extends JPanel {

    private JButton userButton;
    private JLabel userLabel; // <-- CAMBIO 1: Convertido en variable de instancia

    public TopBar(String appName, String userName) {
        setLayout(new BorderLayout());
        setBackground(Colors.CARD_BG);
        setPreferredSize(new Dimension(1020, 70));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER),
                BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));

        // ---------- PANEL IZQUIERDO ----------
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("🚗  " + appName);
        titleLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
        titleLabel.setForeground(Colors.PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        leftPanel.add(titleLabel);

        // ---------- PANEL DERECHO ----------
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);

        // CAMBIO 2: Se quita el "JLabel" para asignar a la variable de instancia
        userLabel = new JLabel("👤  " + userName);
        userLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        userLabel.setForeground(Colors.TEXT_PRIMARY);
        userLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        userButton = new JButton();
        userButton.setLayout(new BorderLayout());
        userButton.setFocusPainted(false);
        userButton.setBorderPainted(false);
        userButton.setContentAreaFilled(false);
        userButton.add(userLabel, BorderLayout.CENTER);

        rightPanel.add(userButton);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    public void addAdminButtonListener(ActionListener listener) {
        userButton.addActionListener(listener);
    }

    // --- CAMBIO 3: MÉTODO NUEVO AÑADIDO ---
    /**
     * Actualiza el nombre del usuario en la barra superior.
     * @param name El nuevo nombre a mostrar.
     */
    public void setUserName(String name) {
        if (name.length() > 20) { // Acorta nombres largos
            name = name.substring(0, 19) + "...";
        }

        if (userLabel != null) {
            userLabel.setText("👤  " + name);
        }
    }
}