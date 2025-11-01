package app.domain.repository;

import app.domain.model.Usuario;

public interface UsuarioRepository {
    Usuario findByUsername(String username);
    boolean registrarCliente(String username, String passwordHash, String nombreCompleto);
}
