package app.infrastructure.persistence;

import app.domain.model.Usuario;
import app.domain.repository.UsuarioRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLUsuarioRepository implements UsuarioRepository {

    private final Connection connection;

    public MySQLUsuarioRepository() {
        // Obtiene la conexión del Singleton
        this.connection = ConexionBD.getInstance().getConnection();
    }

    @Override
    public Usuario findByUsername(String username) {
        String sql = "SELECT * FROM usuarios WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Encontramos al usuario, creamos el objeto
                    return new Usuario(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("rol")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Manejo de errores
        }
        return null; // No se encontró el usuario
    }

    @Override
    public boolean registrarCliente(String username, String passwordHash, String nombreCompleto) {
        // Usamos una transacción para asegurar que se creen AMBAS tablas
        String sqlUsuario = "INSERT INTO usuarios (username, password_hash, rol) VALUES (?, ?, 'CLIENTE')";
        String sqlCliente = "INSERT INTO clientes (usuario_id, nombre_completo) VALUES (?, ?)";

        try {
            // 1. Desactivar Auto-commit para iniciar la transacción
            connection.setAutoCommit(false);

            int usuarioId = -1;

            // 2. Insertar en la tabla 'usuarios'
            try (PreparedStatement psUsuario = connection.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                psUsuario.setString(1, username);
                psUsuario.setString(2, passwordHash);
                psUsuario.executeUpdate();

                // Obtenemos el ID del usuario recién creado
                try (ResultSet generatedKeys = psUsuario.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        usuarioId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID del usuario creado.");
                    }
                }
            }

            // 3. Insertar en la tabla 'clientes' usando el ID obtenido
            try (PreparedStatement psCliente = connection.prepareStatement(sqlCliente)) {
                psCliente.setInt(1, usuarioId);
                psCliente.setString(2, nombreCompleto);
                psCliente.executeUpdate();
            }

            // 4. Si todo salió bien, confirma la transacción
            connection.commit();
            return true;

        } catch (SQLException e) {
            // 5. Si algo falló (ej. username duplicado), revierte todo
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            // 6. Volver a activar el Auto-commit
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}