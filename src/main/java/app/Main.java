package app;

import app.ui.MainFrame;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        // Inicia la interfaz gráfica en el hilo de eventos de Swing.
        // Toda la lógica de carga pesada se moverá al MainFrame.
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}