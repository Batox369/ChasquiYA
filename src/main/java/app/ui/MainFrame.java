package app.ui;

import app.domain.model.*;
import app.domain.model.enums.EstadoConductor;
import app.domain.model.enums.TripPhase;
import app.domain.service.*;
import app.infrastructure.persistence.ConexionBD;
import app.ui.panels.LoadingPanel;
import app.ui.panels.*;
import app.ui.components.modern.ModernMessageDialog;
import app.infrastructure.shared.constants.Colors;
import app.ui.views.SideNavigation;
import app.ui.views.TopBar;
import app.ui.views.TripSidebarPanel;

import java.util.List;

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

    private GestorConductores gestorConductores;
    private AsignadorDeViajes asignadorDeViajes;
    private SideNavigation sideNav;
    private TripSidebarPanel tripSidebar;
    private mapaPanel panelMapa;
    private DashboardPanel dashboardPanel;
    private HistorialPanel historialPanel;
    private PerfilPanel perfilPanel;
    private AdminMenuPanel adminPanel;

    private boolean modoColocarZona = false;
    private AdminMenuPanel panelAdminOrigen;

    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private JPanel welcomePanel;

    public MainFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1122, 704);
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle("Movely");

        Toolkit t = Toolkit.getDefaultToolkit();
        setIconImage(t.getImage(getClass().getResource("/soloLogo.png")));

        LoadingPanel loadingPanel = new LoadingPanel();
        setContentPane(loadingPanel);

        new InitializationTask(loadingPanel).execute();
    }

    /**
     * Tarea de fondo para inicializar los componentes pesados de la aplicación.
     */
    private class InitializationTask extends SwingWorker<Boolean, String> {
        private LoadingPanel loadingPanel;

        public InitializationTask(LoadingPanel loadingPanel) {
            this.loadingPanel = loadingPanel;
        }

        @Override
        protected Boolean doInBackground() throws Exception {
            try {
                publish("Estableciendo conexión...");
                ConexionBD.getInstance().getConnection(); // Inicia la conexión a la BD

                publish("Cargando mapa de zonas...");
                GestorGrafos.getInstancia().getGrafo(); // Carga el grafo

                publish("Preparando conductores...");
                GestorConductores.getInstancia().cargarConductoresDesdeBD(); // Carga los conductores

                publish("Finalizando...");
                Thread.sleep(500); // Pequeña pausa para que se vea el último mensaje

                return true; // Éxito
            } catch (Exception e) {
                publish("Error: " + e.getMessage());
                e.printStackTrace();
                return false; // Fracaso
            }
        }

        @Override
        protected void process(List<String> chunks) {
            // Actualiza la UI con los mensajes de progreso
            String lastMessage = chunks.get(chunks.size() - 1);
            loadingPanel.setStatus(lastMessage);
        }

        @Override
        protected void done() {
            try {
                if (get()) { // Si doInBackground() devolvió true (éxito)
                    // 3. Configura la UI principal
                    initializeMainUI();
                    // 4. Decide si mostrar el login o el dashboard
                    checkSessionAndNavigate();
                } else {
                    // Si hubo un error, muestra un diálogo y cierra la app
                    JOptionPane.showMessageDialog(MainFrame.this, "No se pudo iniciar la aplicación. Verifique la conexión a la base de datos.", "Error Crítico", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    private void initializeLayout() {
        // ... (Tu código de initializeLayout() sin cambios) ...
        mainFrame = new JPanel(new BorderLayout());
        mainFrame.setBackground(Colors.SECONDARY);
        topBar = new TopBar();
        mainFrame.add(topBar, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Colors.SECONDARY);
        leftPanel = new JPanel(new BorderLayout());
        // El tamaño se establecerá dinámicamente al cambiar de vista
        centerPanel.add(leftPanel, BorderLayout.WEST);
        selectedPanel = new JPanel(new BorderLayout());
        selectedPanel.setBackground(Colors.SECONDARY);
        selectedPanel.setBorder(null); // Eliminamos cualquier borde del panel de contenido
        centerPanel.add(selectedPanel, BorderLayout.CENTER);
        mainFrame.add(centerPanel, BorderLayout.CENTER);
        setContentPane(mainFrame);
        adminPanel = new AdminMenuPanel(this);
    }

    private void initializeMainUI() {
        initializeLayout(); // Configura el layout principal (paneles, topbar, etc.)
        setContentPane(mainFrame); // Reemplaza el panel de carga por el panel principal
        revalidate();
        repaint();
    }

    private void checkSessionAndNavigate() {
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
    }

    private void showGuestView(JPanel guestPanel) {
        leftPanel.removeAll();
        selectedPanel.removeAll();

        // --- ¡SOLUCIÓN! Ajustamos el ancho para el panel de login/registro ---
        leftPanel.setPreferredSize(new Dimension(350, 0));

        leftPanel.add(guestPanel, BorderLayout.CENTER);

        if (welcomePanel == null) {
            welcomePanel = new JPanel(new GridBagLayout());
            welcomePanel.setBackground(Colors.CARD_BG);
            JLabel welcomeText = new JLabel("Bienvenido a Movely. Por favor, inicie sesión o regístrese.");
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

    /**
     * Maneja el cierre de sesión, limpiando la sesión y mostrando la vista de invitado.
     */
    public void doLogout() {
        // 1. Borra la sesión guardada
        SessionManager.clearSession();
        // 2. Navega de vuelta al panel de login
        navigateToLoginPanel();
    }

    private void showDashboardView(Usuario user) {
        // --- ¡SOLUCIÓN! Restauramos el ancho para la barra de navegación principal ---
        leftPanel.setPreferredSize(new Dimension(280, 0));

        initializePanels();
        setupListeners();
        topBar.setUserName(user.getUsername());

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
        new ModernMessageDialog(this, "Modo Colocar Zona", "Haz clic en el mapa para seleccionar la ubicación de la nueva zona.", ModernMessageDialog.MessageType.INFO).showDialog();
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
        mostrarMenuYPanel(adminPanel);
    }

    private void initializePanels() {
        sideNav = new SideNavigation();
        tripSidebar = new TripSidebarPanel();

        GrafoZonas grafo = GestorGrafos.getInstancia().getGrafo();

        GestorRutas rutas = new GestorRutas();

        this.gestorConductores = GestorConductores.getInstancia();
        this.asignadorDeViajes = new AsignadorDeViajes();

        panelMapa = new mapaPanel(this, tripSidebar, grafo, rutas, this.gestorConductores);
        
        dashboardPanel = new DashboardPanel();
        historialPanel = new HistorialPanel();
        perfilPanel = new PerfilPanel(this);
        adminPanel = new AdminMenuPanel(this);
    }

    private void setupListeners() {
        sideNav.addSolicitarViajeListener(e -> {
            mostrarMapa();
            sideNav.setSelectedButton("solicitar");
        });
        sideNav.addHistorialListener(e -> {
            Usuario u = SessionManager.getCurrentUser();
            cargarHistorialAsincrono(u);
            mostrarMenuYPanel(historialPanel);
            sideNav.setSelectedButton("historial");
        });
        sideNav.addPerfilListener(e -> {
            perfilPanel.actualizarEstadisticas(); // <-- ¡AQUÍ! Actualizamos los datos antes de mostrar
            mostrarMenuYPanel(perfilPanel);
            sideNav.setSelectedButton("perfil");
        });
        sideNav.addAdminListener(e -> {
            mostrarAdminMenu();
            sideNav.setSelectedButton("admin");
        });
        tripSidebar.addCancelarListener(e -> {
            // SIMPLIFICADO: Ahora solo necesitamos llamar a resetearMapa.
            // Este método se encargará de notificar al MainFrame para que muestre la SideNav.
            Viaje viajeCancelado = panelMapa.getViajeActual();
            if (viajeCancelado != null && viajeCancelado.getConductorId() != null) {
                Conductor conductor = gestorConductores.getConductorPorId(viajeCancelado.getConductorId());
                if (conductor != null) {
                    System.out.println("[LOGIC] Viaje cancelado por el usuario. Conductor " + conductor.getNombreCompleto() + " vuelve a estado DISPONIBLE.");
                    conductor.setEstado(EstadoConductor.DISPONIBLE);
                    conductor.setTripPhase(TripPhase.NONE);
                    conductor.setRutaAsignada(null);
                }
            }
            panelMapa.resetearMapa();
        });
        tripSidebar.addSolicitarListener(e -> {
            // 1. Llama a la lógica de negocio (sin pop-up)
            app.domain.model.Viaje viajeActual = panelMapa.getViajeActual();
            if (viajeActual == null) return;

            // 2. Actualiza el sidebar a su nuevo estado "Asignando..."
            tripSidebar.setEstadoAsignandoConductor();

            // 3. Llama al asignador para encontrar el conductor más cercano
            GrafoZonas grafo = GestorGrafos.getInstancia().getGrafo();
            Zona zonaOrigenViaje = viajeActual.getRutaZonas().get(0);

            Conductor conductorAsignado = asignadorDeViajes.asignarConductorMasCercano(
                    zonaOrigenViaje, // Zona de origen del viaje
                    gestorConductores.getConductores(),
                    grafo
            );

            // 4. Actualiza la UI con el resultado
            if (conductorAsignado != null) {
                System.out.println("[DEBUG] Actualizando UI para mostrar al conductor: " + conductorAsignado.getNombreCompleto());
                tripSidebar.mostrarConductorAsignado(conductorAsignado);
                
                // --- ¡CORRECCIÓN! ---
                // Ya no calculamos la ruta aquí. Solo asignamos el viaje.
                // El simulador se encargará de la ruta cuando el conductor esté listo.
                viajeActual.setConductorId(conductorAsignado.getId()); // Guardamos el ID del conductor en el viaje
                panelMapa.setTripActive(); // <-- ¡AQUÍ! Desactivamos los clics en el mapa

            } // (Opcional: podrías añadir un else para mostrar "No se encontraron conductores")
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
        mostrarMenuYPanel(adminPanel);
    }

    public boolean getmodoColocarZona(){
        return modoColocarZona;
    }

    public mapaPanel getRMapaPanel() {
        return panelMapa;
    }

    /**
     * Se llama cuando el simulador detecta que un viaje ha finalizado.
     * Aquí se centraliza la lógica de negocio para guardar el viaje en el historial.
     * @param conductorId El ID del conductor que completó el viaje.
     */
    public void onViajeCompletado(int conductorId) {
        Viaje viajeCompletado = panelMapa.getViajeActual();

        if (viajeCompletado != null && viajeCompletado.getConductorId() != null && viajeCompletado.getConductorId() == conductorId) {
            System.out.println("[LOGIC] Viaje completado. Guardando en el historial...");
            // --- ¡CORRECCIÓN! ---
            // Obtenemos el usuario desde el SessionManager para asegurar la consistencia.
            viajeCompletado.setUsuarioId(SessionManager.getCurrentUser().getId());
            GestorHistorial.getInstancia().guardarViaje(viajeCompletado);
            historialPanel.refrescarHistorial();

        } else {
            System.err.println("[ERROR] Se intentó completar un viaje, pero no se encontró el viaje activo correspondiente.");
        }

        // Finalmente, reseteamos el mapa para dejarlo listo para el siguiente viaje.
        panelMapa.resetearMapa();
    }
    public void cargarHistorialAsincrono(Usuario usuario) {

        historialPanel.mostrarLoading();  // mensaje temporal
        SwingWorker<Void, Void> worker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() throws Exception {
                // Carga pesada en un hilo diferente al EDT
                GestorHistorial.getInstancia().cargarHistorial(usuario);
                return null;
            }
            @Override
            protected void done() {
                // Cuando termina, vuelve al hilo gráfico
                historialPanel.ocultarLoading();
                historialPanel.refrescarHistorial();
            }
        };

        worker.execute();
    }

}