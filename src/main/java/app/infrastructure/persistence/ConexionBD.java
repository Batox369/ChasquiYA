package app.infrastructure.persistence;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String HOST_CON_PUERTO = dotenv.get("HOST_CON_PUERTO");
    private static final String DATABASE_NAME = dotenv.get("DATABASE_NAME");
    private static final String USER = dotenv.get("USER");
    private static final String PASSWORD = dotenv.get("PASSWORD");
    private static final String URL = "jdbc:mysql://" + HOST_CON_PUERTO + "/" + DATABASE_NAME;

    private static ConexionBD instance;

    private ConexionBD() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver JDBC cargado");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Punto único de acceso (SINGLETON)
    public static synchronized ConexionBD getInstance() {
        if (instance == null) {
            instance = new ConexionBD();
        }
        return instance;
    }
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

