package app.ui.panels;

import app.domain.model.Coordenada;
import app.domain.model.Viaje;
import app.domain.service.GestorConductores;
import app.domain.service.GestorRutas;
import app.ui.components.MapMarker;
import app.ui.components.RouteRenderer;
import app.ui.components.ZonaRenderer; // Importa el renderer
import app.domain.model.*;
import app.infrastructure.shared.constants.Colors;
import app.ui.components.ConductorRenderer;
import app.ui.MainFrame;
import app.ui.views.TripSidebarPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;

public class mapaPanel{
    private JPanel rootPanel;
    private JPanel mapaCanvas;

    private BufferedImage imagen;
    private double zoom = 1.0;
    private int offsetX = 0;
    private int offsetY = 0;
    private int lastX, lastY;

    private static final double MIN_ZOOM = 0.8;
    private static final double MAX_ZOOM = 5.0;
    private static final double ZOOM_FACTOR = 1.1;

    private MapMarker markerOrigen;
    private MapMarker markerDestino;
    private Viaje viajeActual;
    private boolean isDragging = false;
    private boolean mapaInicializado = false;

    private MainFrame mainFrame;
    private TripSidebarPanel tripSidebar;

    private GrafoZonas grafoZonas;
    private GestorRutas gestorRutas;
    private Zona zonaOrigen;
    private Zona zonaDestino;
    private java.util.List<Zona> rutaActual;
    private List<Conductor> conductores;

    public mapaPanel(MainFrame mainFrame, TripSidebarPanel tripSidebar, GrafoZonas grafoZonas, GestorRutas gestorRutas, GestorConductores gestorConductores) {
        this.mainFrame = mainFrame;
        this.tripSidebar = tripSidebar;
        this.grafoZonas = grafoZonas; // Grafo cargado desde ZonaRepository
        this.gestorRutas = gestorRutas;
        this.conductores = gestorConductores.getConductores();


        loadImage();
        initComponents();
        setupListeners();

    }
    private void loadImage() {
        try {
            imagen = ImageIO.read(new File("src/main/resources/mapa.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
            imagen = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = imagen.createGraphics();
            g.setColor(Colors.SECONDARY);
            g.fillRect(0, 0, 800, 600);
            g.setColor(Colors.TEXT_SECONDARY);
            g.setFont(new Font("Segoe UI", Font.BOLD, 20));
            g.drawString("Mapa no encontrado", 300, 300);
            g.dispose();
        }
    }

    private void initComponents() {
        rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(Colors.SECONDARY);
        rootPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        mapaCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMap(g);
            }
        };
        mapaCanvas.setBackground(Colors.CARD_BG);
        mapaCanvas.setBorder(null);
        mapaCanvas.setCursor(new Cursor(Cursor.MOVE_CURSOR));

        rootPanel.add(mapaCanvas, BorderLayout.CENTER);
    }

    private void setupListeners() {
        mapaCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
                isDragging = false;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!isDragging && e.getButton() == MouseEvent.BUTTON1) {
                    handleMapClick(e.getPoint());
                }
                mapaCanvas.setCursor(new Cursor(Cursor.MOVE_CURSOR));
            }
        });

        mapaCanvas.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                isDragging = true;
                int dx = e.getX() - lastX;
                int dy = e.getY() - lastY;

                offsetX += dx;
                offsetY += dy;
                constrainOffset();

                lastX = e.getX();
                lastY = e.getY();
                mapaCanvas.repaint();
                mapaCanvas.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });

        mapaCanvas.addMouseWheelListener(e -> {
            Point mousePos = e.getPoint();
            zoomAtPoint(mousePos, e.getPreciseWheelRotation() < 0);
        });

        mapaCanvas.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = mapaCanvas.getWidth();
                int h = mapaCanvas.getHeight();

                if (!mapaInicializado && w > 400 && h > 300) {
                    mapaInicializado = true; // ⚠️ importante: antes del invokeLater

                    SwingUtilities.invokeLater(() -> {
                        centerMap();
                        mapaCanvas.revalidate();
                        mapaCanvas.repaint();

                        System.out.printf(
                                "Mapa inicializado correctamente con tamaño estable: %dx%d zoom=%.3f%n",
                                w, h, zoom
                        );
                        System.out.printf("[ZOOM-TRACE] %s -> %.3f%n",
                                new Throwable().getStackTrace()[1].getMethodName(), zoom);
                    });

                    return;
                }

                // Fase normal tras inicialización
                if (mapaInicializado) {
                    constrainOffset();
                    mapaCanvas.repaint();
                }
            }
        });

    }

    private void handleMapClick(Point clickPoint) {
        // 1. Convertir clic de Pantalla -> a Coordenada de Imagen/Mundo
        double mundoX = (clickPoint.x - offsetX) / zoom;
        double mundoY = (clickPoint.y - offsetY) / zoom;

        // 2. Revisa si estamos en "Modo Colocar Zona"
        // (Corregí el nombre del método a la convención Java: getModoColocarZona)
        if (mainFrame.getmodoColocarZona()) {
            mainFrame.onZonaColocada(mundoX, mundoY); // Llama al MainFrame con las coords
            return; // Termina aquí, no selecciones origen/destino
        }

        // 3. Buscar la zona más cercana a esa coordenada de IMAGEN
        Zona zonaClic = buscarZonaCercana(mundoX, mundoY);

        if (zonaClic == null) return; // Clic en un lugar vacío

        if (zonaOrigen == null) {
            // 4. Primer clic: Selecciona Origen
            zonaOrigen = zonaClic;
            zonaDestino = null;
            rutaActual = null;
            tripSidebar.setEstadoSinViaje();

        } else if (zonaDestino == null) {
            // 5. Segundo clic: Selecciona Destino
            zonaDestino = zonaClic;

            if (zonaOrigen.getId() == zonaDestino.getId()) {
                zonaDestino = null; // No se puede viajar a la misma zona
                return;
            }
            System.out.println("Calculando ruta desde: " + zonaOrigen.getNombre() + " (ID: " + zonaOrigen.getId() + ") hasta: " + zonaDestino.getNombre() + " (ID: " + zonaDestino.getId() + ")");

            // 6. Calcular la RUTA (la lista de zonas)
            rutaActual = gestorRutas.calcularRutaMasCorta(grafoZonas, zonaOrigen, zonaDestino);
            System.out.println("Ruta calculada: " + (rutaActual == null ? "NULL" : rutaActual.size() + " zonas"));

            // 7. Validar la ruta (una ruta válida debe tener al menos 2 zonas)
            if (rutaActual == null || rutaActual.size() < 2) {
                JOptionPane.showMessageDialog(rootPanel,
                        "No se encontró una ruta entre " + zonaOrigen.getNombre() + " y " + zonaDestino.getNombre(),
                        "Ruta no encontrada",
                        JOptionPane.ERROR_MESSAGE);
                // ¡ARREGLO! Si la ruta falla, reseteamos AMBAS zonas para no quedarnos atascados.
                zonaOrigen = null;  // <-- AÑADIR ESTA LÍNEA
                zonaDestino = null; // Esta línea ya estaba, la dejamos.
                rutaActual = null; // Limpia la ruta fallida
                return;
            }

            // --- ¡ARREGLO PRINCIPAL! ---
            // 8. Calcular la DISTANCIA total de la ruta del grafo
            double distanciaTotalRuta = 0.0;
            for (int i = 0; i < rutaActual.size() - 1; i++) {
                Zona a = rutaActual.get(i);
                Zona b = rutaActual.get(i + 1);
                // Llama al método de GrafoZonas que suma los pesos
                distanciaTotalRuta += grafoZonas.getDistanciaEntre(a, b);
            }

            // 9. Crear el Viaje (con el constructor simple)
            viajeActual = new Viaje(
                    new Coordenada(zonaOrigen.getLongitud(), zonaOrigen.getLatitud()), // Coordenada X, Y del origen
                    new Coordenada(zonaDestino.getLongitud(), zonaDestino.getLatitud()),
                    zonaOrigen.getNombre(),
                    zonaDestino.getNombre()// Coordenada X, Y del destino
            );

            // 10. Establecer la ruta y la distancia CALCULADA
            viajeActual.setRutaZonas(rutaActual);
            // (Asumiendo tu factor de conversión de 1 peso = 10 metros, como en tu clase Viaje)
            viajeActual.setDistanciaMetros(distanciaTotalRuta * 10);

            // 11. Actualizar la UI
            tripSidebar.actualizarViaje(viajeActual);
            mainFrame.mostrarSidebarDeViaje();

        } else {
            // 7. Tercer clic: Reinicia (selecciona nuevo origen)
            zonaOrigen = zonaClic;
            zonaDestino = null;
            rutaActual = null;
            tripSidebar.setEstadoSinViaje();
        }

        mapaCanvas.repaint();
    }

    private Zona buscarZonaCercana(double mundoX, double mundoY) {
        Zona masCercana = null;
        double minDistancia = Double.MAX_VALUE;

        // (Este umbral de 30px es en píxeles de pantalla,
        //  así que lo escalamos por el zoom)
        double umbral = 80 / zoom;

        for (Zona zona : grafoZonas.getZonas()) {
            // Comparamos las coordenadas del "mundo"
            double zonaX = zona.getLongitud(); // Asumiendo X
            double zonaY = zona.getLatitud();  // Asumiendo Y

            double dx = mundoX - zonaX;
            double dy = mundoY - zonaY;
            double distancia = Math.sqrt(dx*dx + dy*dy); // Distancia en píxeles de imagen

            if (distancia < minDistancia) {
                minDistancia = distancia;
                masCercana = zona;
            }
        }

        // Si el clic está dentro del umbral de la zona más cercana
        if (minDistancia < umbral) {
            return masCercana;
        }

        return null; // El clic fue muy lejos de cualquier zona
    }


    public void confirmarViaje() {
        if (viajeActual != null) {
            JOptionPane.showMessageDialog(rootPanel,
                    "¡Viaje solicitado exitosamente!\n\n" +
                            "Distancia: " + viajeActual.getDistanciaFormateada() + "\n" +
                            "Un conductor será asignado pronto.",
                    "Viaje Confirmado",
                    JOptionPane.INFORMATION_MESSAGE);
            resetearMapa();
        }
    }

    public void resetearMapa() {
        markerOrigen = null;
        markerDestino = null;
        viajeActual = null;
        tripSidebar.setEstadoSinViaje();
        mapaCanvas.repaint();
    }

    private void drawMap(Graphics g) {
        if (imagen == null) return;

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- 1. APLICAR TRANSFORMACIÓN (PAN/ZOOM) ---
        // Todo lo que dibujemos DESPUÉS de esto será en el "mundo" de la imagen
        g2d.translate(offsetX, offsetY);
        g2d.scale(zoom, zoom);

        // --- 2. DIBUJAR IMAGEN DE FONDO ---
        g2d.drawImage(imagen, 0, 0, null);

        // --- 3. DIBUJAR EL GRAFO (RUTAS Y ZONAS) ---

        // 3A. Dibujar Zonas (Nodos)
        if (grafoZonas != null) {
            for (Zona zona : grafoZonas.getZonas()) {
                boolean seleccionada = (zona == zonaOrigen || zona == zonaDestino);
                // Llamamos al "Smart" Renderer
                ZonaRenderer.drawZona(g2d, zona, seleccionada);
            }
        }
        System.out.println("Intentando dibujar ruta: " + (rutaActual == null ? "NO hay ruta" : "SI hay ruta (" + rutaActual.size() + " zonas)"));
        // 3B. Dibujar Ruta Calculada
        if (rutaActual != null) {
            RouteRenderer.drawRuta(g2d, rutaActual, zoom);
        }

        // --- 4. DIBUJAR CONDUCTORES ---
        if (conductores != null && grafoZonas != null) {
            for (Conductor conductor : conductores) {
                if (conductor.getZonaActualId() != null) {
                    Zona zonaConductor = grafoZonas.getZona(conductor.getZonaActualId());
                    if (zonaConductor != null) {
                        ConductorRenderer.drawConductor(g2d, conductor, zonaConductor);
                    }
                }
            }
        }

        g2d.dispose();
    }


    private void zoomIn() {
        Point center = new Point(mapaCanvas.getWidth() / 2, mapaCanvas.getHeight() / 2);
        zoomAtPoint(center, true);
    }

    private void zoomOut() {
        Point center = new Point(mapaCanvas.getWidth() / 2, mapaCanvas.getHeight() / 2);
        zoomAtPoint(center, false);
    }

    private void zoomAtPoint(Point point, boolean zoomIn) {
        double prevZoom = zoom;

        if (zoomIn) {
            zoom *= ZOOM_FACTOR;
        } else {
            zoom /= ZOOM_FACTOR;
        }

        zoom = Math.max(MIN_ZOOM, Math.min(zoom, MAX_ZOOM));

        double scale = zoom / prevZoom;
        offsetX = (int) (point.x - scale * (point.x - offsetX));
        offsetY = (int) (point.y - scale * (point.y - offsetY));

        constrainOffset();

        mapaCanvas.firePropertyChange("zoom", prevZoom, zoom);
        mapaCanvas.repaint();
    }

    private void centerMap() {
        if (imagen == null) return;

        final int canvasWidth = mapaCanvas.getWidth();
        final int canvasHeight = mapaCanvas.getHeight();

        // --- SOLUCIÓN ---
        // Establecemos el zoom inicial fijo a 0.8 en lugar de calcularlo.
        zoom = 0.8;
        System.out.println("[TRACE-ZOOM] cambio en " + getClass().getSimpleName() + " -> " + zoom);

        // Centramos la imagen con el nuevo zoom
        offsetX = (canvasWidth - (int) (imagen.getWidth() * zoom)) / 2;
        offsetY = (canvasHeight - (int) (imagen.getHeight() * zoom)) / 2;

        constrainOffset();
        mapaCanvas.repaint();
    }

    private void constrainOffset() {
        if (imagen == null) return;

        int canvasWidth = mapaCanvas.getWidth();
        int canvasHeight = mapaCanvas.getHeight();
        int imgWidth = (int) (imagen.getWidth() * zoom);
        int imgHeight = (int) (imagen.getHeight() * zoom);

        if (imgWidth < canvasWidth) {
            offsetX = (canvasWidth - imgWidth) / 2;
        } else {
            int maxOffsetX = 0;
            int minOffsetX = canvasWidth - imgWidth;
            offsetX = Math.max(minOffsetX, Math.min(maxOffsetX, offsetX));
        }

        if (imgHeight < canvasHeight) {
            offsetY = (canvasHeight - imgHeight) / 2;
        } else {
            int maxOffsetY = 0;
            int minOffsetY = canvasHeight - imgHeight;
            offsetY = Math.max(minOffsetY, Math.min(maxOffsetY, offsetY));
        }
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public void actualizarGrafo(GrafoZonas nuevoGrafo) {
        this.grafoZonas = nuevoGrafo;
        resetearMapa(); // Limpia selecciones y repinta el canvas
        System.out.println("mapaPanel: Grafo actualizado y mapa repintado.");
    }

    public void actualizarConductores(List<Conductor> nuevosConductores) {
        this.conductores = nuevosConductores;
        mapaCanvas.repaint();
        System.out.println("mapaPanel: Lista de conductores actualizada y mapa repintado.");
    }
}
