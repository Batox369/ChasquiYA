package app.ui.panels;

import app.infrastructure.shared.constants.Colors;
import app.ui.components.SmoothScrollPane;
import app.ui.components.ModernTextField;
import app.ui.components.ModernComboBox;
import app.ui.components.ModernMessageDialog;
import app.ui.components.ModernScrollBarUI;
import app.ui.components.PrimaryButton;
import app.domain.model.GrafoZonas;
import app.domain.model.Zona;
import app.domain.repository.ConductorRepository;
import app.domain.repository.GrafoRepository;
import app.domain.service.GestorGrafos;
import app.domain.service.GestorConductores;
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
    private ModernTextField conductorNombreField;
    private ModernComboBox<String> conductorZoneSelector;
    private PrimaryButton btnAgregarConductor;

    // Zonas: Añadir
    private ModernTextField zonaNombreField;
    private PrimaryButton btnSeleccionarUbicacion;
    private JLabel lblCoordsSeleccionadas;

    // Zonas: Conectar
    private ModernComboBox<String> cmbZonaOrigen;
    private ModernComboBox<String> cmbZonaDestino;
    private ModernTextField txtDistancia;
    private PrimaryButton btnConectarZonas;

    private String nombreZonaPendiente = null;

    public AdminMenuPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(241, 245, 249)); // Color de fondo solicitado
        setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));

        add(createHeader(), BorderLayout.NORTH);

        // --- MEJORA DE SCROLLPANE ---
        JScrollPane scrollPane = new SmoothScrollPane(createMainContentPanel());
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
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
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

        conductorNombreField = new ModernTextField(20);
        conductorZoneSelector = new ModernComboBox<>();
        btnAgregarConductor = new PrimaryButton("Agregar Conductor");

        // Fila 0: Nombre
        gbc.gridy = 0;
        gbc.gridx = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.WEST; gbc.insets = new Insets(0, 15, SPACING, 0);
        form.add(createFieldLabel("Nombre del Conductor"), gbc);

        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(conductorNombreField, gbc);

        // Fila 1: Zona
        gbc.gridy = 1; gbc.insets = new Insets(SPACING, 15, SPACING, 0);
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

        zonaNombreField = new ModernTextField(20);
        zonaNombreField.setPlaceholder("Ej: Centro, Norte, Sur");
        btnSeleccionarUbicacion = new PrimaryButton("Seleccionar en Mapa");
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

        cmbZonaOrigen = new ModernComboBox<>();
        cmbZonaDestino = new ModernComboBox<>();
        txtDistancia = new ModernTextField(10);
        txtDistancia.setPlaceholder("Ej: 5.5");
        btnConectarZonas = new PrimaryButton("Crear Conexión");

        // Fila 0: Zona de Origen
        gbc.gridy = 0;
        gbc.gridx = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(SPACING, 10, SPACING, 0);
        form.add(createFieldLabel("Zona de Origen"), gbc);

        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(cmbZonaOrigen, gbc);

        // Fila 1: Zona de Destino
        gbc.gridy = 1; gbc.insets = new Insets(SPACING, 10, SPACING, 0);
        gbc.gridx = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        form.add(createFieldLabel("Zona de Destino"), gbc);

        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(cmbZonaDestino, gbc);

        // Fila 2: Distancia
        gbc.gridy = 2;
        gbc.gridx = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(SPACING, 10, SPACING, 0);
        form.add(createFieldLabel("Distancia (metros)"), gbc);

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
            String nombreZona = (String) conductorZoneSelector.getSelectedItem();

            if (nombreConductor.isEmpty()) {
                new ModernMessageDialog(mainFrame, "Campo Requerido", "Por favor, ingrese el nombre del conductor.", ModernMessageDialog.MessageType.WARNING).showDialog();
                return;
            }

            if (nombreZona == null) {
                new ModernMessageDialog(mainFrame, "Campo Requerido", "Por favor, seleccione una zona para el conductor.", ModernMessageDialog.MessageType.WARNING).showDialog();
                return;
            }

            Zona zonaAsignada = buscarZonaPorNombreEnGrafo(GestorGrafos.getInstancia().getGrafo(), nombreZona);
            if (zonaAsignada == null) {
                new ModernMessageDialog(mainFrame, "Error Interno", "La zona seleccionada no es válida.", ModernMessageDialog.MessageType.ERROR).showDialog();
                return;
            }

            ConductorRepository repo = new MySQLConductorRepository();
            boolean exito = repo.addConductor(nombreConductor, zonaAsignada.getId());

            if (exito) {
                new ModernMessageDialog(mainFrame, "Éxito", String.format("Conductor '%s' agregado exitosamente a la zona '%s'.", nombreConductor, nombreZona), ModernMessageDialog.MessageType.SUCCESS).showDialog();
                conductorNombreField.setText("");
                conductorZoneSelector.setSelectedIndex(-1); // Limpiar selección

                // --- ¡NUEVO! Recargar y actualizar el mapa con el nuevo conductor ---
                // 1. Recargamos la lista de conductores desde la BD
                GestorConductores gestorConductores = GestorConductores.getInstancia();
                gestorConductores.cargarConductoresDesdeBD();

                // 2. Notificamos al mapa para que se repinte con la lista actualizada
                mainFrame.getRMapaPanel().actualizarConductores(gestorConductores.getConductores());
            } else {
                new ModernMessageDialog(mainFrame, "Error", "Error al agregar el conductor. Verifique la consola para más detalles.", ModernMessageDialog.MessageType.ERROR).showDialog();
            }
        });
    }

    private void setupZonaListeners() {
        btnSeleccionarUbicacion.addActionListener(e -> {
            nombreZonaPendiente = zonaNombreField.getText().trim();
            if (nombreZonaPendiente.isEmpty()) {
                new ModernMessageDialog(mainFrame, "Campo Requerido", "Por favor, ingrese un nombre para la zona primero.", ModernMessageDialog.MessageType.WARNING).showDialog();
                return;
            }
            mainFrame.activarModoColocarZona(this);
        });

        btnConectarZonas.addActionListener(e -> {
            String nombreOrigen = (String) cmbZonaOrigen.getSelectedItem();
            String nombreDestino = (String) cmbZonaDestino.getSelectedItem();
            String distStr = txtDistancia.getText().trim();

            if (nombreOrigen == null || nombreDestino == null || nombreOrigen.equals(nombreDestino)) {
                new ModernMessageDialog(mainFrame, "Selección Inválida", "Debe seleccionar dos zonas diferentes.", ModernMessageDialog.MessageType.ERROR).showDialog();
                return;
            }
            if (distStr.isEmpty()) {
                new ModernMessageDialog(mainFrame, "Campo Requerido", "Ingrese la distancia entre las zonas.", ModernMessageDialog.MessageType.WARNING).showDialog();
                return;
            }

            double distancia;
            try {
                distancia = Double.parseDouble(distStr);
                if (distancia <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                new ModernMessageDialog(mainFrame, "Error de Formato", "La distancia debe ser un número positivo válido.", ModernMessageDialog.MessageType.ERROR).showDialog();
                return;
            }

            GrafoZonas grafo = GestorGrafos.getInstancia().getGrafo();
            Zona zonaA = buscarZonaPorNombreEnGrafo(grafo, nombreOrigen);
            Zona zonaB = buscarZonaPorNombreEnGrafo(grafo, nombreDestino);

            if (zonaA == null || zonaB == null) {
                new ModernMessageDialog(mainFrame, "Error Interno", "No se encontraron las zonas seleccionadas.", ModernMessageDialog.MessageType.ERROR).showDialog();
                return;
            }

            GrafoRepository repo = new MySQLGrafoRepository();
            boolean exito = repo.addConexion(zonaA.getId(), zonaB.getId(), distancia);

            if (exito) {
                new ModernMessageDialog(mainFrame, "Éxito", String.format("Conexión creada exitosamente entre '%s' y '%s'.", nombreOrigen, nombreDestino), ModernMessageDialog.MessageType.SUCCESS).showDialog();
                txtDistancia.setText("");

                GestorGrafos gestor = GestorGrafos.getInstancia();
                gestor.recargarGrafo();
                mainFrame.getRMapaPanel().actualizarGrafo(gestor.getGrafo());
            } else {
                new ModernMessageDialog(mainFrame, "Error", "Error al crear la conexión. Es posible que ya exista.", ModernMessageDialog.MessageType.ERROR).showDialog();
            }
        });
    }

    public void onUbicacionSeleccionada(double x, double y) {
        if (nombreZonaPendiente == null) return;
        lblCoordsSeleccionadas.setText(String.format("Coordenadas: X=%.0f, Y=%.0f", x, y));

        GrafoRepository repo = new MySQLGrafoRepository();
        boolean exito = repo.addZona(nombreZonaPendiente, x, y);

        if (exito) {
            new ModernMessageDialog(mainFrame, "Éxito", String.format("Zona '%s' creada exitosamente.", nombreZonaPendiente), ModernMessageDialog.MessageType.SUCCESS).showDialog();
            zonaNombreField.setText("");
            nombreZonaPendiente = null;
            lblCoordsSeleccionadas.setText("Sin ubicación seleccionada");

            GestorGrafos gestor = GestorGrafos.getInstancia();
            gestor.recargarGrafo();
            mainFrame.getRMapaPanel().actualizarGrafo(gestor.getGrafo());
            actualizarComboBoxesConexion();
            actualizarComboBoxZonasConductores();
        } else {
            new ModernMessageDialog(mainFrame, "Error", "Error al crear la zona. Verifique que el nombre no esté duplicado.", ModernMessageDialog.MessageType.ERROR).showDialog();
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
        conductorZoneSelector.removeAllItems();
        Collection<Zona> zonas = GestorGrafos.getInstancia().getGrafo().getZonas();
        if (zonas != null) {
            for (Zona z : zonas) {
                conductorZoneSelector.addItem(z.getNombre());
            }
        }
        conductorZoneSelector.setSelectedIndex(-1); // Dejar sin selección por defecto
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