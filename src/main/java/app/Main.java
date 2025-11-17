package app;

import app.infrastructure.persistence.ConexionBD;
import app.ui.MainFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        // --- PASO 1: Conectar a la BD (Con verificación) ---
        Connection dbConnection = null;

        try {
            dbConnection = ConexionBD.getInstance().getConnection();
        } catch (SQLException e) {
            System.err.println("Error al obtener conexión con la BD: " + e.getMessage());
            return; // o lo que necesites
        }


        if (dbConnection == null) {
            System.err.println("Fallo crítico: No se pudo conectar a la base de datos.");
            JOptionPane.showMessageDialog(null,
                    "Error: No se pudo conectar a la base de datos.\nLa aplicación se cerrará.",
                    "Error de Conexión",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return; // Salir
        }
        System.out.println("Base de datos conectada.");

        // --- PASO 2: Iniciar el Frame Principal ---
        // El MainFrame se encargará de toda la lógica de sesión
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}