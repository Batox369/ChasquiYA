package Components;

import Constants.Colors;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SideNavigation extends JPanel {

    private ModernButton solicitarViajeBtn;
    private ModernButton historialBtn;
    private ModernButton configuracionBtn;
    private ModernButton perfilBtn;

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

        solicitarViajeBtn = new ModernButton("Solicitar Viaje", "");
        historialBtn = new ModernButton("Historial", "");
        configuracionBtn = new ModernButton("Configuración", "");
        perfilBtn = new ModernButton("Mi Perfil", "");

        solicitarViajeBtn.setSelected(true);

        add(solicitarViajeBtn);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(historialBtn);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(configuracionBtn);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(perfilBtn);
        add(Box.createVerticalGlue());
    }

    public void addSolicitarViajeListener(ActionListener listener) {
        solicitarViajeBtn.addActionListener(listener);
    }

    public void addHistorialListener(ActionListener listener) {
        historialBtn.addActionListener(listener);
    }

    public void addConfiguracionListener(ActionListener listener) {
        configuracionBtn.addActionListener(listener);
    }

    public void addPerfilListener(ActionListener listener) {
        perfilBtn.addActionListener(listener);
    }

    public void setSelectedButton(String buttonName) {
        solicitarViajeBtn.setSelected(false);
        historialBtn.setSelected(false);
        configuracionBtn.setSelected(false);
        perfilBtn.setSelected(false);

        switch (buttonName) {
            case "solicitar":
                solicitarViajeBtn.setSelected(true);
                break;
            case "historial":
                historialBtn.setSelected(true);
                break;
            case "configuracion":
                configuracionBtn.setSelected(true);
                break;
            case "perfil":
                perfilBtn.setSelected(true);
                break;
        }
    }
}
