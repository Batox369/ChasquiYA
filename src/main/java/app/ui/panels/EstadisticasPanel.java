package app.ui.panels;

import app.domain.model.Conductor;
import app.domain.model.Zona;
import app.domain.repository.ConductorRepository;
import app.domain.repository.GrafoRepository;
import app.infrastructure.persistence.MySQLConductorRepository;
import app.infrastructure.persistence.MySQLGrafoRepository;
import app.infrastructure.shared.constants.Colors;
import app.domain.structures.ListaEnlazadaSimple;
import app.domain.structures.Nodo;
import app.ui.components.modern.ModernButton;
import app.ui.components.SmoothScrollPane;
import app.ui.components.modern.ModernScrollBarUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EstadisticasPanel extends JPanel {

    private JPanel topConductoresPanel;
    private JPanel topZonasPanel;
    private JLabel estadoLabel;

    // --- ¡NUEVO! ---
    // Panel y Layout para cambiar entre vistas
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public EstadisticasPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(20, 40, 20, 40));

        // Título principal
        JLabel titleLabel = new JLabel("Estadísticas del Sistema");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Colors.TEXT_PRIMARY);

        // Mensaje de estado (loading)
        estadoLabel = new JLabel("");
        estadoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        estadoLabel.setForeground(Colors.TEXT_SECONDARY);
        add(estadoLabel, BorderLayout.SOUTH);

        // --- ¡DISEÑO CORREGIDO! Panel de botones de navegación ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        ModernButton btnTopConductores = new ModernButton("🏆 Top Conductores");
        ModernButton btnTopZonas = new ModernButton("📍 Zonas Frecuentes");

        // Agrupamos los botones para que solo uno pueda estar seleccionado a la vez
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(btnTopConductores);
        buttonGroup.add(btnTopZonas);

        btnTopConductores.setSelected(true); // El primero está seleccionado por defecto

        buttonPanel.add(btnTopConductores);
        buttonPanel.add(btnTopZonas);

        // Panel superior que contiene título y botones
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // --- ¡NUEVO! Panel principal con CardLayout para las listas ---
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);

        // Pestaña 1: Top Conductores
        topConductoresPanel = new JPanel();
        topConductoresPanel.setLayout(new BoxLayout(topConductoresPanel, BoxLayout.Y_AXIS));
        topConductoresPanel.setOpaque(false);
        JScrollPane scrollConductores = new SmoothScrollPane(topConductoresPanel);
        scrollConductores.setBorder(null);
        scrollConductores.getVerticalScrollBar().setUI(new ModernScrollBarUI());

        // Pestaña 2: Top Zonas
        topZonasPanel = new JPanel();
        topZonasPanel.setLayout(new BoxLayout(topZonasPanel, BoxLayout.Y_AXIS));
        topZonasPanel.setOpaque(false);
        JScrollPane scrollZonas = new SmoothScrollPane(topZonasPanel);
        scrollZonas.setBorder(null);
        scrollZonas.getVerticalScrollBar().setUI(new ModernScrollBarUI());

        cardPanel.add(scrollConductores, "CONDUCTORES");
        cardPanel.add(scrollZonas, "ZONAS");
        add(cardPanel, BorderLayout.CENTER);

        // --- Listeners para los botones ---
        btnTopConductores.addActionListener(e -> cardLayout.show(cardPanel, "CONDUCTORES"));
        btnTopZonas.addActionListener(e -> cardLayout.show(cardPanel, "ZONAS"));

        // Ya no cargamos las estadísticas en el constructor para evitar bloquear la UI.
        // Se cargarán de forma asíncrona desde el MainFrame.
    }

    public void mostrarLoading() {
        estadoLabel.setText("Cargando estadísticas...");
        // Limpiamos las listas mientras carga para evitar mostrar datos viejos
        topConductoresPanel.removeAll();
        topZonasPanel.removeAll();
        topConductoresPanel.revalidate();
        topConductoresPanel.repaint();
        topZonasPanel.revalidate();
        topZonasPanel.repaint();
    }

    public void ocultarLoading() {
        estadoLabel.setText("");
    }

    public void refrescarEstadisticas(ListaEnlazadaSimple<Conductor> topConductores, ListaEnlazadaSimple<Zona> topZonas) {
        actualizarLista(topConductoresPanel, topConductores);
        actualizarLista(topZonasPanel, topZonas);
    }

    private void actualizarLista(JPanel panel, ListaEnlazadaSimple<?> items) {
        panel.removeAll();
        if (items.getTamano() == 0) {
            JLabel emptyLabel = new JLabel("No hay datos disponibles.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            emptyLabel.setForeground(Colors.TEXT_SECONDARY);
            panel.setLayout(new BorderLayout());
            panel.add(emptyLabel, BorderLayout.CENTER);
        } else {
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            int rank = 1;
            Nodo<?> actual = items.getCabeza();
            while (actual != null) {
                Object item = actual.dato;
                panel.add(createRankingCard(rank, item));
                panel.add(Box.createRigidArea(new Dimension(0, 10)));
                rank++;
                actual = actual.siguiente;
            }
        }
        panel.revalidate();
        panel.repaint();
    }

    private JPanel createRankingCard(int rank, Object item) {
        // --- ¡DISEÑO MEJORADO! Inspirado en HistorialPanel ---
        JPanel card = new JPanel(new BorderLayout(20, 0));
        card.setBackground(Colors.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER, 1),
                new EmptyBorder(15, 20, 15, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // Panel para el ranking y el nombre
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        infoPanel.setOpaque(false);

        JLabel rankLabel = new JLabel(String.format("#%d", rank));
        rankLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        rankLabel.setForeground(Colors.PRIMARY);

        String nombre = "";
        int viajes = 0;
        if (item instanceof Conductor) {
            nombre = ((Conductor) item).getNombreCompleto();
            viajes = ((Conductor) item).getViajesRealizados();
        } else if (item instanceof Zona) {
            nombre = ((Zona) item).getNombre();
            viajes = ((Zona) item).getNumeroViajes();
        }

        JLabel nameLabel = new JLabel(nombre);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(Colors.TEXT_PRIMARY);

        JLabel viajesLabel = new JLabel(String.format("%d viajes", viajes));
        viajesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        viajesLabel.setForeground(Colors.TEXT_SECONDARY);

        infoPanel.add(rankLabel);
        infoPanel.add(nameLabel);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(viajesLabel, BorderLayout.EAST);

        return card;
    }
}