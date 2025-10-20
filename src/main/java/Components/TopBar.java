package Components;

import Constants.Colors;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class TopBar extends JPanel {

    private JButton userButton;

    public TopBar(String appName, String userName) {
        setLayout(new BorderLayout());
        setBackground(Colors.CARD_BG);
        setPreferredSize(new Dimension(1020, 70));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER),
                BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));

        // Panel izquierdo con logo
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);

        JLabel logoLabel = new JLabel("🚗");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JLabel titleLabel = new JLabel(appName);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Colors.PRIMARY);

        leftPanel.add(logoLabel);
        leftPanel.add(titleLabel);

        // Panel derecho con usuario (centrado verticalmente)
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 5, 0, 5);

        userButton = new JButton(userName);
        userButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userButton.setForeground(Colors.TEXT_PRIMARY);
        userButton.setFocusPainted(false);
        userButton.setContentAreaFilled(false);
        userButton.setBorderPainted(false);
        userButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel avatarLabel = new JLabel("👤");
        avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        // Añadir con separación entre ellos
        gbc.gridx = 0;
        rightPanel.add(userButton, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 8, 0, 0);
        rightPanel.add(avatarLabel, gbc);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    public void addAdminButtonListener(ActionListener listener) {
        userButton.addActionListener(listener);
    }
}
