package app.ui.views;

import app.infrastructure.shared.constants.Colors;
import javax.swing.*;
import java.awt.*;

public class TopBar extends JPanel {
    
    private JLabel userNameLabel;

    public TopBar() {
        setLayout(new BorderLayout());
        setBackground(Colors.CARD_BG);
        setPreferredSize(new Dimension(0, 80));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER),
                BorderFactory.createEmptyBorder(0, 35, 0, 55) // Padding horizontal
        ));

        // --- Panel Izquierdo: Logo de la Aplicación ---
        JLabel logoLabel = new JLabel();
        try {
            // Carga la imagen desde la carpeta 'resources'
            java.net.URL imgUrl = getClass().getResource("/logo.png");
            if (imgUrl != null) {
                ImageIcon originalIcon = new ImageIcon(imgUrl);
                // Redimensionamos el logo para que tenga una altura adecuada (ej. 40px)
                Image image = originalIcon.getImage().getScaledInstance(-1, 130, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(image));
            } else {
                System.err.println("Error: No se encontró el archivo /logo.png en la carpeta resources.");
                logoLabel.setText("Movely");
                logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
                logoLabel.setForeground(Colors.PRIMARY);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar el logo: " + e.getMessage());
        }
        add(logoLabel, BorderLayout.WEST);

        // --- Panel Derecho: Nombre de Usuario ---
        // --- ¡CORRECCIÓN! Usamos BorderLayout para un centrado vertical perfecto ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        userNameLabel = new JLabel();
        // La negrita ahora se controlará con HTML, así que usamos una fuente normal.
        userNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        userNameLabel.setForeground(Colors.TEXT_PRIMARY);
        rightPanel.add(userNameLabel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    public void setUserName(String name) {
        if (userNameLabel != null) {
            // --- ¡CORRECCIÓN! Usamos HTML para poner en negrita solo el nombre ---
            userNameLabel.setText("<html>Hola, <b>" + name + "</b></html>");
        }
    }
}