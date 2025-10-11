package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class mapaPanel {
    private JPanel panel1;
    private JPanel mapa;
    private BufferedImage imagen;
    private double zoom = 1.0;
    private int offsetX = 0;
    private int offsetY = 0;
    private int lastX, lastY;

    public mapaPanel() {
        try {
            imagen = ImageIO.read(new File("src/main/resources/mapa.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapa = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imagen != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.translate(offsetX, offsetY);
                    g2d.scale(zoom, zoom);
                    g2d.drawImage(imagen, 0, 0, null);
                }
            }
        };

        mapa.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
            }
        });

        mapa.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastX;
                int dy = e.getY() - lastY;
                offsetX += dx;
                offsetY += dy;
                lastX = e.getX();
                lastY = e.getY();
                mapa.repaint();
            }
        });

        mapa.addMouseWheelListener(e -> {
            double prevZoom = zoom;
            int x = e.getX();
            int y = e.getY();

            // ajuste de zoom
            if (e.getPreciseWheelRotation() < 0)
                zoom *= 1.1;
            else
                zoom /= 1.1;

            // límites
            zoom = Math.max(0.2, Math.min(zoom, 5.0));

            // mantener el punto bajo el cursor fijo
            double scale = zoom / prevZoom;
            offsetX = (int) (x - scale * (x - offsetX));
            offsetY = (int) (y - scale * (y - offsetY));

            mapa.repaint();
        });

        panel1 = new JPanel(new BorderLayout());
        panel1.add(mapa, BorderLayout.CENTER);
    }

    public JPanel getRootPanel() {
        return panel1;
    }
}
