package Panels;

import Constants.Colors;
import Components.ModernActionButton;
import Components.InputField;
import Components.ZoneSelector;
import Components.PrimaryButton;
import Models.Sistema;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AdminMenuPanel extends JPanel {
    private final ModernActionButton btnVolver;
    private final InputField nombreField;
    private final ZoneSelector zoneSelector;
    private final PrimaryButton btnAgregar;

    public AdminMenuPanel(Sistema sistema) {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Gestión de Conductores", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Colors.TEXT_PRIMARY);

        // Panel central con los inputs
        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        centerPanel.setOpaque(false);

        nombreField = new InputField("Nombre del conductor");
        zoneSelector = new ZoneSelector("Zona asignada");
        btnAgregar = new PrimaryButton("Agregar conductor");

        // Inicializar zonas si existen
        zoneSelector.setZonas(sistema.obtenerNombresZonas());

        centerPanel.add(nombreField);
        centerPanel.add(zoneSelector);
        centerPanel.add(btnAgregar);

        btnVolver = new ModernActionButton("Volver");

        add(title, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(btnVolver, BorderLayout.SOUTH);

        // Listener del botón agregar
        btnAgregar.addActionListener(e -> {
            String nombre = nombreField.getText().trim();
            String zona = zoneSelector.getZonaSeleccionada();

            System.out.println("== DEPURACIÓN ==");
            System.out.println("Nombre ingresado: " + nombre);
            System.out.println("Zona seleccionada: " + zona);

            if (nombre.isEmpty()) {
                System.out.println("⚠️ Falta ingresar el nombre del conductor.");
                JOptionPane.showMessageDialog(this, "Debe ingresar un nombre.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (zona == null || zona.isEmpty()) {
                System.out.println("⚠️ No hay zona seleccionada o no existen zonas.");
                JOptionPane.showMessageDialog(this, "Debe seleccionar una zona.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            sistema.agregarConductor(nombre, zona);

            System.out.println("✅ Conductor agregado correctamente.");
            sistema.imprimirZonasYConductores();

            nombreField.setText("");
            zoneSelector.setSelectedIndex(0);
        });
    }

    public void addVolverListener(ActionListener listener) {
        btnVolver.addActionListener(listener);
    }
}
