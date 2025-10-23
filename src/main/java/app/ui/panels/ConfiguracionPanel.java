package app.ui.panels;

import app.infrastructure.shared.constants.Colors;
import javax.swing.*;
import java.awt.*;

public class ConfiguracionPanel extends JPanel {

    public ConfiguracionPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JLabel label = new JLabel("Configuración", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(Colors.TEXT_PRIMARY);

        add(label, BorderLayout.CENTER);
    }
}