package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainUI {
    private JPanel panel1;
    private JPanel PanelMapa;
    private JLabel Texto;
    private JButton buscarConductoresButton;

    public MainUI() {
        //$$$setupUI$$$();
        Texto.setForeground(Color.WHITE);

        // Aplica el estilo a todos los botones del panel principal
        aplicarEstiloBotones(panel1);
    }

    private void aplicarEstiloBotones(Container contenedor) {
        for (Component c : contenedor.getComponents()) {
            if (c instanceof JButton boton) {

                // Estilo base del botón
                boton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
                boton.setBackground(new Color(45, 45, 45));  // color base
                boton.setForeground(Color.WHITE);
                boton.setFocusPainted(false);
                boton.setBorderPainted(false);
                boton.setContentAreaFilled(false);
                boton.setOpaque(true);

                // Interacción dinámica con el mouse
                boton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        boton.setBackground(new Color(65, 65, 65)); // hover
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        boton.setBackground(new Color(45, 45, 45)); // normal
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        boton.setBackground(new Color(30, 30, 30)); // click
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        boton.setBackground(new Color(65, 65, 65)); // vuelve al hover
                    }
                });
            }

            // Si hay paneles hijos, aplica también ahí
            if (c instanceof Container subContenedor) {
                aplicarEstiloBotones(subContenedor);
            }
        }
    }

    public JPanel getPanel1() {
        return panel1;
    }
}
