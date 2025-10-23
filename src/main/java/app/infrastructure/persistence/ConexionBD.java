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
