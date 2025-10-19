package Core;

import Components.*;
import Panels.*;
import Constants.Colors;
import Models.Sistema;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel mainFrame;
    private JPanel selectedPanel;
    private SideNavigation sideNav;
    private mapaPanel panelMapa;
    private TripInfoPanel tripPanel;
    private Sistema sistema;

    // Paneles precargados
    private DashboardPanel dashboardPanel;
    private HistorialPanel historialPanel;
    private ConfiguracionPanel configuracionPanel;
    private PerfilPanel perfilPanel;

    // Admin panel & stuff
    private AdminMenuPanel adminPanel;


    public MainFrame() {
        sistema = Sistema.getInstancia();

        initializePanels();
        initComponents();
        setupListeners();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1020, 640);
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle("Sistema de Viajes");

        mostrarPanel(dashboardPanel);
        setVisible(true);
    }

    private void initializePanels() {
        panelMapa = new mapaPanel();
        dashboardPanel = new DashboardPanel();
        historialPanel = new HistorialPanel();
        configuracionPanel = new ConfiguracionPanel();
        perfilPanel = new PerfilPanel();
        adminPanel = new AdminMenuPanel(sistema);
    }

    private void initComponents() {
        mainFrame = new JPanel(new BorderLayout());
        mainFrame.setBackground(Colors.SECONDARY);

        adminPanel.addVolverListener(e -> {
            setContentPane(mainFrame);  // restaurar layout original
            revalidate();
            repaint();
        });

        // Top Bar
        TopBar topBar = new TopBar("TravelApp", "Uaaaasuario");
        topBar.addAdminButtonListener(e -> mostrarAdminMenu());
        // Panel central
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Colors.SECONDARY);

        // Navegación lateral
        sideNav = new SideNavigation();
        centerPanel.add(sideNav, BorderLayout.WEST);

        // Panel de contenido
        selectedPanel = new JPanel(new BorderLayout());
        selectedPanel.setBackground(Colors.SECONDARY);
        centerPanel.add(selectedPanel, BorderLayout.CENTER);

        mainFrame.add(topBar, BorderLayout.NORTH);
        mainFrame.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainFrame);
    }

    private void setupListeners() {
        sideNav.addSolicitarViajeListener(e -> {
            mostrarMapa();
            sideNav.setSelectedButton("solicitar");
        });

        sideNav.addHistorialListener(e -> {
            mostrarPanel(historialPanel);
            sideNav.setSelectedButton("historial");
        });

        sideNav.addConfiguracionListener(e -> {
            mostrarPanel(configuracionPanel);
            sideNav.setSelectedButton("configuracion");
        });

        sideNav.addPerfilListener(e -> {
            mostrarPanel(perfilPanel);
            sideNav.setSelectedButton("perfil");
        });
    }

    private void mostrarPanel(JPanel panel) {
        selectedPanel.removeAll();
        selectedPanel.add(panel, BorderLayout.CENTER);
        selectedPanel.revalidate();
        selectedPanel.repaint();
    }

    public void mostrarMapa() {
        selectedPanel.removeAll();
        selectedPanel.add(panelMapa.getRootPanel(), BorderLayout.CENTER);
        selectedPanel.revalidate();
        selectedPanel.repaint();
    }

    public void mostrarAdminMenu() {
        setContentPane(adminPanel);
        revalidate();
        repaint();
    }
}