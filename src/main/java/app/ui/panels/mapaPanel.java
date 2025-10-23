package app.ui.panels;

import app.domain.model.Coordenada;
import app.domain.model.Viaje;
import app.ui.components.MapMarker;
import app.ui.components.RouteRenderer;
import app.ui.components.*;
import app.domain.model.*;
import app.infrastructure.shared.constants.Colors;
import app.ui.MainFrame;
import app.ui.views.TripSidebarPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class mapaPanel {
    private JPanel rootPanel;
    private JPanel mapaCanvas;
    private JPanel controlsPanel;

    private BufferedImage imagen;
    private double zoom = 1.0;
    private int offsetX = 0;
    private int offsetY = 0;
    private int lastX, lastY;

    private static final double MIN_ZOOM = 0.5;
    private static final double MAX_ZOOM = 5.0;
    private static final double ZOOM_FACTOR = 1.1;

    private MapMarker markerOrigen;
    private MapMarker markerDestino;
    private Viaje viajeActual;
    private boolean isDragging = false;

    private MainFrame mainFrame;
    private TripSidebarPanel tripSidebar;

    public mapaPanel(MainFrame mainFrame, TripSidebarPanel tripSidebar) {
        this.mainFrame = mainFrame;
        this.tripSidebar = tripSidebar;
        loadImage();
        initComponents();
        setupListeners();

        // Ahora, ejecutamos el centrado solo cuando el panel esté realmente visible
        rootPanel.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && rootPanel.isShowing()) {
                SwingUtilities.invokeLater(() -> {
                    centerMap();
                    mapaCanvas.revalidate();
                    mapaCanvas.repaint();
                });
            }
        });
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

        createControlsPanel();

        rootPanel.add(controlsPanel, BorderLayout.NORTH);
        rootPanel.add(mapaCanvas, BorderLayout.CENTER);
    }

    private void createControlsPanel() {
        controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlsPanel.setBackground(Colors.SECONDARY);

        JButton zoomInBtn = createControlButton("+", "Acercar");
        zoomInBtn.addActionListener(e -> zoomIn());

        JButton zoomOutBtn = createControlButton("-", "Alejar");
        zoomOutBtn.addActionListener(e -> zoomOut());

        JButton resetBtn = createControlButton("⟲", "Centrar");
        resetBtn.addActionListener(e -> centerMap());

        JLabel zoomLabel = new JLabel(String.format("Zoom: %.0f%%", zoom * 100));
        zoomLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        zoomLabel.setForeground(Colors.TEXT_SECONDARY);

        mapaCanvas.addPropertyChangeListener("zoom", evt -> {
            zoomLabel.setText(String.format("Zoom: %.0f%%", zoom * 100));
        });

        controlsPanel.add(zoomOutBtn);
        controlsPanel.add(zoomLabel);
        controlsPanel.add(zoomInBtn);
        controlsPanel.add(resetBtn);
    }

    private JButton createControlButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(45, 35));
        button.setBackground(Colors.PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Colors.HOVER);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(Colors.PRIMARY);
            }
        });

        return button;
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
                constrainOffset();
                mapaCanvas.repaint();
            }
        });
    }

    private void handleMapClick(Point clickPoint) {
        Coordenada coordenada = new Coordenada(clickPoint, zoom, offsetX, offsetY);

        if (markerOrigen == null) {
            markerOrigen = new MapMarker(coordenada, "origen");
            markerDestino = null;
            viajeActual = null;
        } else if (markerDestino == null) {
            markerDestino = new MapMarker(coordenada, "destino");
            viajeActual = new Viaje(markerOrigen.getCoordenada(),
                    markerDestino.getCoordenada());

            if (viajeActual.getDistanciaMetros() < 100) {
                JOptionPane.showMessageDialog(rootPanel,
                        "La distancia mínima es de 100 metros.",
                        "Distancia muy corta",
                        JOptionPane.WARNING_MESSAGE);
                markerDestino = null;
                viajeActual = null;
                mapaCanvas.repaint();
                return;
            }

            // Actualizar sidebar y mostrarlo
            tripSidebar.actualizarViaje(viajeActual);
            mainFrame.mostrarSidebarDeViaje();
        } else {
            markerOrigen = new MapMarker(coordenada, "origen");
            markerDestino = null;
            viajeActual = null;
            tripSidebar.setEstadoSinViaje();
        }

        mapaCanvas.repaint();
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

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g2d.translate(offsetX, offsetY);
        g2d.scale(zoom, zoom);
        g2d.drawImage(imagen, 0, 0, null);

        g2d.scale(1/zoom, 1/zoom);
        g2d.translate(-offsetX, -offsetY);

        if (markerOrigen != null && markerDestino != null) {
            RouteRenderer.drawRoute(g2d,
                    markerOrigen.getCoordenada(),
                    markerDestino.getCoordenada(),
                    zoom, offsetX, offsetY);
        }

        if (markerOrigen != null) {
            markerOrigen.draw(g2d, zoom, offsetX, offsetY);
        }
        if (markerDestino != null) {
            markerDestino.draw(g2d, zoom, offsetX, offsetY);
        }
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

        int canvasWidth = mapaCanvas.getWidth();
        int canvasHeight = mapaCanvas.getHeight();
        int imgWidth = (int) (imagen.getWidth() * zoom);
        int imgHeight = (int) (imagen.getHeight() * zoom);

        offsetX = (canvasWidth - imgWidth) / 2;
        offsetY = (canvasHeight - imgHeight) / 2;

        if (imgWidth > canvasWidth || imgHeight > canvasHeight) {
            double scaleX = (double) canvasWidth / imagen.getWidth();
            double scaleY = (double) canvasHeight / imagen.getHeight();
            zoom = Math.min(scaleX, scaleY) * 0.9;

            imgWidth = (int) (imagen.getWidth() * zoom);
            imgHeight = (int) (imagen.getHeight() * zoom);
            offsetX = (canvasWidth - imgWidth) / 2;
            offsetY = (canvasHeight - imgHeight) / 2;
        }

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
}
