package Components;

import Models.Viaje;
import Constants.Colors;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class TripInfoPanel extends JPanel {
    private JLabel distanciaLabel;
    private JButton solicitarButton;
    private JButton cancelarButton;

    public TripInfoPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        setBackground(new Color(255, 255, 255, 250));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Icono
        JLabel iconLabel = new JLabel("🚗");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        // Distancia
        distanciaLabel = new JLabel("Distancia: --");
        distanciaLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        distanciaLabel.setForeground(Colors.TEXT_PRIMARY);

        // Botón Solicitar
        solicitarButton = new JButton("Solicitar Viaje");
        solicitarButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        solicitarButton.setBackground(Colors.PRIMARY);
        solicitarButton.setForeground(Color.WHITE);
        solicitarButton.setFocusPainted(false);
        solicitarButton.setBorderPainted(false);
        solicitarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        solicitarButton.setPreferredSize(new Dimension(150, 35));

        solicitarButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                solicitarButton.setBackground(Colors.HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                solicitarButton.setBackground(Colors.PRIMARY);
            }
        });

        // Botón Cancelar
        cancelarButton = new JButton("Cancelar");
        cancelarButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelarButton.setBackground(Colors.SECONDARY);
        cancelarButton.setForeground(Colors.TEXT_PRIMARY);
        cancelarButton.setFocusPainted(false);
        cancelarButton.setBorderPainted(false);
        cancelarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelarButton.setPreferredSize(new Dimension(100, 35));

        cancelarButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                cancelarButton.setBackground(new Color(220, 220, 220));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                cancelarButton.setBackground(Colors.SECONDARY);
            }
        });

        add(iconLabel);
        add(distanciaLabel);
        add(solicitarButton);
        add(cancelarButton);
    }

    public void actualizarViaje(Viaje viaje) {
        distanciaLabel.setText("Distancia: " + viaje.getDistanciaFormateada());
    }

    public void addSolicitarListener(ActionListener listener) {
        solicitarButton.addActionListener(listener);
    }

    public void addCancelarListener(ActionListener listener) {
        cancelarButton.addActionListener(listener);
    }
}