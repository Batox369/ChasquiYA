package Components;

import javax.swing.*;
import java.awt.*;
import Constants.Colors;

public class InputField extends JPanel {
    private JLabel label;
    private JTextField textField;

    public InputField(String labelText) {
        setLayout(new BorderLayout(5, 5));
        setOpaque(false);

        label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(Colors.TEXT_PRIMARY);

        textField = new JTextField(15);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createLineBorder(Colors.BORDER));

        add(label, BorderLayout.NORTH);
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
}
