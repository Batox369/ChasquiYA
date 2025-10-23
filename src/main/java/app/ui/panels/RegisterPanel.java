package app.ui.panels;

import app.domain.repository.UsuarioRepository;
import app.infrastructure.persistence.MySQLUsuarioRepository;
import app.infrastructure.shared.constants.Colors;
import app.ui.MainFrame;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel {

    private MainFrame mainFrame;
    private JTextField txtUsuario;
    private JTextField txtNombreCompleto;
    private JPasswordField txtPass;
    private JPasswordField txtPassConfirm;

    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new GridBagLayout());
        setBackground(Colors.PRIMARY);
        setPreferredSize(new Dimension(250, 640));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Crear Cuenta");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(lblTitulo, gbc);

        // (Campos para Nombre, Usuario, Contraseña, etc.)
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 15, 5, 15);

        gbc.gridy = 1;
        add(new JLabel("Nombre Completo:"), gbc);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 15, 10, 15);
        txtNombreCompleto = new JTextField(20);
        add(txtNombreCompleto, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(10, 15, 5, 15);
        add(new JLabel("Usuario (email):"), gbc);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 15, 10, 15);
        txtUsuario = new JTextField(20);
        add(txtUsuario, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(10, 15, 5, 15);
        add(new JLabel("Contraseña:"), gbc);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 15, 10, 15);
        txtPass = new JPasswordField(20);
        add(txtPass, gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(10, 15, 5, 15);
        add(new JLabel("Confirmar Contraseña:"), gbc);
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 15, 10, 15);
        txtPassConfirm = new JPasswordField(20);
        add(txtPassConfirm, gbc);

        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 15, 10, 15);
        JButton btnRegister = new JButton("Registrarme");
        add(btnRegister, gbc);

        gbc.gridy = 10;
        gbc.insets = new Insets(0, 15, 10, 15);
        JButton btnGoToLogin = new JButton("Ya tengo cuenta, iniciar sesión");
        btnGoToLogin.setBorderPainted(false);
        btnGoToLogin.setContentAreaFilled(false);
        btnGoToLogin.setForeground(Color.WHITE);
        btnGoToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(btnGoToLogin, gbc);

        // --- ACCIONES ---
        btnRegister.addActionListener(e -> onRegisterButtonClick());
        btnGoToLogin.addActionListener(e -> {
            mainFrame.navigateToLoginPanel(); // Llama al MainFrame
        });
    }

    private void onRegisterButtonClick() {
        String nombreCompleto = txtNombreCompleto.getText();
        String username = txtUsuario.getText();
        String pass1 = new String(txtPass.getPassword());
        String pass2 = new String(txtPassConfirm.getPassword());

        if (username.isEmpty() || pass1.isEmpty() || nombreCompleto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe llenar todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!pass1.equals(pass2)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String passwordHash = BCrypt.hashpw(pass1, BCrypt.gensalt(12));
        UsuarioRepository userRepo = new MySQLUsuarioRepository();
        boolean exito = userRepo.registrarCliente(username, passwordHash, nombreCompleto);

        if (exito) {
            JOptionPane.showMessageDialog(this, "¡Registro exitoso! Ahora puede iniciar sesión.");
            mainFrame.navigateToLoginPanel();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar. Es posible que el usuario ya exista.", "Error de Registro", JOptionPane.ERROR_MESSAGE);
        }
    }
}