package app.ui.components.modern;

import app.infrastructure.shared.constants.Colors;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * Una implementación de UI para JScrollBar con una apariencia moderna y minimalista.
 * El scrollbar es semi-transparente y se vuelve más opaco al pasar el ratón por encima.
 */
public class ModernScrollBarUI extends BasicScrollBarUI {

    private final int SCROLL_BAR_ALPHA = 150;
    private final int SCROLL_BAR_ALPHA_ROLLOVER = 220;
    private final int THUMB_SIZE = 8;
    private final Color THUMB_COLOR = Colors.PRIMARY;

    @Override
    protected void configureScrollBarColors() {
        // No se necesita, lo manejamos nosotros
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        // No pintar el "track" o fondo del scrollbar para hacerlo transparente
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Define la opacidad basada en si el ratón está sobre la barra
        int alpha = isThumbRollover() ? SCROLL_BAR_ALPHA_ROLLOVER : SCROLL_BAR_ALPHA;
        g2.setColor(new Color(THUMB_COLOR.getRed(), THUMB_COLOR.getGreen(), THUMB_COLOR.getBlue(), alpha));

        // Dibuja el "thumb" (la parte que se arrastra) como un rectángulo redondeado
        g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, THUMB_SIZE, THUMB_SIZE);
        g2.dispose();
    }

    @Override
    protected Dimension getMinimumThumbSize() {
        return new Dimension(THUMB_SIZE, THUMB_SIZE);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    /**
     * Crea un botón invisible y de tamaño cero para reemplazar las flechas
     * de incremento/decremento del scrollbar.
     */
    private JButton createZeroButton() {
        JButton button = new JButton();
        Dimension zeroDim = new Dimension(0, 0);
        button.setPreferredSize(zeroDim);
        button.setMinimumSize(zeroDim);
        button.setMaximumSize(zeroDim);
        return button;
    }
}