package app.ui.panels;

import app.domain.model.Viaje;
import app.domain.service.GestorHistorial;
import app.domain.structures.ListaEnlazadaSimple;
import app.domain.structures.Nodo;
import app.ui.components.ModernScrollBarUI;
import app.infrastructure.shared.constants.Colors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;

public class HistorialPanel extends JPanel {

    private JPanel listPanel;
    private JLabel estadoLabel;  // ⬅ NUEVO
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'a las' HH:mm");

    public HistorialPanel() {
        setLayout(new BorderLayout(0, 20));
        setOpaque(false);
        setBorder(new EmptyBorder(20, 40, 20, 40));

        // Título
        JLabel titleLabel = new JLabel("Historial de Viajes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Colors.TEXT_PRIMARY);
        add(titleLabel, BorderLayout.NORTH);

        // Mensaje de estado (loading)
        estadoLabel = new JLabel("");
        estadoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        estadoLabel.setForeground(Colors.TEXT_SECONDARY);
        add(estadoLabel, BorderLayout.SOUTH);

        // Panel para la lista con scroll
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Colors.CARD_BG);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Solo actualiza la vista. La recarga desde BD se hace ASINCRÓNICAMENTE desde MainFrame.
     */
    public void refrescarHistorial() {
        actualizarVista();
    }

    public void mostrarLoading() {
        estadoLabel.setText("Cargando historial...");
    }

    public void ocultarLoading() {
        estadoLabel.setText("");
    }

    public void actualizarVista() {
        listPanel.removeAll();

        ListaEnlazadaSimple<Viaje> historial = GestorHistorial.getInstancia().getHistorialEnMemoria();
        Nodo<Viaje> actual = historial.getCabeza();

        if (actual == null) {
            JLabel emptyLabel = new JLabel("Aún no has realizado ningún viaje.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            emptyLabel.setForeground(Colors.TEXT_SECONDARY);

            listPanel.setLayout(new BorderLayout());
            listPanel.add(emptyLabel, BorderLayout.CENTER);
        } else {
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

            while (actual != null) {
                listPanel.add(createTripCard(actual.dato));
                listPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                actual = actual.siguiente;
            }
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createTripCard(Viaje viaje) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Colors.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER, 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel routeLabel = new JLabel(String.format("De %s a %s", viaje.getNombreOrigen(), viaje.getNombreDestino()));
        routeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        routeLabel.setForeground(Colors.TEXT_PRIMARY);

        JLabel dateLabel = new JLabel(dateFormat.format(viaje.getFecha()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(Colors.TEXT_SECONDARY);

        infoPanel.add(routeLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(dateLabel);

        JLabel priceLabel = new JLabel(String.format("S/ %.2f", viaje.getPrecio()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        priceLabel.setForeground(Colors.SUCCESS);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(priceLabel, BorderLayout.EAST);

        return card;
    }
}
