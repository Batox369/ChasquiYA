package app.ui.panels;

import app.infrastructure.shared.constants.Colors;
import app.ui.components.InputField;
import app.ui.components.ModernScrollBarUI;
import app.ui.components.ModernActionButton;
import app.ui.components.PrimaryButton;
import app.ui.components.ZoneSelector;
import app.domain.model.GrafoZonas;
import app.domain.model.Zona;
import app.domain.repository.ConductorRepository;
import app.domain.repository.GrafoRepository;
import app.domain.service.GestorGrafos;
import app.domain.service.Sistema;
import app.infrastructure.persistence.MySQLConductorRepository;
import app.infrastructure.persistence.MySQLGrafoRepository;
import app.ui.MainFrame;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

public class AdminMenuPanel extends JPanel {

    private static final int PADDING = 16; // Reducido para compactar
    private static final int SPACING = 10; // Reducido para compactar

    // private final ModernActionButton btnVolver; // Eliminado
    private final MainFrame mainFrame;

    // Conductores
    private InputField conductorNombreField;
    private ZoneSelector conductorZoneSelector;
    private PrimaryButton btnAgregarConductor;

    // Zonas: Añadir
    private InputField zonaNombreField;
    private PrimaryButton btnSeleccionarUbicacion;
    private JLabel lblCoordsSeleccionadas;

    // Zonas: Conectar
    private JComboBox<String> cmbZonaOrigen;
    private JComboBox<String> cmbZonaDestino;
    private InputField txtDistancia;
    private PrimaryButton btnConectarZonas;

    private String nombreZonaPendiente = null;

    public AdminMenuPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(241, 245, 249)); // Color de fondo solicitado
        setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));

        add(createHeader(), BorderLayout.NORTH);

        // --- MEJORA DE SCROLLPANE ---
        JScrollPane scrollPane = new JScrollPane(createMainContentPanel());
        scrollPane.setBorder(null); // Sin bordes
        scrollPane.getViewport().setBackground(new Color(241, 245, 249)); // Fondo consistente solicitado
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI()); // UI personalizada
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Scroll más suave
        // El JScrollPane permite que el contenido sea desplazable si la ventana es muy pequeña
        add(scrollPane, BorderLayout.CENTER);

        setupListeners();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, PADDING, 0));

        JLabel title = new JLabel("Panel de Administración");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(30, 41, 59));

        JPanel titlePanel = new JPanel(new GridLayout(1, 1, 0, 4));
        titlePanel.setOpaque(false);
        titlePanel.add(title);

        header.add(titlePanel, BorderLayout.WEST);
        return header;
    }

    /**
     * Crea el panel de contenido principal con todos los formularios alineados verticalmente.
     */
    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(SPACING, 0, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, SPACING * 4, 0); // Aumentamos la separación entre secciones

        // --- Sección 1: Agregar Conductor ---
        gbc.gridy = 0;
        JPanel conductorForm = createConductorForm();
        conductorForm.setBorder(createTitledBorder("Agregar Nuevo Conductor"));
        mainPanel.add(conductorForm, gbc);

        // --- Sección 2: Añadir Zona ---
        gbc.gridy = 1;
        JPanel anadirZonaForm = createAnadirZonaForm();
        anadirZonaForm.setBorder(createTitledBorder("Añadir Nueva Zona"));
        mainPanel.add(anadirZonaForm, gbc);

        // --- Sección 3: Conectar Zonas ---
        gbc.gridy = 2;
        JPanel conectarZonasForm = createConectarZonasForm();
        conectarZonasForm.setBorder(createTitledBorder("Conectar Zonas Existentes"));
        mainPanel.add(conectarZonasForm, gbc);

        // Espaciador para empujar todo hacia arriba
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        mainPanel.add(Box.createGlue(), gbc);

        actualizarComboBoxesConexion();
        actualizarComboBoxZonasConductores();

        return mainPanel;
    }

    private JPanel createConductorForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        // Añadimos un margen interno al panel del formulario
        form.setBorder(BorderFactory.createEmptyBorder(SPACING, SPACING, SPACING, SPACING));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, SPACING, 0);
        gbc.weightx = 1.0;

        conductorNombreField = new InputField("Nombre completo del conductor");
        conductorZoneSelector = new ZoneSelector("Zona de trabajo");
        btnAgregarConductor = new PrimaryButton("Agregar Conductor");

        // Fila 0: Nombre
        gbc.gridy = 0;
        gbc.gridx = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.WEST;
        form.add(createFieldLabel("Nombre del Conductor"), gbc);

        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(conductorNombreField, gbc);

        // Fila 1: Zona
        gbc.gridy = 1; gbc.insets = new Insets(SPACING, 0, SPACING, 0);
        gbc.gridx = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        form.add(createFieldLabel("Zona Asignada"), gbc);

        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(conductorZoneSelector, gbc);

        // Fila 2: Botón
        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.WEST; // Botón a la izquierda
        gbc.insets = new Insets(SPACING * 2, 0, 0, 0);
        form.add(btnAgregarConductor, gbc);

        return form;
    }

    private JPanel createAnadirZonaForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        // Añadimos un margen interno al panel del formulario
        form.setBorder(BorderFactory.createEmptyBorder(SPACING, SPACING, SPACING, SPACING));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, SPACING, 0);
        gbc.weightx = 1.0;

        zonaNombreField = new InputField("Ej: Centro, Norte, Sur");
        btnSeleccionarUbicacion = new PrimaryButton("📍 Seleccionar en Mapa");
        lblCoordsSeleccionadas = new JLabel("Sin ubicación seleccionada");
        lblCoordsSeleccionadas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCoordsSeleccionadas.setForeground(new Color(100, 116, 139));

        // Fila 0: Nombre de la Zona
        gbc.gridy = 0;
        gbc.gridx = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.WEST;
        form.add(createFieldLabel("Nombre de la Zona"), gbc);

        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(zonaNombreField, gbc);

        // Fila 1: Botón y Label de Coordenadas
        gbc.gridy = 1; gbc.insets = new Insets(SPACING, 0, 0, 0);
        gbc.gridx = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE; gbc.gridwidth = 1;
        form.add(btnSeleccionarUbicacion, gbc);

        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(SPACING, 10, 0, 0); // Añadimos un pequeño margen a la izquierda
        form.add(lblCoordsSeleccionadas, gbc);

        return form;
    }

    private JPanel createConectarZonasForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        // Añadimos un margen interno al panel del formulario
        form.setBorder(BorderFactory.createEmptyBorder(SPACING, SPACING, SPACING, SPACING));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, SPACING, 0);
        gbc.weightx = 1.0;

        cmbZonaOrigen = new JComboBox<>();
        cmbZonaDestino = new JComboBox<>();
        txtDistancia = new InputField("Ej: 5.5");
        btnConectarZonas = new PrimaryButton("Crear Conexión");

        styleComboBox(cmbZonaOrigen);
        styleComboBox(cmbZonaDestino);

        // Fila 0: Zona de Origen
        gbc.gridy = 0;
        gbc.gridx = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.WEST;
        form.add(createFieldLabel("Zona de Origen"), gbc);

        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(cmbZonaOrigen, gbc);

        // Fila 1: Zona de Destino
        gbc.gridy = 1; gbc.insets = new Insets(SPACING, 0, SPACING, 0);
        gbc.gridx = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        form.add(createFieldLabel("Zona de Destino"), gbc);

        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(cmbZonaDestino, gbc);

        // Fila 2: Distancia
        gbc.gridy = 2;
        gbc.gridx = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        form.add(createFieldLabel("Distancia (km)"), gbc);

        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(txtDistancia, gbc);

        // Fila 3: Botón
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.WEST; // Botón a la izquierda
        gbc.insets = new Insets(SPACING * 2, 0, 0, 0);
        form.add(btnConectarZonas, gbc);

        return form;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13)); // Corregido: SEMIBOLD no es una constante de Font
        label.setForeground(new Color(51, 65, 85));
        return label;
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setPreferredSize(new Dimension(0, 38));
        // Aplicamos un borde sutil y consistente
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(0, 8, 0, 0)
        ));
    }

    private Border createTitledBorder(String title) {
        // 1. Creamos el borde exterior con el título
        Border titled = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)), title,
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 20), Colors.TEXT_PRIMARY);

        // 2. Creamos un borde vacío que actuará como margen/padding interno
        Border padding = BorderFactory.createEmptyBorder(15, 15, 15, 15);

        // 3. Los combinamos en un solo borde compuesto y lo devolvemos
        return BorderFactory.createCompoundBorder(titled, padding);
    }


    private void setupListeners() {
        setupConductorListeners();
        setupZonaListeners();
    }

    private void setupConductorListeners() {
        btnAgregarConductor.addActionListener(e -> {
            String nombreConductor = conductorNombreField.getText().trim();
            String nombreZona = conductorZoneSelector.getZonaSeleccionada();

            if (nombreConductor.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese el nombre del conductor.", "Campo Requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (nombreZona == null) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione una zona para el conductor.", "Campo Requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Zona zonaAsignada = buscarZonaPorNombreEnGrafo(GestorGrafos.getInstancia().getGrafo(), nombreZona);
            if (zonaAsignada == null) {
                JOptionPane.showMessageDialog(this, "La zona seleccionada no es válida.", "Error Interno", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ConductorRepository repo = new MySQLConductorRepository();
            boolean exito = repo.addConductor(nombreConductor, zonaAsignada.getId());

            if (exito) {
                JOptionPane.showMessageDialog(this,
                        String.format("Conductor '%s' agregado exitosamente a la zona '%s'.", nombreConductor, nombreZona),
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                conductorNombreField.setText("");
                conductorZoneSelector.setSelectedIndex(-1); // Limpiar selección
            } else {
                JOptionPane.showMessageDialog(this, "Error al agregar el conductor. Verifique la consola para más detalles.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void setupZonaListeners() {
        btnSeleccionarUbicacion.addActionListener(e -> {
            nombreZonaPendiente = zonaNombreField.getText().trim();
            if (nombreZonaPendiente.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Por favor, ingrese un nombre para la zona primero.",
                    "Campo Requerido",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            mainFrame.activarModoColocarZona(this);
        });

        btnConectarZonas.addActionListener(e -> {
            String nombreOrigen = (String) cmbZonaOrigen.getSelectedItem();
            String nombreDestino = (String) cmbZonaDestino.getSelectedItem();
            String distStr = txtDistancia.getText().trim();

            if (nombreOrigen == null || nombreDestino == null || nombreOrigen.equals(nombreDestino)) {
                JOptionPane.showMessageDialog(this,
                    "Debe seleccionar dos zonas diferentes.",
                    "Selección Inválida",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (distStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Ingrese la distancia entre las zonas.",
                    "Campo Requerido",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            double distancia;
            try {
                distancia = Double.parseDouble(distStr);
                if (distancia <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "La distancia debe ser un número positivo válido.",
                    "Error de Formato",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            GrafoZonas grafo = GestorGrafos.getInstancia().getGrafo();
            Zona zonaA = buscarZonaPorNombreEnGrafo(grafo, nombreOrigen);
            Zona zonaB = buscarZonaPorNombreEnGrafo(grafo, nombreDestino);

            if (zonaA == null || zonaB == null) {
                JOptionPane.showMessageDialog(this,
                    "No se encontraron las zonas seleccionadas.",
                    "Error Interno",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            GrafoRepository repo = new MySQLGrafoRepository();
            boolean exito = repo.addConexion(zonaA.getId(), zonaB.getId(), distancia);

            if (exito) {
                JOptionPane.showMessageDialog(this,
                    String.format("Conexión creada exitosamente entre '%s' y '%s'.", nombreOrigen, nombreDestino),
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                txtDistancia.setText("");

                GestorGrafos gestor = GestorGrafos.getInstancia();
                gestor.recargarGrafo();
                mainFrame.getRMapaPanel().actualizarGrafo(gestor.getGrafo());
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al crear la conexión. Es posible que ya exista.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void onUbicacionSeleccionada(double x, double y) {
        if (nombreZonaPendiente == null) return;
        lblCoordsSeleccionadas.setText(String.format("Coordenadas: X=%.0f, Y=%.0f", x, y));

        GrafoRepository repo = new MySQLGrafoRepository();
        boolean exito = repo.addZona(nombreZonaPendiente, x, y);

        if (exito) {
            JOptionPane.showMessageDialog(this,
                String.format("Zona '%s' creada exitosamente.", nombreZonaPendiente),
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            zonaNombreField.setText("");
            nombreZonaPendiente = null;
            lblCoordsSeleccionadas.setText("Sin ubicación seleccionada");

            GestorGrafos gestor = GestorGrafos.getInstancia();
            gestor.recargarGrafo();
            mainFrame.getRMapaPanel().actualizarGrafo(gestor.getGrafo());
            actualizarComboBoxesConexion();
            actualizarComboBoxZonasConductores();
        } else {
            JOptionPane.showMessageDialog(this,
                "Error al crear la zona. Verifique que el nombre no esté duplicado.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            nombreZonaPendiente = null;
        }
    }

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

    private void actualizarComboBoxZonasConductores() {
        ArrayList<String> nombresZonas = new ArrayList<>();
        Collection<Zona> zonas = GestorGrafos.getInstancia().getGrafo().getZonas();
        if (zonas != null) {
            for (Zona z : zonas) {
                nombresZonas.add(z.getNombre());
            }
        }
        conductorZoneSelector.setZonas(nombresZonas);
    }

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
        // btnVolver ya no existe, este método puede ser eliminado o dejado vacío.
    }
}