package app.ui.views;

import app.infrastructure.shared.constants.Colors;
import app.ui.components.ModernButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SideNavigation extends JPanel {

    private ModernButton solicitarViajeBtn;
    private ModernButton historialBtn;
    private ModernButton perfilBtn;

    // Componentes para la sección de administrador
    private ModernButton adminBtn;
    private JSeparator adminSeparator;
    private JLabel adminTitle;

    public SideNavigation() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Colors.CARD_BG);
        setPreferredSize(new Dimension(250, 0));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Colors.BORDER),
                BorderFactory.createEmptyBorder(30, 15, 30, 15)
        ));

        JLabel navTitle = new JLabel("MENÚ");
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        navTitle.setForeground(Colors.TEXT_SECONDARY);
        navTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        navTitle.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 0));
        add(navTitle);

        // --- ¡CORRECCIÓN! Usamos las rutas a las imágenes en lugar de emojis ---
        // --- ¡CORRECCIÓN DE RUTA! --- Se quita la barra inicial '/'
        solicitarViajeBtn = new ModernButton("Solicitar Viaje", "coche.png");
        historialBtn = new ModernButton("Historial", "reloj.png");
        perfilBtn = new ModernButton("Mi Perfil", "user.png");


        solicitarViajeBtn.setSelected(true);

        add(solicitarViajeBtn);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(historialBtn);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(perfilBtn);
        add(Box.createVerticalGlue());

        // --- Sección de Administrador (inicialmente oculta) ---
        adminSeparator = new JSeparator();
        adminSeparator.setForeground(Colors.BORDER);
        adminSeparator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        adminTitle = new JLabel("ADMINISTRADOR");
        adminTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        adminTitle.setForeground(Colors.TEXT_SECONDARY);
        adminTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        adminTitle.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 0));

        adminBtn = new ModernButton("Panel de Control", "ajustes.png");

        add(adminSeparator);
        add(adminTitle);
        add(adminBtn);

        // Por defecto, la sección de admin está oculta
        setAdminFeaturesVisible(true);
    }

    public void addSolicitarViajeListener(ActionListener listener) {
        solicitarViajeBtn.addActionListener(listener);
    }

    public void addHistorialListener(ActionListener listener) {
        historialBtn.addActionListener(listener);
    }

    public void addPerfilListener(ActionListener listener) {
        perfilBtn.addActionListener(listener);
    }

    public void addAdminListener(ActionListener listener) {
        adminBtn.addActionListener(listener);
    }

    public void setSelectedButton(String buttonName) {
        solicitarViajeBtn.setSelected(false);
        historialBtn.setSelected(false);
        perfilBtn.setSelected(false);
        adminBtn.setSelected(false);

        switch (buttonName) {
            case "solicitar":
                solicitarViajeBtn.setSelected(true);
                break;
            case "historial":
                historialBtn.setSelected(true);
                break;
            case "perfil":
                perfilBtn.setSelected(true);
                break;
            case "admin":
                adminBtn.setSelected(true);
                break;
        }
    }

    /**
     * Muestra u oculta la sección de administración en la barra de navegación.
     * Llama a este método después de que un usuario inicie sesión.
     * @param visible true para mostrar, false para ocultar.
     */
    public void setAdminFeaturesVisible(boolean visible) {
        adminSeparator.setVisible(visible);
        adminTitle.setVisible(visible);
        adminBtn.setVisible(visible);
    }
}
