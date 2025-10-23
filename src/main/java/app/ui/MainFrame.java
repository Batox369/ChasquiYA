package app.ui;

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

    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private JPanel welcomePanel;

    public MainFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1020, 640);
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle("Sistema de Viajes");

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
        centerPanel.add(selectedPanel, BorderLayout.CENTER);
        mainFrame.add(centerPanel, BorderLayout.CENTER);
        setContentPane(mainFrame);
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

    private void initializePanels() {
        sideNav = new SideNavigation();
        tripSidebar = new TripSidebarPanel();
        panelMapa = new mapaPanel(this, tripSidebar);
        dashboardPanel = new DashboardPanel();
        historialPanel = new HistorialPanel();
        configuracionPanel = new ConfiguracionPanel();
        perfilPanel = new PerfilPanel(this);
        adminPanel = new AdminMenuPanel(sistema);
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
        tripSidebar.addCancelarListener(e -> {
            panelMapa.resetearMapa();
            mostrarDashboard();
            sideNav.setSelectedButton("solicitar");
        });
        tripSidebar.addSolicitarListener(e -> {
            panelMapa.confirmarViaje();
            mostrarDashboard();
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
}