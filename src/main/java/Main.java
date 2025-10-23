import javax.swing.*;

import ui.MainFrame;
import infrastructure.persistence.ConexionBD;

public class Main  {
    public static void main(String[] args){
        ConexionBD.getInstance();
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
            mainFrame.mostrarMapa();
        });
    }
}
