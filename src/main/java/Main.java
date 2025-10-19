import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import Core.MainFrame;
import Models.Conductor;

public class Main  {
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
            mainFrame.mostrarMapa();
        });
    }
}
