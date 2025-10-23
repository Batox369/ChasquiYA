import javax.swing.*;

import ui.MainFrame;

public class Main  {
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
            mainFrame.mostrarMapa();
        });
    }
}
