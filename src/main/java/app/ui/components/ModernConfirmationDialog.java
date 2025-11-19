package app.ui.components;

import app.infrastructure.shared.constants.Colors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class ModernConfirmationDialog extends JDialog {

    private boolean confirmed = false;

    public ModernConfirmationDialog(Frame owner, String title, String message) {
        super(owner, title, true); // true for modal

        // --- Estilo del Diálogo ---
        setUndecorated(true); // Sin bordes ni barra de título del sistema operativo
        setSize(400, 200);
        setLocationRelativeTo(owner);
        getRootPane().setBorder(BorderFactory.createLineBorder(Colors.BORDER, 1));

        // --- Contenido ---
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(Colors.CARD_BG);
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        setContentPane(contentPanel);

        // Mensaje
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        messageLabel.setForeground(Colors.TEXT_PRIMARY);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(messageLabel, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        PrimaryButton confirmButton = new PrimaryButton("Sí, cerrar sesión");
        SecondaryButton cancelButton = new SecondaryButton("Cancelar");

        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // --- Acciones ---
        confirmButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
    }

    /**
     * Muestra el diálogo y devuelve true si el usuario confirmó, o false si canceló.
     * @return boolean La elección del usuario.
     */
    public boolean showDialog() {
        setVisible(true);
        return confirmed;
    }
}