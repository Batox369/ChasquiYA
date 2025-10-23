package app.ui.views;

import app.domain.model.Viaje;
import app.infrastructure.shared.constants.Colors;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class TripInfoPanel extends JPanel {
    private JLabel distanciaLabel;
    private JLabel tiempoLabel;
    private JLabel precioLabel;
    private JButton solicitarButton;
    private JButton cancelarButton;

    public TripInfoPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
        setBackground(new Color(255, 255, 255, 250));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));

        // Icono
        JLabel iconLabel = new JLabel("🚗");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        // Panel de información
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        // Distancia
        distanciaLabel = new JLabel("Distancia: --");
        distanciaLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        distanciaLabel.setForeground(Colors.TEXT_PRIMARY);
        distanciaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Tiempo estimado
        tiempoLabel = new JLabel("Tiempo: --");
        tiempoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tiempoLabel.setForeground(Colors.TEXT_SECONDARY);
        tiempoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Precio estimado
        precioLabel = new JLabel("Precio: S/ --");
        precioLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        precioLabel.setForeground(Colors.PRIMARY);
        precioLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(distanciaLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        infoPanel.add(tiempoLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        infoPanel.add(precioLabel);

        // Botón Solicitar
        solicitarButton = new JButton("Solicitar Viaje");
        solicitarButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        solicitarButton.setBackground(Colors.PRIMARY);
        solicitarButton.setForeground(Color.WHITE);
        solicitarButton.setFocusPainted(false);
        solicitarButton.setBorderPainted(false);
        solicitarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        solicitarButton.setPreferredSize(new Dimension(150, 40));

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
        cancelarButton.setPreferredSize(new Dimension(100, 40));

        cancelarButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                cancelarButton.setBackground(new Color(220, 220, 220));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                cancelarButton.setBackground(Colors.SECONDARY);
            }
        });

        add(iconLabel);
        add(infoPanel);
        add(solicitarButton);
        add(cancelarButton);
    }

    public void actualizarViaje(Viaje viaje, String tipoServicio) {
        distanciaLabel.setText("Distancia: " + viaje.getDistanciaFormateada());

        // Calcular tiempo
        int tiempo = calcularTiempoEstimado(viaje.getDistanciaMetros(), tipoServicio);
        tiempoLabel.setText("Tiempo: " + tiempo + " min");

        // Calcular precio
        double precio = calcularPrecio(viaje.getDistanciaMetros(), tipoServicio);
        precioLabel.setText("Precio: S/ " + String.format("%.2f", precio));
    }

    private int calcularTiempoEstimado(double distanciaMetros, String tipoServicio) {
        double velocidadKmH = tipoServicio.equals("Premium") ? 50 : 40;
        double distanciaKm = distanciaMetros / 1000;
        return (int) Math.ceil((distanciaKm / velocidadKmH) * 60);
    }

    private double calcularPrecio(double distanciaMetros, String tipoServicio) {
        double tarifaBase = 5.0;
        double costoPorKm = 2.5;

        switch (tipoServicio) {
            case "Premium":
                tarifaBase = 8.0;
                costoPorKm = 4.0;
                break;
            case "Compartido":
                tarifaBase = 3.0;
                costoPorKm = 1.5;
                break;
        }

        double distanciaKm = distanciaMetros / 1000;
        return tarifaBase + (distanciaKm * costoPorKm);
    }

    public void addSolicitarListener(ActionListener listener) {
        solicitarButton.addActionListener(listener);
    }

    public void addCancelarListener(ActionListener listener) {
        cancelarButton.addActionListener(listener);
    }
}