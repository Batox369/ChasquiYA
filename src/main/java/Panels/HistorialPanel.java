package Panels;

import Constants.Colors;
import javax.swing.*;
import java.awt.*;

public class HistorialPanel extends JPanel {

    public HistorialPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JLabel label = new JLabel("Historial de Viajes", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(Colors.TEXT_PRIMARY);

        add(label, BorderLayout.CENTER);
    }
}