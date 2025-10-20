package Core;

import Components.*;
import Panels.*;
import Constants.Colors;
import Models.Sistema;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel mainFrame;
    private JPanel leftPanel; // Panel izquierdo que cambia entre menú y sidebar
    private JPanel selectedPanel;
    private SideNavigation sideNav;
    private TripSidebarPanel tripSidebar;
    private mapaPanel panelMapa;
    private Sistema sistema;

    private DashboardPanel dashboardPanel;
    private HistorialPanel historialPanel;
    private ConfiguracionPanel configuracionPanel;
    private PerfilPanel perfilPanel;
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

        mostrarMenuYDashboard();
        setVisible(true);
    }

    private void initializePanels() {
        // Crear navegación lateral
        sideNav = new SideNavigation();

        // Crear sidebar de viaje
        tripSidebar = new TripSidebarPanel();

        // Crear mapa y pasarle referencia a MainFrame
        panelMapa = new mapaPanel(this, tripSidebar);

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
            setContentPane(mainFrame);
            revalidate();
            repaint();
        });

        TopBar topBar = new TopBar("ChasquiYa", "Usuario");
        topBar.addAdminButtonListener(e -> mostrarAdminMenu());

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Colors.SECONDARY);

        // Panel izquierdo dinámico (empieza con el menú)
        leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(sideNav, BorderLayout.CENTER);
        centerPanel.add(leftPanel, BorderLayout.WEST);

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

        // Listener del sidebar de viaje
        tripSidebar.addCancelarListener(e -> {
            panelMapa.resetearMapa();
            mostrarMenuYDashboard();
        });

        tripSidebar.addSolicitarListener(e -> {
            panelMapa.confirmarViaje();
            mostrarMenuYDashboard();
        });
    }

    private void mostrarMenuYPanel(JPanel panel) {
        // Mostrar menú en la izquierda
        leftPanel.removeAll();
        leftPanel.add(sideNav, BorderLayout.CENTER);

        // Mostrar panel en el centro
        selectedPanel.removeAll();
        selectedPanel.add(panel, BorderLayout.CENTER);

        leftPanel.revalidate();
        leftPanel.repaint();
        selectedPanel.revalidate();
        selectedPanel.repaint();
    }

    public void mostrarMenuYDashboard() {
        mostrarMenuYPanel(dashboardPanel);
        sideNav.setSelectedButton("solicitar"); // Reset selección
    }

    public void mostrarMapa() {
        // Mostrar menú en la izquierda
        leftPanel.removeAll();
        leftPanel.add(sideNav, BorderLayout.CENTER);

        // Mostrar mapa en el centro
        selectedPanel.removeAll();
        selectedPanel.add(panelMapa.getRootPanel(), BorderLayout.CENTER);

        leftPanel.revalidate();
        leftPanel.repaint();
        selectedPanel.revalidate();
        selectedPanel.repaint();
    }

    public void mostrarSidebarDeViaje() {
        // Reemplazar menú por sidebar de viaje
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