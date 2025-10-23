package app.ui.panels;

// ... (Todos tus imports: Usuario, UsuarioRepository, BCrypt, SessionManager, Colors, MainFrame...)
import app.domain.model.Usuario;
import app.domain.repository.UsuarioRepository;
import app.infrastructure.persistence.MySQLUsuarioRepository;
import app.infrastructure.shared.SessionManager;
import app.infrastructure.shared.constants.Colors;
import app.ui.MainFrame;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    // ... (tus variables: mainFrame, txtUsuario, txtPass, chckbxRecordarme, btnLogin)
    private MainFrame mainFrame;
    private JTextField txtUsuario;
    private JPasswordField txtPass;
    private JCheckBox chckbxRecordarme;
    private JButton btnLogin;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        // ... (tu código de layout: setBackground, setPreferredSize, GridBagLayout, etc.)
        setLayout(new GridBagLayout());
        setBackground(Colors.PRIMARY);
        setPreferredSize(new Dimension(250, 640));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ... (Tu código para añadir Título, Usuario, Contraseña, CheckBox y Botón Login)
        JLabel lblTitulo = new JLabel("Bienvenido");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(lblTitulo, gbc);

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setForeground(Color.WHITE);
        gbc.gridy = 1; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(30, 15, 5, 15);
        add(lblUsuario, gbc);

        gbc.gridy = 2; gbc.gridwidth = 2; gbc.insets = new Insets(0, 15, 10, 15);
        txtUsuario = new JTextField(20);
        add(txtUsuario, gbc);

        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setForeground(Color.WHITE);
        gbc.gridy = 3; gbc.gridwidth = 1; gbc.insets = new Insets(10, 15, 5, 15);
        add(lblPass, gbc);

        gbc.gridy = 4; gbc.gridwidth = 2; gbc.insets = new Insets(0, 15, 10, 15);
        txtPass = new JPasswordField(20);
        add(txtPass, gbc);

        gbc.gridy = 6; gbc.insets = new Insets(10, 15, 10, 15);
        btnLogin = new JButton("Ingresar");
        add(btnLogin, gbc);


        // --- AÑADIR ESTE BOTÓN ---
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 15, 10, 15);
        JButton btnGoToRegister = new JButton("No tengo cuenta, registrarme");
        btnGoToRegister.setBorderPainted(false);
        btnGoToRegister.setContentAreaFilled(false);
        btnGoToRegister.setForeground(Color.WHITE);
        btnGoToRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(btnGoToRegister, gbc);

        // --- ACCIONES ---
        btnLogin.addActionListener(e -> onLoginButtonClick());

        // --- AÑADIR ESTA ACCIÓN ---
        btnGoToRegister.addActionListener(e -> {
            mainFrame.navigateToRegisterPanel(); // Llama al MainFrame
        });
    }

    private void onLoginButtonClick() {
        // ... (Tu lógica de login sin cambios)
        String username = txtUsuario.getText();
        String password = new String(txtPass.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe llenar ambos campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UsuarioRepository userRepo = new MySQLUsuarioRepository();
        Usuario usuario = userRepo.findByUsername(username);

        if (usuario != null && BCrypt.checkpw(password, usuario.getPasswordHash())) {
            SessionManager.setCurrentUser(usuario);
            SessionManager.saveSession(usuario.getUsername());
            mainFrame.onLoginSuccess(usuario);
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Error de Acceso", JOptionPane.ERROR_MESSAGE);
        }
    }
}