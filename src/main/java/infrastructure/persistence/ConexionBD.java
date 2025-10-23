package infrastructure.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
// 1. ¡ACTUALIZA ESTOS 4 DATOS CON LOS DE VULTAM!

    // Pega aquí el "ENDPOINT" que te dio Vultam (Ej: b1.vultam.com:25060)
    private static final String HOST_CON_PUERTO = "104.243.38.185:3306";

    // Pega aquí el "DATABASE NAME" (s16555_chasquiya_db)
    private static final String DATABASE_NAME = "s16555_chasquiya_db";

    // Pega aquí el "USERNAME" que te dio Vultam
    private static final String USER = "u16555_Jvpf5iKdYP";

    // Pega aquí la CONTRASEÑA que obtienes al presionar el ícono (👁️)
    private static final String PASSWORD = "6wHA.h@zYvH9j^qzp8rhF7Sw";

    // --- Esta línea construye la URL de conexión ---
    private static final String URL = "jdbc:mysql://" + HOST_CON_PUERTO + "/" + DATABASE_NAME;


    // --- El resto de la clase es igual ---

    private static ConexionBD instance;
    private Connection connection;

    private ConexionBD() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conexión a la BD de Vultam (" + DATABASE_NAME + ") exitosa.");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver JDBC no encontrado. (¿Falta en pom.xml?)");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos de Vultam.");
            e.printStackTrace();
        }
    }

    public static synchronized ConexionBD getInstance() {
        if (instance == null) {
            instance = new ConexionBD();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión a la BD cerrada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
