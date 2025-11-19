package app.ui.views;

import app.domain.model.Viaje;
import app.domain.model.Coordenada;
import app.infrastructure.shared.constants.Colors;
import app.infrastructure.shared.constants.UIFonts;
import app.infrastructure.shared.constants.*;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class TripSidebarPanel extends JPanel {

    private JLabel origenLabel;
    private JLabel destinoLabel;
    private JPanel distanciaPanel;
    private JPanel tiempoPanel;
    private JPanel precioPanel;
    private JButton solicitarButton;
    private JButton cancelarButton;
    private JPanel asignandoPanel; // <-- NUEVO: Panel para el estado "Asignando"
    private JPanel conductorPanel; // <-- NUEVO: Panel para mostrar al conductor asignado

    private Viaje viajeActual;

    public TripSidebarPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Colors.CARD_BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Colors.BORDER),
                BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));
        setPreferredSize(new Dimension(250, 0));

        createHeader();
        add(Box.createRigidArea(new Dimension(0, 20)));

        createLocationSection();
        add(Box.createRigidArea(new Dimension(0, 20)));

        createTripInfoSection();
        add(Box.createRigidArea(new Dimension(0, 20)));

        createConductorPanel(); // <-- NUEVO y REUBICADO

        createAsignandoPanel(); // <-- NUEVO: Crear el panel de "Asignando"

        add(Box.createVerticalGlue());

        createActionButtons();

        setEstadoSinViaje();
    }

    private void createHeader() {
        JLabel titleLabel = new JLabel("Información del Viaje");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Colors.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Selecciona origen y destino");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitleLabel.setForeground(Colors.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(subtitleLabel);
    }

    private void createLocationSection() {
        // Panel Origen
        JPanel origenPanel = new JPanel();
        origenPanel.setLayout(new BoxLayout(origenPanel, BoxLayout.Y_AXIS));
        origenPanel.setBackground(Colors.SECONDARY);
        origenPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        origenPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        origenPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel origenTitle = new JLabel("📍 Origen");
        origenTitle.setFont(UIFonts.EMOJI.deriveFont(Font.BOLD, 12f));
        origenTitle.setForeground(Colors.TEXT_PRIMARY);

        origenLabel = new JLabel("No seleccionado");
        origenLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        origenLabel.setForeground(Colors.TEXT_SECONDARY);

        origenPanel.add(origenTitle);
        origenPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        origenPanel.add(origenLabel);

        add(origenPanel);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // Panel Destino
        JPanel destinoPanel = new JPanel();
        destinoPanel.setLayout(new BoxLayout(destinoPanel, BoxLayout.Y_AXIS));
        destinoPanel.setBackground(Colors.SECONDARY);
        destinoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        destinoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        destinoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel destinoTitle = new JLabel("📌 Destino");
        destinoTitle.setFont(UIFonts.EMOJI.deriveFont(Font.BOLD, 12f));
        destinoTitle.setForeground(Colors.TEXT_PRIMARY);

        destinoLabel = new JLabel("No seleccionado");
        destinoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        destinoLabel.setForeground(Colors.TEXT_SECONDARY);

        destinoPanel.add(destinoTitle);
        destinoPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        destinoPanel.add(destinoLabel);

        add(destinoPanel);
    }

    private void createTripInfoSection() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Colors.SECONDARY);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        distanciaPanel = createInfoLabel("📏 Distancia", "-- km");
        infoPanel.add(distanciaPanel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        tiempoPanel = createInfoLabel("⏱️ Tiempo", "-- min");
        infoPanel.add(tiempoPanel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        precioPanel = createInfoLabel("💰 Precio", "S/ --");
        infoPanel.add(precioPanel);

        add(infoPanel);
    }

    private JPanel createInfoLabel(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        // Fuente con soporte para emojis
        Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 11);
        Font valueFont = new Font("Segoe UI", Font.BOLD, 11);

        JLabel labelText = new JLabel(label);
        labelText.setFont(emojiFont);
        labelText.setForeground(Colors.TEXT_SECONDARY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(valueFont);
        valueLabel.setForeground(Colors.TEXT_PRIMARY);

        row.add(labelText, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);

        return row;
    }

    private void createActionButtons() {
        // Botón Solicitar
        solicitarButton = new JButton("Solicitar Viaje");
        solicitarButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        solicitarButton.setBackground(Colors.PRIMARY);
        solicitarButton.setForeground(Color.WHITE);
        solicitarButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        solicitarButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        solicitarButton.setFocusPainted(false);
        solicitarButton.setBorderPainted(false);
        solicitarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        solicitarButton.setEnabled(false);

        solicitarButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (solicitarButton.isEnabled()) {
                    solicitarButton.setBackground(Colors.HOVER);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (solicitarButton.isEnabled()) {
                    solicitarButton.setBackground(Colors.PRIMARY);
                }
            }
        });

        add(solicitarButton);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // Botón Cancelar
        cancelarButton = new JButton("Cancelar");
        cancelarButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cancelarButton.setBackground(Colors.SECONDARY);
        cancelarButton.setForeground(Colors.TEXT_PRIMARY);
        cancelarButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        cancelarButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cancelarButton.setFocusPainted(false);
        cancelarButton.setBorderPainted(false);
        cancelarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        cancelarButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                cancelarButton.setBackground(new Color(220, 220, 220));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                cancelarButton.setBackground(Colors.SECONDARY);
            }
        });

        add(cancelarButton);
    }

    /**
     * Crea el panel que muestra la información del conductor asignado.
     * Inicialmente está oculto.
     */
    private void createConductorPanel() {
        // --- ¡CORREGIDO! Reutilizamos el estilo del panel de información del viaje ---
        conductorPanel = new JPanel(); // Este será el contenedor principal
        conductorPanel.setLayout(new BoxLayout(conductorPanel, BoxLayout.Y_AXIS));
        conductorPanel.setBackground(Colors.SECONDARY);
        conductorPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        conductorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        conductorPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Reutilizamos el método createInfoLabel para mantener la consistencia
        JPanel nombrePanel = createInfoLabel("👤 Conductor", "...");
        conductorPanel.add(nombrePanel);
        conductorPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel placaPanel = createInfoLabel("    Placa", "...");
        conductorPanel.add(placaPanel);

        conductorPanel.setVisible(false); // Oculto por defecto
        add(conductorPanel);
    }
    /**
     * Crea el panel que se muestra mientras se asigna un conductor.
     * Inicialmente está oculto.
     */
    private void createAsignandoPanel() {
        asignandoPanel = new JPanel();
        asignandoPanel.setLayout(new BoxLayout(asignandoPanel, BoxLayout.Y_AXIS));
        asignandoPanel.setOpaque(false);
        asignandoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        asignandoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel asignandoLabel = new JLabel("Asignando conductor...");
        asignandoLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        asignandoLabel.setForeground(Colors.SUCCESS);
        asignandoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        asignandoPanel.add(asignandoLabel);
        asignandoPanel.setVisible(false); // Oculto por defecto
        add(asignandoPanel);
    }

    public void actualizarViaje(Viaje viaje) {
        this.viajeActual = viaje;

        origenLabel.setText(viaje.getNombreOrigen());
        origenLabel.setForeground(Colors.SUCCESS);

        destinoLabel.setText(viaje.getNombreDestino());
        destinoLabel.setForeground(Colors.ERROR);

        updateInfoValue(distanciaPanel, viaje.getDistanciaFormateada());

        int tiempo = calcularTiempo(viaje.getDistanciaMetros());
        updateInfoValue(tiempoPanel, tiempo + " min");

        double precio = calcularPrecio(viaje.getDistanciaMetros());
        updateInfoValue(precioPanel, String.format("S/ %.2f", precio));

        solicitarButton.setEnabled(true);
    }

    private void updateInfoValue(JPanel panel, String newValue) {
        Component[] components = panel.getComponents();
        if (components.length >= 2 && components[1] instanceof JLabel) {
            ((JLabel) components[1]).setText(newValue);
        }
    }

    private int calcularTiempo(double distanciaMetros) {
        double velocidadKmH = 40.0;
        double distanciaKm = distanciaMetros / 1000;
        return (int) Math.ceil((distanciaKm / velocidadKmH) * 60);
    }

    private double calcularPrecio(double distanciaMetros) {
        double tarifaBase = 5.0;
        double costoPorKm = 3.5;
        double distanciaKm = distanciaMetros / 1000;
        return tarifaBase + (distanciaKm * costoPorKm);
    }

    public void setEstadoSinViaje() {
        origenLabel.setText("No seleccionado");
        origenLabel.setForeground(Colors.TEXT_SECONDARY);

        destinoLabel.setText("No seleccionado");
        destinoLabel.setForeground(Colors.TEXT_SECONDARY);

        updateInfoValue(distanciaPanel, "-- km");
        updateInfoValue(tiempoPanel, "-- min");
        updateInfoValue(precioPanel, "S/ --");

        solicitarButton.setEnabled(false);

        // --- ¡CORRECCIÓN! ---
        // Nos aseguramos de que el estado "Asignando..." se revierta.
        solicitarButton.setVisible(true);
        asignandoPanel.setVisible(false);
        conductorPanel.setVisible(false); // Ocultamos también el panel del conductor
        cancelarButton.setVisible(true);
        viajeActual = null;
    }

    /**
     * Cambia la vista del panel al estado "Asignando Conductor".
     * Oculta los detalles del viaje y el botón de solicitar,
     * y muestra el mensaje de asignación.
     */
    public void setEstadoAsignandoConductor() {
        // --- CORRECCIÓN ---
        // Ya no ocultamos la información del viaje. El usuario debe seguir viéndola.
        // Solo ocultamos el botón de solicitar y mostramos el estado de asignación.
        solicitarButton.setVisible(false);

        asignandoPanel.setVisible(true);
    }

    /**
     * Muestra la información del conductor que ha sido asignado al viaje.
     * @param conductor El conductor asignado.
     */
    public void mostrarConductorAsignado(app.domain.model.Conductor conductor) {
        asignandoPanel.setVisible(false); // Ocultamos "Asignando..."

        // Actualizamos los valores usando el mismo método que para el resto de la info
        updateInfoValue((JPanel) conductorPanel.getComponent(0), conductor.getNombreCompleto());
        updateInfoValue((JPanel) conductorPanel.getComponent(2), conductor.getPlacaVehiculo());
        
        conductorPanel.setVisible(true); // Mostramos el panel del conductor
    }
    /**
     * Oculta el botón de cancelar. Se usa cuando el conductor ha llegado al punto de recogida.
     */
    public void ocultarBotonCancelar() {
        cancelarButton.setVisible(false);
    }

    public void addSolicitarListener(ActionListener listener) {
        solicitarButton.addActionListener(listener);
    }

    public void addCancelarListener(ActionListener listener) {
        cancelarButton.addActionListener(listener);
    }

    public Viaje getViajeActual() {
        return viajeActual;
    }
}