package app.ui.components;

import javax.swing.*;
import java.awt.*;
import app.infrastructure.shared.constants.Colors;

public class InputField extends JPanel {
    private final JTextField textField;

    public InputField(String labelText) {
        setLayout(new BorderLayout());
        // El panel exterior tendrá el fondo y el borde
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));

        textField = new JTextField(15);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // El JTextField interno es transparente y no tiene borde
        textField.setOpaque(false);
        textField.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8)); // Padding interno

        add(textField, BorderLayout.CENTER);
    }

    public String getText() {
        return textField.getText().trim();
    }

    public void setText(String text) {
        textField.setText(text);
    }

    public void clear() {
        textField.setText("");
    }

    public JTextField getField() {
        return textField;
    }

    /**
     * Este método ya no es necesario, el estilo se aplica en el constructor.
     */
    public void setFlatLook() {}
}
