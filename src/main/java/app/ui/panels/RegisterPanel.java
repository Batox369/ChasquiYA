package app.ui.panels;

import app.domain.model.Usuario;
import app.domain.repository.UsuarioRepository;
import app.infrastructure.persistence.MySQLUsuarioRepository;
import app.infrastructure.shared.constants.Colors;
import app.ui.components.ModernMessageDialog;
import app.ui.MainFrame;
import app.ui.components.ModernPasswordField;
import app.ui.components.ModernTextField;
import app.ui.components.PrimaryButton;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel {

    private MainFrame mainFrame;
    private ModernTextField txtUsuario;
    private ModernTextField txtNombreCompleto;
    private ModernPasswordField txtPass;
    private ModernPasswordField txtPassConfirm;
    private PrimaryButton btnRegister;

    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new GridBagLayout());
        setBackground(Colors.PRIMARY_DARK2);
        setPreferredSize(new Dimension(350, 640));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Crear Cuenta");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 32));
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
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblNombre.setForeground(Color.WHITE);
        add(lblNombre, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 15, 10, 15);
        gbc.gridwidth = 2;
        txtNombreCompleto = new ModernTextField(20);
        txtNombreCompleto.setPlaceholder("ej. Ana López");
        add(txtNombreCompleto, gbc);

        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 15, 5, 15);
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblUsuario.setForeground(Color.WHITE);
        add(lblUsuario, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 15, 10, 15);
        gbc.gridwidth = 2;
        txtUsuario = new ModernTextField(20);
        txtUsuario.setPlaceholder("ej. ana.lopez");
        add(txtUsuario, gbc);

        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 15, 5, 15);
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblPass.setForeground(Color.WHITE);
        add(lblPass, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 15, 10, 15);
        gbc.gridwidth = 2;
        txtPass = new ModernPasswordField(20);
        txtPass.setPlaceholder("Mínimo 6 caracteres");
        add(txtPass, gbc);

        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 15, 5, 15);
        JLabel lblConfirmPass = new JLabel("Confirmar Contraseña:");
        lblConfirmPass.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblConfirmPass.setForeground(Color.WHITE);
        add(lblConfirmPass, gbc);

        gbc.gridy = 8;
        gbc.insets = new Insets(0, 15, 10, 15);
        gbc.gridwidth = 2;
        txtPassConfirm = new ModernPasswordField(20);
        txtPassConfirm.setPlaceholder("Repita la contraseña");
        add(txtPassConfirm, gbc);

        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 15, 10, 15);
        btnRegister = new PrimaryButton("Registrarme");
        add(btnRegister, gbc);

        gbc.gridy = 10;
        gbc.insets = new Insets(0, 15, 10, 15);
        JButton btnGoToLogin = new JButton("Ya tengo cuenta, iniciar sesión");
        btnGoToLogin.setBorderPainted(false);
        btnGoToLogin.setFont(new Font("SansSerif", Font.PLAIN, 12));
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
            new ModernMessageDialog(mainFrame, "Campos Incompletos", "Debe llenar todos los campos.", ModernMessageDialog.MessageType.WARNING).showDialog();
            return;
        }
        if (!pass1.equals(pass2)) {
            new ModernMessageDialog(mainFrame, "Error de Contraseña", "Las contraseñas no coinciden.", ModernMessageDialog.MessageType.ERROR).showDialog();
            return;
        }
        if (pass1.length() < 6) {
            new ModernMessageDialog(mainFrame, "Contraseña Débil", "La contraseña debe tener al menos 6 caracteres.", ModernMessageDialog.MessageType.WARNING).showDialog();
            return;
        }

        String passwordHash = BCrypt.hashpw(pass1, BCrypt.gensalt(12));
        UsuarioRepository userRepo = new MySQLUsuarioRepository();
        if (userRepo.registrarCliente(username, passwordHash, nombreCompleto)) {
            new ModernMessageDialog(mainFrame, "Registro Exitoso", "¡Registro exitoso! Ahora puede iniciar sesión.", ModernMessageDialog.MessageType.SUCCESS).showDialog();
            mainFrame.navigateToLoginPanel();
        } else {
            new ModernMessageDialog(mainFrame, "Error de Registro", "Error al registrar. Es posible que el usuario ya exista.", ModernMessageDialog.MessageType.ERROR).showDialog();
        }
    }
}