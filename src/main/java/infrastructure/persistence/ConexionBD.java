package infrastructure.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    // 1. Define los datos de tu conexión (¡Cámbialos por los tuyos!)
    private static final String URL = "jdbc:mysql://localhost:3306/chasquiya_db"; // Asegúrate que la BD exista
    private static final String USER = "root";
    private static final String PASSWORD = "tu_contraseña_secreta"; // Cambia esto

    // 2. Variable para guardar la única instancia (Patrón Singleton)
    private static ConexionBD instance;

    // 3. La conexión en sí
    private Connection connection;

    // 4. Constructor privado para evitar que se creen nuevas instancias
    private ConexionBD() {
        try {
            // Cargamos el driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Creamos la conexión
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conexión a la BD exitosa.");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver JDBC no encontrado.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos.");
            e.printStackTrace();
        }
    }

    // 5. Método público para obtener la instancia (Singleton)
    public static synchronized ConexionBD getInstance() {
        if (instance == null) {
            instance = new ConexionBD();
        }
        return instance;
    }

    // 6. Método para obtener la conexión y poder usarla
    public Connection getConnection() {
        return connection;
    }

    // (Opcional) Método para cerrar la conexión al final del programa
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
