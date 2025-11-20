package app.ui.panels;

import app.infrastructure.shared.constants.Colors;

import javax.swing.*;
import java.awt.*;

public class LoadingPanel extends JPanel {

    private JLabel statusLabel;
    private JProgressBar progressBar;

    public LoadingPanel() {
        setLayout(new GridBagLayout());
        setBackground(Colors.CARD_BG); // Fondo blanco

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 10, 0);

        // --- Logo ---
        JLabel logoLabel = new JLabel();
        try {
            java.net.URL imgUrl = getClass().getResource("/logo.png");
            if (imgUrl != null) {
                ImageIcon originalIcon = new ImageIcon(imgUrl);
                // --- ¡CORRECCIÓN! Hacemos el logo mucho más grande ---
                Image image = originalIcon.getImage().getScaledInstance(-1, 220, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(image));
            }
        } catch (Exception e) {
            logoLabel.setText("ChasquiYA");
            logoLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
            logoLabel.setForeground(Colors.TEXT_PRIMARY);
        }
        add(logoLabel, gbc);

        // --- Barra de Progreso ---
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true); // Estilo "cargando" infinito
        progressBar.setPreferredSize(new Dimension(250, 8));
        progressBar.setForeground(Colors.PRIMARY);
        progressBar.setBackground(Colors.BORDER); // Un fondo más sutil para la barra
        progressBar.setBorderPainted(false);
        add(progressBar, gbc);

        // --- Etiqueta de Estado ---
        statusLabel = new JLabel("Iniciando aplicación...");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statusLabel.setForeground(Colors.TEXT_SECONDARY);
        add(statusLabel, gbc);
    }

    /**
     * Actualiza el mensaje de estado que ve el usuario.
     * Este método es seguro para ser llamado desde otros hilos.
     * @param status El nuevo mensaje a mostrar.
     */
    public void setStatus(String status) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(status));
    }
}