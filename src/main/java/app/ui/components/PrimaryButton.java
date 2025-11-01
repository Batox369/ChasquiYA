package app.ui.components;

import javax.swing.*;
import java.awt.*;
import app.infrastructure.shared.constants.Colors;

public class PrimaryButton extends JButton {

    public PrimaryButton(String text) {
        super(text);
        setFont(new Font("Segoe UI", Font.PLAIN, 16));
        setBackground(Colors.PRIMARY);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        addChangeListener(e -> {
            if (getModel().isRollover()) {
                setBackground(Colors.HOVER);
            } else {
                setBackground(Colors.PRIMARY);
            }
        });
    }
}
