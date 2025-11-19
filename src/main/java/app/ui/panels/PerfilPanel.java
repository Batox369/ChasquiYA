package app.ui.panels;

import app.domain.model.Viaje;
import app.domain.service.GestorHistorial;
import app.domain.structures.Nodo;
import app.ui.components.StatCard;
import app.ui.components.ModernConfirmationDialog;
import app.infrastructure.shared.constants.Colors;
import app.infrastructure.shared.SessionManager; // <-- Importar
import app.ui.MainFrame; // <-- Importar

import javax.swing.*;
import java.awt.*;

public class PerfilPanel extends JPanel {

    private MainFrame mainFrame; // <-- Añadido

    // --- Convertimos las tarjetas en miembros de la clase ---
    private StatCard totalViajesCard;
    private StatCard distanciaTotalCard; // <-- NUEVO
    private StatCard gastoTotalCard;     // <-- NUEVO
    private StatCard gastoPromedioCard;  // <-- NUEVO

    // 1. Modificar el constructor
    public PerfilPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame; // <-- Añadido

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Panel de Estadísticas Simplificado ---
        // Volvemos a un GridLayout para acomodar 4 tarjetas
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setOpaque(false);

        totalViajesCard = new StatCard("Total Viajes", "0", "📊", Colors.PRIMARY);
        distanciaTotalCard = new StatCard("Distancia Total", "0 km", "🗺️", Colors.ACCENT); // <-- NUEVO
        gastoTotalCard = new StatCard("Gasto Total", "S/ 0.00", "💰", Colors.SUCCESS); // <-- NUEVO
        gastoPromedioCard = new StatCard("Gasto Promedio", "S/ 0.00", "💸", new Color(245, 166, 35)); // <-- NUEVO

        statsPanel.add(totalViajesCard);
        statsPanel.add(distanciaTotalCard);
        statsPanel.add(gastoTotalCard);
        statsPanel.add(gastoPromedioCard);

        add(statsPanel, BorderLayout.CENTER); // La tarjeta va en el centro

        // --- 2. Panel para el Botón (NUEVO) ---
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Alineado a la derecha
        southPanel.setOpaque(false);
        southPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // Espacio arriba

        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setBackground(new Color(220, 53, 69)); // Color rojo
        btnCerrarSesion.setForeground(Color.WHITE);
        btnCerrarSesion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.setCursor(new Cursor(Cursor.HAND_CURSOR));

        southPanel.add(btnCerrarSesion);

        add(southPanel, BorderLayout.SOUTH); // El botón va abajo

        // --- 3. Acción del Botón (NUEVO) ---
        btnCerrarSesion.addActionListener(e -> {
            ModernConfirmationDialog dialog = new ModernConfirmationDialog(mainFrame, "Confirmar Cierre de Sesión", "¿Estás seguro de que quieres cerrar sesión?");
            boolean confirmed = dialog.showDialog();

            if (confirmed) {
                mainFrame.doLogout(); // <-- ¡SOLUCIÓN! Llama al nuevo método del MainFrame
            }
        });
    }

    /**
     * Obtiene los datos reales de los servicios y actualiza las tarjetas de estadísticas.
     */
    public void actualizarEstadisticas() {
        // 1. Obtener el historial de viajes
        var historial = GestorHistorial.getInstancia().getHistorialEnMemoria();
        int totalViajes = historial.getTamano();
        double distanciaTotalMetros = 0;
        double gastoTotal = 0;

        // 2. Recorrer la lista enlazada para sumar las distancias
        Nodo<Viaje> actual = historial.getCabeza();
        while (actual != null) {
            distanciaTotalMetros += actual.dato.getDistanciaMetros();
            gastoTotal += actual.dato.getPrecio();
            actual = actual.siguiente;
        }

        // 3. Calcular y formatear los valores
        double distanciaKm = distanciaTotalMetros / 1000.0;
        String distanciaFormateada = String.format("%.1f km", distanciaKm);

        double gastoPromedio = (totalViajes > 0) ? (gastoTotal / totalViajes) : 0;
        String gastoTotalFormateado = String.format("S/ %.2f", gastoTotal);
        String gastoPromedioFormateado = String.format("S/ %.2f", gastoPromedio);

        // 4. Actualizar los valores de todas las tarjetas
        totalViajesCard.setValue(String.valueOf(totalViajes));
        distanciaTotalCard.setValue(distanciaFormateada);
        gastoTotalCard.setValue(gastoTotalFormateado);
        gastoPromedioCard.setValue(gastoPromedioFormateado);
    }
}