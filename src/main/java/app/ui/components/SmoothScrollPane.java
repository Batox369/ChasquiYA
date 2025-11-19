package app.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Una extensión de JScrollPane que implementa una animación de "scroll suave".
 * Reemplaza el comportamiento de scroll por defecto por una animación controlada por un Timer.
 */
public class SmoothScrollPane extends JScrollPane {

    private Timer scrollTimer;
    private int targetValue;
    private final int animationDuration = 200; // Duración en milisegundos
    private final int frameRate = 60;
    private long startTime;

    public SmoothScrollPane(Component view) {
        super(view);

        // 1. Eliminamos los listeners por defecto para tomar control total del scroll
        for (MouseWheelListener mwl : getMouseWheelListeners()) {
            removeMouseWheelListener(mwl);
        }

        // 2. Añadimos nuestro propio listener con la lógica de animación
        addMouseWheelListener(new SmoothMouseWheelListener());
    }

    private class SmoothMouseWheelListener implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            JScrollBar verticalScrollBar = getVerticalScrollBar();
            
            // --- ¡SOLUCIÓN! ---
            // Detenemos cualquier animación en curso para evitar conflictos.
            if (scrollTimer != null && scrollTimer.isRunning()) {
                scrollTimer.stop();
            }

            // El nuevo punto de partida es SIEMPRE la posición actual de la barra.
            int startValue = verticalScrollBar.getValue(); 

            // --- ¡SOLUCIÓN! ---
            // Aumentamos la cantidad de scroll para que se sienta más rápido y responsivo.
            int scrollAmount = e.getUnitsToScroll() * verticalScrollBar.getUnitIncrement() * 3; // Multiplicador de velocidad
            targetValue = startValue + scrollAmount;
            targetValue = Math.max(verticalScrollBar.getMinimum(), Math.min(targetValue, verticalScrollBar.getMaximum() - verticalScrollBar.getVisibleAmount()));

            if (startValue == targetValue) {
                return;
            }
            startTime = System.currentTimeMillis();

            int delay = 1000 / frameRate;
            scrollTimer = new Timer(delay, ae -> {
                long elapsed = System.currentTimeMillis() - startTime;
                double progress = Math.min(1.0, (double) elapsed / animationDuration);
                progress = 1 - Math.pow(1 - progress, 3); // Función de easing (suavizado)

                int currentValue = (int) (startValue + (targetValue - startValue) * progress);
                verticalScrollBar.setValue(currentValue);

                if (progress == 1.0) {
                    ((Timer) ae.getSource()).stop();
                }
            });

            scrollTimer.start();
        }
    }
}