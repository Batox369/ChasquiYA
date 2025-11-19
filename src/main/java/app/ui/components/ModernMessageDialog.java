package app.ui.components;

import app.infrastructure.shared.constants.Colors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ModernMessageDialog extends JDialog {

    public enum MessageType {
        SUCCESS,
        INFO,
        WARNING,
        ERROR
    }

    public ModernMessageDialog(Frame owner, String title, String message, MessageType type) {
        super(owner, title, true); // modal

        setUndecorated(true);
        setSize(420, 220);
        setLocationRelativeTo(owner);
        getRootPane().setBorder(BorderFactory.createLineBorder(Colors.BORDER, 1));

        // --- Panel Principal ---
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Colors.CARD_BG); // <-- SOLUCIÓN: Asegura un fondo blanco consistente
        setContentPane(mainPanel);
        GridBagConstraints gbc = new GridBagConstraints();

        // --- Panel Izquierdo (Icono) ---
        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setOpaque(true); // <-- ¡SOLUCIÓN! Fuerza al panel a pintar su propio color de fondo.

        gbc.gridx = 0;
        gbc.gridy = 0;
        // --- ¡SOLUCIÓN! Restauramos la distribución porcentual ---
        gbc.weightx = 0.35; // 35% del ancho para el ícono
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(iconPanel, gbc);

        JLabel iconLabel = new JLabel("", SwingConstants.CENTER);

        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 55));
        iconLabel.setForeground(Color.WHITE); // EMOJIS BLANCOS
        iconPanel.add(iconLabel, BorderLayout.CENTER);

        // --- Panel Derecho ---
        JPanel detailsPanel = new JPanel(new BorderLayout(0, 15));
        detailsPanel.setBackground(Colors.CARD_BG);
        detailsPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        gbc.gridx = 1; // Siguiente columna
        gbc.weightx = 0.65; // 65% del ancho para los detalles
        gbc.weighty = 1.0; // Ocupa todo el alto
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(detailsPanel, gbc);

        // --- Título ---
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        switch (type) {
            case SUCCESS:
                iconLabel.setText("✅");
                iconPanel.setBackground(Colors.SUCCESS);
                titleLabel.setForeground(Colors.SUCCESS);
                break;
            case INFO:
                iconLabel.setText("❕");
                iconPanel.setBackground(Colors.PRIMARY);
                titleLabel.setForeground(Colors.PRIMARY);
                break;
            case WARNING:
                iconLabel.setText("⚠");
                iconPanel.setBackground(new Color(245, 158, 11));
                titleLabel.setForeground(new Color(245, 158, 11));
                break;
            case ERROR:
                iconLabel.setText("❌");
                iconPanel.setBackground(Colors.ERROR);
                titleLabel.setForeground(Colors.ERROR);
                break;
        }

        detailsPanel.add(titleLabel, BorderLayout.NORTH);

        // --- Mensaje ---
        JLabel messageLabel = new JLabel("<html><div style='width:100%;'>" + message + "</div></html>");
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        messageLabel.setForeground(Colors.TEXT_PRIMARY);
        detailsPanel.add(messageLabel, BorderLayout.CENTER);

        // --- Botón ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);

        PrimaryButton okButton = new PrimaryButton("Aceptar");
        buttonPanel.add(okButton);

        detailsPanel.add(buttonPanel, BorderLayout.SOUTH);

        okButton.addActionListener(e -> dispose());
    }

    public void showDialog() {
        setVisible(true);
    }
}
