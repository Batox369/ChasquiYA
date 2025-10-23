package ui.panels;

import ui.components.StatCard;
import infrastructure.shared.constants.Colors;
import javax.swing.*;
import java.awt.*;

public class PerfilPanel extends JPanel {

    public PerfilPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        statsGrid.setOpaque(false);

        statsGrid.add(new StatCard("Total Viajes", "45", "📊", Colors.PRIMARY));
        statsGrid.add(new StatCard("En Proceso", "3", "🚗", Colors.ACCENT));
        statsGrid.add(new StatCard("Completados", "42", "✓", Colors.SUCCESS));
        statsGrid.add(new StatCard("Cancelados", "2", "✗", Colors.ERROR));

        add(statsGrid, BorderLayout.CENTER);
    }
}