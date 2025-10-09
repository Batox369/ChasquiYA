import javax.swing.*;
import UI.*;

import java.awt.*;

public class Main {
    public static void main(String[] args){
        System.out.println("Hola mundo de java");

        SwingUtilities.invokeLater(() ->{
            JFrame FramePrincipal = new JFrame("ChasquiYa");
            FramePrincipal.setContentPane(new MainUI().getPanel1());
            FramePrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            FramePrincipal.setSize(800, 600);
            //FramePrincipal.pack(); // Ajusta el tamaño automáticamente
            FramePrincipal.setLocationRelativeTo(null); // Centra la ventana
            FramePrincipal.setVisible(true);
        });
    }
}
