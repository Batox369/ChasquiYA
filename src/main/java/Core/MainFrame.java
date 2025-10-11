package Core;

import javax.swing.*;
import java.awt.*;
import UI.mapaPanel;

public class MainFrame extends JFrame {

    private JPanel mainFrame;
    private JPanel bgFrame;
    private JPanel topBar;
    private JPanel bottomBar;
    private JPanel selectedPanel;
    private JPanel Dashboard;
    private JButton solicitarViajeButton;
    private JButton historialButton;
    private JButton configuracionButton;
    private JButton perfilButton;

    private mapaPanel panelMapa; // mantenemos una única instancia del mapa

    public MainFrame() {
        setContentPane(mainFrame);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1020, 640);
        setLocationRelativeTo(null);
        setResizable(false);

        // Inicializamos el panel del mapa solo una vez
        panelMapa = new mapaPanel();

        // Asignamos acciones a los botones (ejemplo)
        solicitarViajeButton.addActionListener(e -> mostrarMapa());
    }

    public void mostrarMapa() {
        selectedPanel.removeAll();
        selectedPanel.setLayout(new BorderLayout());
        selectedPanel.add(panelMapa.getRootPanel(), BorderLayout.CENTER);
        selectedPanel.revalidate();
        selectedPanel.repaint();
    }

    private void createUIComponents() {
        // Aquí puedes crear componentes personalizados si usas IntelliJ GUI Designer
    }
}