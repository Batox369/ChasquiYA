package Components;

import Constants.Colors;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class TopBar extends JPanel {

    private JButton userButton; // 👈 guardamos el botón como atributo

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

        //JLabel logoLabel = new JLabel("🚗");
        //logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        JLabel titleLabel = new JLabel(appName);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Colors.PRIMARY);

        //leftPanel.add(logoLabel);
        leftPanel.add(titleLabel);

        // Panel derecho con usuario
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        userButton = new JButton(userName);
        userButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userButton.setForeground(Colors.TEXT_PRIMARY);
        userButton.setFocusPainted(false);
        userButton.setContentAreaFilled(false);
        userButton.setBorderPainted(false);
        userButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel avatarLabel = new JLabel("");
        avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        avatarLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        rightPanel.add(userButton);
        rightPanel.add(avatarLabel);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    public void addAdminButtonListener(ActionListener listener) {
        userButton.addActionListener(listener);
    }
}
