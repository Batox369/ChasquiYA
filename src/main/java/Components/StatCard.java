package Components;

import Constants.Colors;
import javax.swing.*;
import java.awt.*;

public class StatCard extends JPanel {

    public StatCard(String title, String value, String icon, Color accentColor) {
        setLayout(new GridBagLayout());
        setBackground(Colors.CARD_BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        // Ícono (emoji)
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setForeground(accentColor);

        // Panel de texto (título y valor)
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(Colors.TEXT_SECONDARY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(Colors.TEXT_PRIMARY);

        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(valueLabel);

        // Configuración de layout flexible
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 15); // espacio entre icono y texto
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 0;

        // Ícono en la columna 0
        gbc.gridx = 0;
        add(iconLabel, gbc);

        // Texto en la columna 1
        gbc.gridx = 1;
        add(textPanel, gbc);
    }
}
