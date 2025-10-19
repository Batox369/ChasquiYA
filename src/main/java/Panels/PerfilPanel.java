package Panels;

import Constants.Colors;
import javax.swing.*;
import java.awt.*;

public class PerfilPanel extends JPanel {

    public PerfilPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JLabel label = new JLabel("Mi Perfil", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(Colors.TEXT_PRIMARY);

        add(label, BorderLayout.CENTER);
    }
}