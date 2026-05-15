package GUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import Servicios.ManadaServicio;

public class Opcion1 extends JPanel {

    // Zonas de hábitat de cada especie: nombre, x, y, radio, color
    private static final Object[][] HABITATS = {
        {"Foca",   530, 190, 150,  new Color(200, 230, 255, 120)},
        {"Alce",   350, 285, 100,  new Color(34,  139, 34,  120)},
        {"Halcon", 690, 380, 120,  new Color(169, 169, 169, 120)},
        {"Cebra",  365, 775, 100,  new Color(255, 255, 153, 120)},
        {"Ciervo", 500, 500, 100,  new Color(144, 238, 144, 120)},
        {"Leon",   800, 600, 150,  new Color(210, 180, 140, 120)},
        {"Tigre",  295, 500, 100,  new Color(255, 140, 0,   120)},
        {"Lobo",   520, 870, 100,  new Color(105, 105, 105, 120)},
        {"Paloma", 740, 800, 100,  new Color(255, 182, 193, 120)},
        {"Orca",   110, 890, 160,  new Color(0,   0,   139, 120)},
    };

    private Image mapaImg;
    private String especieHover = null;   // especie bajo el mouse
    private Point  posHover     = null;   // posición del mouse
    private List<Map<String, Object>> manadas = new ArrayList<>();
    private Map<String, Image> iconosEspecies = new HashMap<>();

    public Opcion1() {
        mapaImg = new ImageIcon("Imagenes/Mapa.jpeg").getImage();
        cargarManadas();
        cargarIconos();
        configurarMouse();
    }

    private void cargarManadas() {
        try {
            manadas = ManadaServicio.cargarManadas();
        } catch (Exception e) {
            manadas = new ArrayList<>();
        }
    }

    private void cargarIconos() {
    String ruta = "Imagenes/IconosTherania/";

    String[] especies = {
        "Foca", "Alce", "Halcon", "Cebra", "Ciervo",
        "Leon", "Tigre", "Lobo", "Paloma", "Orca"
    };

    for (String especie : especies) {
        String archivo = ruta + especie + ".png";
        Image img = new ImageIcon(archivo).getImage();
        iconosEspecies.put(especie, img);
    }
}

    private void configurarMouse() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // Convertir coordenadas del panel a coordenadas del mapa (1080x1080)
                double escalaX = 1080.0 / getWidth();
                double escalaY = 1080.0 / getHeight();
                int mx = (int)(e.getX() * escalaX);
                int my = (int)(e.getY() * escalaY);

                especieHover = null;
                posHover = e.getPoint();

                for (Object[] h : HABITATS) {
                    int hx = (int) h[1];
                    int hy = (int) h[2];
                    int r  = (int) h[3];
                    double dist = Math.sqrt(Math.pow(mx - hx, 2) + Math.pow(my - hy, 2));
                    if (dist <= r) {
                        especieHover = (String) h[0];
                        break;
                    }
                }
                repaint();
            }
        });

        // Clic: recargar datos de manadas del JSON
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarManadas();
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1 — Dibujar mapa de fondo
        g2.drawImage(mapaImg, 0, 0, getWidth(), getHeight(), this);

        // 2 — Dibujar zonas de hábitat
        double escalaX = (double) getWidth()  / 1080.0;
        double escalaY = (double) getHeight() / 1080.0;

        for (Object[] h : HABITATS) {
            String especie = (String) h[0];
            int hx = (int)(((int) h[1]) * escalaX);
            int hy = (int)(((int) h[2]) * escalaY);
            int r  = (int)(((int) h[3]) * Math.min(escalaX, escalaY));
            Color color = (Color) h[4];

            // Círculo de zona
            g2.setColor(color);
            g2.fillOval(hx - r, hy - r, r * 2, r * 2);

            // Borde más visible si el mouse está encima
            if (especie.equals(especieHover)) {
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(3));
            } else {
                g2.setColor(new Color(255, 255, 255, 80));
                g2.setStroke(new BasicStroke(1));
            }
            g2.drawOval(hx - r, hy - r, r * 2, r * 2);

            Image icono = iconosEspecies.get(especie);

            if (icono != null) {

                int tamaño = (int)(r * 1.2);

                g2.drawImage(
                    icono,
                    hx - tamaño / 2,
                    hy - tamaño / 2,
                    tamaño,
                    tamaño,
                    this);
                }

            // 3 — Tooltip con info de manadas al hacer hover
            if (especieHover != null && posHover != null) {
                dibujarTooltip(g2, especieHover, posHover);
            }
        }
    }

    private void dibujarTooltip(Graphics2D g2, String especie, Point pos) {
        // Recopilar manadas de esa especie
        List<String> lineas = new ArrayList<>();
        lineas.add("── " + especie.toUpperCase() + " ──");
        int totalMiembros = 0;

        for (Map<String, Object> m : manadas) {
            String esp = (String) m.get("especie");
            if (especie.equalsIgnoreCase(esp)) {
                int miembros = ((Number) m.get("totalMiembros")).intValue();
                int cupo     = ((Number) m.get("cupoMaximo")).intValue();
                totalMiembros += miembros;
                lineas.add(m.get("nombre") + ": " + miembros + "/" + cupo);
            }
        }
        lineas.add("Total: " + totalMiembros + " ciudadanos");

        // Fondo del tooltip
        int padding = 8;
        int lineH   = 18;
        int w = 220;
        int h = lineas.size() * lineH + padding * 2;
        int tx = Math.min(pos.x + 15, getWidth()  - w - 5);
        int ty = Math.min(pos.y + 15, getHeight() - h - 5);

        g2.setColor(new Color(20, 20, 20, 200));
        g2.fillRoundRect(tx, ty, w, h, 10, 10);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(tx, ty, w, h, 10, 10);

        // Texto
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        for (int i = 0; i < lineas.size(); i++) {
            if (i == 0) g2.setFont(new Font("Arial", Font.BOLD, 13));
            else        g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.setColor(Color.WHITE);
            g2.drawString(lineas.get(i), tx + padding, ty + padding + (i + 1) * lineH - 4);
        }
    }
}