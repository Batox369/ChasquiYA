package app.ui.components.modern;

import app.infrastructure.shared.constants.Colors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class ModernButton extends JToggleButton {

    private Color defaultColor = Colors.CARD_BG;
    private Color hoverColor = new Color(230, 232, 235); // Un gris muy claro para el hover
    private Color selectedColor = Colors.PRIMARY;

    private boolean isHovered = false;
    private final int cornerRadius = 12; // Radio de las esquinas redondeadas

    public ModernButton(String text, String icon) {
        super(text); // El texto se manejará por el método de pintado
        initStyles(icon);
    }

    public ModernButton(String text) {
        this(text, "");
    }

    private void initStyles(String icon) {
        // --- Estilos Base ---
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false); // Esencial para el pintado personalizado
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setMaximumSize(new Dimension(220, 45));
        setPreferredSize(new Dimension(220, 45));

        // --- ¡CORRECCIÓN DE LAYOUT! ---
        // Usamos un BoxLayout horizontal para alinear el ícono y el texto.
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        // --- Lógica de Hover ---
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });

        // --- Icono y Texto como JLabels para centrado y estilo ---
        JLabel iconLabel = new JLabel();

        // --- ¡CORRECCIÓN! Lógica para cargar el ícono desde una imagen ---
        try {
            java.net.URL imgUrl = Thread.currentThread().getContextClassLoader().getResource(icon);
            if (imgUrl != null) {
                ImageIcon originalIcon = new ImageIcon(imgUrl);
                // Redimensionamos el ícono para un tamaño consistente (ej. 20x20 píxeles)
                Image image = originalIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                iconLabel.setIcon(new ImageIcon(image));
            } else {
                System.err.println("Icono no encontrado en la ruta: " + icon);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar el icono: " + icon);
        }
        iconLabel.setAlignmentY(Component.CENTER_ALIGNMENT); // Centrado vertical

        JLabel textLabel = new JLabel(getText()); // Obtenemos el texto del JToggleButton
        textLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        textLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

        // Limpiamos el texto del botón base para que no se pinte dos veces
        setText("");

        // Añadimos los componentes con espaciado
        add(Box.createHorizontalStrut(15)); // Padding izquierdo
        add(iconLabel);
        add(Box.createHorizontalStrut(15)); // Espacio entre ícono y texto
        add(textLabel);
        add(Box.createHorizontalGlue()); // Empuja todo a la izquierda
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color backgroundColor;
        Color foregroundColor;

        if (isSelected()) {
            backgroundColor = selectedColor;
            foregroundColor = Color.WHITE;
        } else if (isHovered) {
            backgroundColor = hoverColor;
            foregroundColor = Colors.TEXT_PRIMARY;
        } else {
            backgroundColor = defaultColor;
            foregroundColor = Colors.TEXT_PRIMARY;
        }

        // Dibuja el fondo redondeado
        g2.setColor(backgroundColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

        // --- ¡CORRECCIÓN DE PINTADO! ---
        // Iteramos sobre los componentes hijos (nuestros JLabels) y les asignamos el color correcto.
        for (Component comp : getComponents()) {
            if (comp instanceof JLabel) {
                comp.setForeground(foregroundColor);
            }
        }

        // --- ¡SOLUCIÓN! ---
        // En lugar de llamar a super.paintComponent(), que puede causar parpadeos,
        // llamamos directamente a super.paintChildren() para pintar nuestros JLabels internos.
        super.paintChildren(g);
        g2.dispose();
    }

}
