package Components;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import Constants.Colors;
import Models.Sistema;

public class ZoneSelector extends JPanel {
    private JLabel label;
    private JComboBox<String> combo;

    public ZoneSelector(String labelText) {
        setLayout(new BorderLayout(5, 5));
        setOpaque(false);

        label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(Colors.TEXT_PRIMARY);

        combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createLineBorder(Colors.BORDER));

        add(label, BorderLayout.NORTH);
        add(combo, BorderLayout.CENTER);
    }

    public void recargarZonas() {
        combo.removeAllItems();
        List<String> zonas = Sistema.getInstancia().getGestorZonas().obtenerNombresZonas();
        for (String z : zonas) combo.addItem(z);
    }

    public String getZonaSeleccionada() {
        return (String) combo.getSelectedItem();
    }

    public JComboBox<String> getComboBox() {
        return combo;
    }

    public void setZonas(List<String> zonas) {
        combo.removeAllItems();
        for (String z : zonas) combo.addItem(z);
    }
    public void setSelectedIndex(int index) {
        combo.setSelectedIndex(index);
    }

}
