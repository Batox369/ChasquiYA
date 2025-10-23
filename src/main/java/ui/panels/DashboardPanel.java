package ui.panels;

import infrastructure.shared.constants.Colors;
import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JLabel label = new JLabel("Dashboard de Viajes", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(Colors.TEXT_PRIMARY);

        add(label, BorderLayout.CENTER);
    }
}
