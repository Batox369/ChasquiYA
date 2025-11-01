package app.ui.panels;

import app.infrastructure.shared.constants.Colors;
import app.ui.components.ModernActionButton;
import app.ui.components.InputField;
import app.ui.components.ZoneSelector; // Puedes reusar este si lo adaptas
import app.ui.components.PrimaryButton;
import app.domain.repository.GrafoRepository;
import app.infrastructure.persistence.MySQLGrafoRepository;
import app.domain.service.GestorGrafos;
import app.domain.service.Sistema;
import app.ui.MainFrame;
import app.domain.model.GrafoZonas; // Importar
import app.domain.model.Zona; // Importar

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList; // Para listas de nombres
import java.util.Collection; // Para colección de zonas

public class AdminMenuPanel extends JPanel {

    // --- Componentes Comunes ---
    private final ModernActionButton btnVolver;
    private final MainFrame mainFrame;

    // --- Componentes Pestaña Conductores ---
    private InputField conductorNombreField;
    private ZoneSelector conductorZoneSelector;
    private PrimaryButton btnAgregarConductor;
    private final Sistema sistema;

    // --- Componentes Pestaña Zonas: Añadir ---
    private InputField zonaNombreField;
    private PrimaryButton btnSeleccionarUbicacion;
    private JLabel lblCoordsSeleccionadas;

    // --- Componentes Pestaña Zonas: Conectar (NUEVO) ---
    private JComboBox<String> cmbZonaOrigen;
    private JComboBox<String> cmbZonaDestino;
    private InputField txtDistancia;
    private PrimaryButton btnConectarZonas;

    // --- Variables ---
    private String nombreZonaPendiente = null;

    public AdminMenuPanel(Sistema sistema, MainFrame mainFrame) {
        this.sistema = sistema;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Panel de Administración", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Colors.TEXT_PRIMARY);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // --- Pestaña 1: Gestión de Conductores ---
        JPanel panelConductores = createPanelConductores(); // Lógica movida a método

        // --- Pestaña 2: Gestión de Zonas ---
        JPanel panelZonas = createPanelZonas(); // Lógica movida a método

        // Añadir pestañas
        tabbedPane.addTab("Gestionar Conductores", panelConductores);
        tabbedPane.addTab("Gestionar Zonas", panelZonas);

        btnVolver = new ModernActionButton("Volver al Menú Principal");

        add(title, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(btnVolver, BorderLayout.SOUTH);

        // --- Listeners ---
        setupConductorListeners();
        setupZonaListeners(); // Mover listeners a este método
    }

    // --- Métodos de Creación de Paneles (para organizar) ---

    private JPanel createPanelConductores() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 15, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        conductorNombreField = new InputField("Nombre del conductor");
        conductorZoneSelector = new ZoneSelector("Zona asignada");
        btnAgregarConductor = new PrimaryButton("Agregar conductor");
        actualizarComboBoxZonasConductores(); // Carga inicial

        panel.add(conductorNombreField);
        panel.add(conductorZoneSelector);
        panel.add(btnAgregarConductor);
        return panel;
    }

    private JPanel createPanelZonas() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 10, 5); // Aumentar espacio vertical
        gbc.gridx = 0;
        gbc.weightx = 1.0; // Permitir que se estiren horizontalmente

        // --- Sección Añadir Zona ---
        gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblAnadir = new JLabel("Añadir Nueva Zona");
        lblAnadir.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(lblAnadir, gbc);

        gbc.gridy++; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        zonaNombreField = new InputField("");
        panel.add(zonaNombreField, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        btnSeleccionarUbicacion = new PrimaryButton("1. Seleccionar Ubicación en Mapa");
        panel.add(btnSeleccionarUbicacion, gbc);

        gbc.gridy++;
        lblCoordsSeleccionadas = new JLabel("Ubicación: (Esperando clic...)");
        lblCoordsSeleccionadas.setForeground(Colors.TEXT_SECONDARY);
        panel.add(lblCoordsSeleccionadas, gbc);

        // Separador visual
        gbc.gridy++; gbc.insets = new Insets(20, 5, 15, 5);
        panel.add(new JSeparator(), gbc);
        gbc.insets = new Insets(5, 5, 10, 5); // Resetear insets

        // --- Sección Conectar Zonas (NUEVO) ---
        gbc.gridy++; gbc.gridwidth = 2;
        JLabel lblConectar = new JLabel("Conectar Zonas Existentes");
        lblConectar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(lblConectar, gbc);

        gbc.gridy++; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Desde Zona:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        cmbZonaOrigen = new JComboBox<>();
        panel.add(cmbZonaOrigen, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Hasta Zona:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        cmbZonaDestino = new JComboBox<>();
        panel.add(cmbZonaDestino, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Distancia (Peso):"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        txtDistancia = new InputField("Ej: 5.5");
        panel.add(txtDistancia, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        btnConectarZonas = new PrimaryButton("Conectar Zonas");
        panel.add(btnConectarZonas, gbc);

        actualizarComboBoxesConexion(); // Carga inicial

        return panel;
    }

    // --- Listeners ---

    private void setupConductorListeners() {
        btnAgregarConductor.addActionListener(e -> {
            // ... (Tu lógica existente para agregar conductor)
        });
    }

    private void setupZonaListeners() {
        // Listener para ir al mapa a seleccionar ubicación
        btnSeleccionarUbicacion.addActionListener(e -> {
            nombreZonaPendiente = zonaNombreField.getText().trim();
            if (nombreZonaPendiente.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese un nombre para la nueva zona primero.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            mainFrame.activarModoColocarZona(this);
        });

        // Listener para conectar zonas (NUEVO)
        btnConectarZonas.addActionListener(e -> {
            String nombreOrigen = (String) cmbZonaOrigen.getSelectedItem();
            String nombreDestino = (String) cmbZonaDestino.getSelectedItem();
            String distStr = txtDistancia.getText().trim();

            if (nombreOrigen == null || nombreDestino == null || nombreOrigen.equals(nombreDestino)) {
                JOptionPane.showMessageDialog(this, "Seleccione dos zonas diferentes.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (distStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese la distancia (peso).", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double distancia;
            try {
                distancia = Double.parseDouble(distStr);
                if (distancia <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "La distancia debe ser un número positivo.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Buscar IDs de las zonas
            GrafoZonas grafo = GestorGrafos.getInstancia().getGrafo();
            // Necesitarás este método en GrafoZonas o buscar manualmente
            Zona zonaA = buscarZonaPorNombreEnGrafo(grafo, nombreOrigen);
            Zona zonaB = buscarZonaPorNombreEnGrafo(grafo, nombreDestino);

            if (zonaA == null || zonaB == null) {
                JOptionPane.showMessageDialog(this, "Error: No se encontraron las zonas seleccionadas.", "Error Interno", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Llamar al Repositorio
            GrafoRepository repo = new MySQLGrafoRepository();
            boolean exito = repo.addConexion(zonaA.getId(), zonaB.getId(), distancia);

            if (exito) {
                JOptionPane.showMessageDialog(this, "Conexión entre '" + nombreOrigen + "' y '" + nombreDestino + "' añadida.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                txtDistancia.setText(""); // Limpiar campo

                // Recargar grafo y repintar mapa
                GestorGrafos.getInstancia().recargarGrafo();
                mainFrame.getMapaPanel().repaint();
                JOptionPane.showMessageDialog(this, "El grafo ha sido actualizado.", "Información", JOptionPane.INFORMATION_MESSAGE);

            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar la conexión. ¿Ya existe?", "Error BD", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Llamado por mapaPanel al seleccionar ubicación.
     */
    public void onUbicacionSeleccionada(double x, double y) {
        if (nombreZonaPendiente == null) return;
        lblCoordsSeleccionadas.setText(String.format("Ubicación: X=%.0f, Y=%.0f", x, y));

        GrafoRepository repo = new MySQLGrafoRepository();
        boolean exito = repo.addZona(nombreZonaPendiente, x, y);

        if (exito) {
            JOptionPane.showMessageDialog(this, "Zona '" + nombreZonaPendiente + "' añadida.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            zonaNombreField.setText("");
            nombreZonaPendiente = null;
            lblCoordsSeleccionadas.setText("Ubicación: (Selecciona nueva ubicación)");

            // Recargar grafo, repintar mapa Y ACTUALIZAR COMBOBOXES
            GestorGrafos.getInstancia().recargarGrafo();
            mainFrame.getMapaPanel().repaint();
            actualizarComboBoxesConexion(); // <-- ACTUALIZAR COMBOBOXES
            actualizarComboBoxZonasConductores(); // <-- ACTUALIZAR COMBOBOX CONDUCTORES
            JOptionPane.showMessageDialog(this, "El grafo ha sido actualizado.", "Información", JOptionPane.INFORMATION_MESSAGE);

        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar la zona. ¿Nombre duplicado?", "Error BD", JOptionPane.ERROR_MESSAGE);
            nombreZonaPendiente = null;
        }
    }

    // --- Métodos Auxiliares ---

    /**
     * Actualiza los JComboBox de la sección "Conectar Zonas".
     */
    private void actualizarComboBoxesConexion() {
        cmbZonaOrigen.removeAllItems();
        cmbZonaDestino.removeAllItems();
        Collection<Zona> zonas = GestorGrafos.getInstancia().getGrafo().getZonas();
        if (zonas != null) {
            for (Zona z : zonas) {
                cmbZonaOrigen.addItem(z.getNombre());
                cmbZonaDestino.addItem(z.getNombre());
            }
        }
    }

    /**
     * Actualiza el ZoneSelector de la pestaña "Conductores".
     */
    private void actualizarComboBoxZonasConductores() {
        // Necesitarás adaptar tu ZoneSelector o usar un JComboBox estándar
        // y obtener la lista de nombres similar a actualizarComboBoxesConexion
        ArrayList<String> nombresZonas = new ArrayList<>();
        Collection<Zona> zonas = GestorGrafos.getInstancia().getGrafo().getZonas();
        if (zonas != null) {
            for (Zona z : zonas) {
                nombresZonas.add(z.getNombre());
            }
        }
        conductorZoneSelector.setZonas(nombresZonas);
    }

    /**
     * Busca una zona por nombre dentro del grafo cargado.
     * (Sería ideal mover esto a la clase GrafoZonas).
     */
    private Zona buscarZonaPorNombreEnGrafo(GrafoZonas grafo, String nombre) {
        if (grafo == null || nombre == null) return null;
        for (Zona z : grafo.getZonas()) {
            if (nombre.equals(z.getNombre())) {
                return z;
            }
        }
        return null;
    }

    public void addVolverListener(ActionListener listener) {
        btnVolver.addActionListener(listener);
    }
}