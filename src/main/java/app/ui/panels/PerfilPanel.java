package app.ui.panels;

import app.ui.components.StatCard;
import app.infrastructure.shared.constants.Colors;
import app.infrastructure.shared.SessionManager; // <-- Importar
import app.ui.MainFrame; // <-- Importar

import javax.swing.*;
import java.awt.*;

public class PerfilPanel extends JPanel {

    private MainFrame mainFrame; // <-- Añadido

    // 1. Modificar el constructor
    public PerfilPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame; // <-- Añadido

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Panel de Estadísticas (Tu código original) ---
        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        statsGrid.setOpaque(false);

        statsGrid.add(new StatCard("Total Viajes", "45", "📊", Colors.PRIMARY));
        statsGrid.add(new StatCard("En Proceso", "3", "🚗", Colors.ACCENT));
        statsGrid.add(new StatCard("Completados", "42", "✓", Colors.SUCCESS));
        statsGrid.add(new StatCard("Cancelados", "2", "✗", Colors.ERROR));

        add(statsGrid, BorderLayout.CENTER); // Las tarjetas van en el centro

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
            int confirm = JOptionPane.showConfirmDialog(
                    mainFrame,
                    "¿Estás seguro de que quieres cerrar sesión?",
                    "Confirmar Cierre de Sesión",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                // 1. Borra la sesión guardada
                SessionManager.clearSession();

                // 2. Cierra esta ventana (MainFrame)
                mainFrame.dispose();

                // 3. Abre una nueva instancia (que mostrará el login)
                // Usamos SwingUtilities para asegurar que se haga en el hilo de UI
                SwingUtilities.invokeLater(() -> {
                    new MainFrame();
                });
            }
        });
    }
}