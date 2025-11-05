package app.ui;

import app.domain.model.GrafoZonas;
import app.domain.repository.ZonaRepository;
import app.domain.service.GestorGrafos;
import app.domain.service.GestorRutas;
import app.infrastructure.persistence.ConexionBD;
import app.ui.panels.*;
import app.infrastructure.shared.constants.Colors;
import app.domain.service.Sistema;
import app.ui.views.SideNavigation;
import app.ui.views.TopBar;
import app.ui.views.TripSidebarPanel;

import app.domain.model.Usuario;
import app.domain.repository.UsuarioRepository;
import app.infrastructure.persistence.MySQLUsuarioRepository;
import app.infrastructure.shared.SessionManager;
import app.ui.panels.LoginPanel;
import app.ui.panels.RegisterPanel;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel mainFrame;
    private JPanel leftPanel;
    private JPanel selectedPanel;
    private TopBar topBar;

    private SideNavigation sideNav;
    private TripSidebarPanel tripSidebar;
    private mapaPanel panelMapa;
    private Sistema sistema;
    private DashboardPanel dashboardPanel;
    private HistorialPanel historialPanel;
    private ConfiguracionPanel configuracionPanel;
    private PerfilPanel perfilPanel;
    private AdminMenuPanel adminPanel;

    private boolean modoColocarZona = false; // <-- NUEVA VARIABLE DE ESTADO
    private AdminMenuPanel panelAdminOrigen;

    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private JPanel welcomePanel;

    ZonaRepository repo = new ZonaRepository(ConexionBD.getInstance().getConnection());

    public MainFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1122, 704);
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle("Sistema de Viajes");

        sistema = Sistema.getInstancia();
        initializeLayout();

        String savedUsername = SessionManager.getSavedUsername();
        if (savedUsername != null) {
            UsuarioRepository userRepo = new MySQLUsuarioRepository();
            Usuario user = userRepo.findByUsername(savedUsername);

            if (user != null) {
                SessionManager.setCurrentUser(user);
                showDashboardView(user);
            } else {
                SessionManager.clearSession();
                navigateToLoginPanel();
            }
        } else {
            navigateToLoginPanel();
        }

        setVisible(true);
    }

    private void initializeLayout() {
        // ... (Tu código de initializeLayout() sin cambios) ...
        mainFrame = new JPanel(new BorderLayout());
        mainFrame.setBackground(Colors.SECONDARY);
        topBar = new TopBar("ChasquiYa", "Invitado");
        mainFrame.add(topBar, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Colors.SECONDARY);
        leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(250, 0));
        centerPanel.add(leftPanel, BorderLayout.WEST);
        selectedPanel = new JPanel(new BorderLayout());
        selectedPanel.setBackground(Colors.SECONDARY);
        selectedPanel.setBorder(null); // Eliminamos cualquier borde del panel de contenido
        centerPanel.add(selectedPanel, BorderLayout.CENTER);
        mainFrame.add(centerPanel, BorderLayout.CENTER);
        setContentPane(mainFrame);
        adminPanel = new AdminMenuPanel(sistema, this);
    }

    private void showGuestView(JPanel guestPanel) {
        leftPanel.removeAll();
        selectedPanel.removeAll();

        leftPanel.add(guestPanel, BorderLayout.CENTER);

        if (welcomePanel == null) {
            welcomePanel = new JPanel(new GridBagLayout());
            welcomePanel.setBackground(Colors.CARD_BG);
            JLabel welcomeText = new JLabel("Bienvenido a ChasquiYa. Por favor, inicie sesión o regístrese.");
            welcomeText.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            welcomeText.setForeground(Colors.TEXT_SECONDARY.darker());
            welcomePanel.add(welcomeText);
        }
        selectedPanel.add(welcomePanel, BorderLayout.CENTER);

        topBar.setUserName("Invitado");

        revalidate();
        repaint();
    }

    public void navigateToLoginPanel() {
        if (loginPanel == null) {
            loginPanel = new LoginPanel(this);
        }
        showGuestView(loginPanel);
    }

    public void navigateToRegisterPanel() {
        if (registerPanel == null) {
            registerPanel = new RegisterPanel(this);
        }
        showGuestView(registerPanel);
    }

    private void showDashboardView(Usuario user) {
        sistema = Sistema.getInstancia();
        initializePanels();
        setupListeners();
        topBar.setUserName(user.getUsername());
        topBar.addAdminButtonListener(e -> mostrarAdminMenu());
        adminPanel.addVolverListener(e -> {
            setContentPane(mainFrame);
            revalidate();
            repaint();
        });
        leftPanel.add(sideNav, BorderLayout.CENTER);
        mostrarMapa();
    }

    public void onLoginSuccess(Usuario user) {
        // ... (Tu código de onLoginSuccess() sin cambios) ...
        leftPanel.removeAll();
        selectedPanel.removeAll();
        showDashboardView(user);
        revalidate();
        repaint();
    }

    public void activarModoColocarZona(AdminMenuPanel panelOrigen) {
        this.modoColocarZona = true;
        this.panelAdminOrigen = panelOrigen;
        mostrarMapa(); // Muestra el mapa
        JOptionPane.showMessageDialog(this, "Haz clic en el mapa para seleccionar la ubicación de la nueva zona.", "Modo Colocar Zona", JOptionPane.INFORMATION_MESSAGE);
        // Cambiar cursor o indicar visualmente el modo
        panelMapa.getRootPanel().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    public void onZonaColocada(double x, double y) {
        if (!modoColocarZona || panelAdminOrigen == null) return;

        // Llama de vuelta al AdminPanel para que guarde
        panelAdminOrigen.onUbicacionSeleccionada(x, y);

        // Salir del modo
        this.modoColocarZona = false;
        this.panelAdminOrigen = null;
        panelMapa.getRootPanel().setCursor(Cursor.getDefaultCursor());

        // Volver al panel de Admin
        mostrarAdminMenu();
    }

    private void initializePanels() {
        sideNav = new SideNavigation();
        tripSidebar = new TripSidebarPanel();

        // --- ¡AQUÍ ESTÁ LA FORMA CORRECTA! ---
        // 1. Obtén el grafo desde el Singleton GestorGrafos
        GrafoZonas grafo = GestorGrafos.getInstancia().getGrafo();

        // 2. Crea una instancia del GestorRutas
        GestorRutas rutas = new GestorRutas();

        // 3. Pasa las instancias correctas al constructor de mapaPanel
        panelMapa = new mapaPanel(this, tripSidebar, grafo, rutas);
        // ------

        dashboardPanel = new DashboardPanel();
        historialPanel = new HistorialPanel();
        configuracionPanel = new ConfiguracionPanel();
        perfilPanel = new PerfilPanel(this);
        adminPanel = new AdminMenuPanel(sistema, this);
    }

    private void setupListeners() {
        sideNav.addSolicitarViajeListener(e -> {
            mostrarMapa();
            sideNav.setSelectedButton("solicitar");
        });
        sideNav.addHistorialListener(e -> {
            mostrarMenuYPanel(historialPanel);
            sideNav.setSelectedButton("historial");
        });
        sideNav.addConfiguracionListener(e -> {
            mostrarMenuYPanel(configuracionPanel);
            sideNav.setSelectedButton("configuracion");
        });
        sideNav.addPerfilListener(e -> {
            mostrarMenuYPanel(perfilPanel);
            sideNav.setSelectedButton("perfil");
        });
        sideNav.addAdminListener(e -> {
            mostrarMenuYPanel(adminPanel);
            sideNav.setSelectedButton("admin");
        });
        tripSidebar.addCancelarListener(e -> {
            panelMapa.resetearMapa();
            mostrarMapa();
            sideNav.setSelectedButton("solicitar");
        });
        tripSidebar.addSolicitarListener(e -> {
            panelMapa.confirmarViaje();
            mostrarMapa();
            sideNav.setSelectedButton("solicitar");
        });
    }

    private void mostrarMenuYPanel(JPanel panel) {
        leftPanel.removeAll();
        leftPanel.add(sideNav, BorderLayout.CENTER);
        selectedPanel.removeAll();
        selectedPanel.add(panel, BorderLayout.CENTER);
        leftPanel.revalidate();
        leftPanel.repaint();
        selectedPanel.revalidate();
        selectedPanel.repaint();
    }

    public void mostrarDashboard() {
        mostrarMenuYPanel(dashboardPanel);
        sideNav.setSelectedButton("solicitar");
    }

    public void mostrarMenuYPerfil() {
        mostrarMenuYPanel(perfilPanel);
        sideNav.setSelectedButton("perfil");
    }

    public void mostrarMapa() {
        leftPanel.removeAll();
        leftPanel.add(sideNav, BorderLayout.CENTER);
        selectedPanel.removeAll();
        selectedPanel.add(panelMapa.getRootPanel(), BorderLayout.CENTER);
        leftPanel.revalidate();
        leftPanel.repaint();
        selectedPanel.revalidate();
        selectedPanel.repaint();
    }

    public void mostrarSidebarDeViaje() {
        leftPanel.removeAll();
        leftPanel.add(tripSidebar, BorderLayout.CENTER);
        leftPanel.revalidate();
        leftPanel.repaint();
    }

    public void mostrarAdminMenu() {
        setContentPane(adminPanel);
        revalidate();
        repaint();
    }

    public boolean getmodoColocarZona(){
        return modoColocarZona;
    }

    public mapaPanel getRMapaPanel() {
        return panelMapa;
    }


    public JPanel getMapaPanel() {
        if (selectedPanel == null) {
            System.err.println("Advertencia: Se llamó a getMapaPanel() antes de inicializar panelMapa.");
        }
        return selectedPanel;
    }
}