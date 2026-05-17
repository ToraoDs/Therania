package GUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import Servicios.CiudadanoServicio;
import Servicios.ManadaServicio;

public class Opcion1 extends JPanel {

    private static final Object[][] HABITATS = {
        {"Foca",   530, 190, 150, new Color(200, 230, 255, 120)},
        {"Alce",   350, 285, 100, new Color(34,  139, 34,  120)},
        {"Halcon", 690, 380, 120, new Color(169, 169, 169, 120)},
        {"Cebra",  365, 775, 100, new Color(255, 255, 153, 120)},
        {"Ciervo", 500, 500, 100, new Color(144, 238, 144, 120)},
        {"Leon",   800, 600, 150, new Color(210, 180, 140, 120)},
        {"Tigre",  295, 500, 100, new Color(255, 140, 0,   120)},
        {"Lobo",   520, 870, 100, new Color(105, 105, 105, 120)},
        {"Paloma", 740, 800, 100, new Color(255, 182, 193, 120)},
        {"Orca",   110, 890, 160, new Color(0,   0,   139, 120)},
    };

    private static final Set<String> ESPECIES_BASE = new HashSet<>(Arrays.asList(
        "Foca","Alce","Halcon","Cebra","Ciervo","Leon","Tigre","Lobo","Paloma","Orca"
    ));

    private Image mapaImg;
    private String especieHover = null;
    private Point  posHover     = null;
    private List<Map<String, Object>> manadas           = new ArrayList<>();
    private Map<String, Image>        iconosEspecies    = new HashMap<>();
    private List<Object[]>            habitatsPersonalizados = new ArrayList<>();
    private final Consumer<String>    onEspecieClic;
    private Consumer<String>          callbackHabitat   = null;

    // ─── Constructor ──────────────────────────────────────────────────────

    public Opcion1(Consumer<String> onEspecieClic) {
        this.onEspecieClic = onEspecieClic;
        mapaImg = new ImageIcon("Imagenes/Mapa.jpeg").getImage();
        cargarManadas();
        cargarIconos();
        cargarEspeciesPersonalizadas();
        configurarMouse();

        javax.swing.Timer timerRefresh = new javax.swing.Timer(3000, e -> {
            cargarManadas();
            cargarEspeciesPersonalizadas();
            repaint();
        });
        timerRefresh.start();

        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0) {
                if (!isShowing()) timerRefresh.stop();
                else              timerRefresh.start();
            }
        });
    }

    public Opcion1() { this(null); }

    // ─── Carga de datos ───────────────────────────────────────────────────

    private void cargarManadas() {
        try { manadas = ManadaServicio.cargarManadas(); }
        catch (Exception e) { manadas = new ArrayList<>(); }
    }

    private void cargarIconos() {
        String ruta = "Imagenes/IconosTherania/";
        for (String especie : ESPECIES_BASE)
            iconosEspecies.put(especie, new ImageIcon(ruta + especie + ".png").getImage());
    }

    private void cargarEspeciesPersonalizadas() {
        habitatsPersonalizados.clear();
        try {
            List<Map<String, Object>> ciudadanos = CiudadanoServicio.cargarCiudadanos();
            Set<String> vistas = new HashSet<>();
            for (Map<String, Object> c : ciudadanos) {
                String especie = (String) c.get("especie");
                String habitat = (String) c.get("habitatSimbolico");
                if (especie == null || ESPECIES_BASE.contains(especie)) continue;
                if (vistas.contains(especie) || habitat == null)        continue;
                vistas.add(especie);
                try {
                    int hx = 540, hy = 540, hr = 80;
                    for (String part : habitat.split(",")) {
                        String[] kv = part.trim().split(":");
                        if (kv.length == 2) {
                            int val = Integer.parseInt(kv[1].trim());
                            switch (kv[0].trim()) {
                                case "x" -> hx = val;
                                case "y" -> hy = val;
                                case "r" -> hr = val;
                            }
                        }
                    }
                    habitatsPersonalizados.add(new Object[]{
                        especie, hx, hy, hr, new Color(180, 130, 220, 120)
                    });
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
    }

    // ─── Modo selección de hábitat (para Opcion6) ─────────────────────────

    public void activarModoSeleccionHabitat(Consumer<String> callback) {
        this.callbackHabitat = callback;
    }

    // ─── Eventos de ratón ─────────────────────────────────────────────────

    private void configurarMouse() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                double escalaX = 1080.0 / getWidth();
                double escalaY = 1080.0 / getHeight();
                int mx = (int)(e.getX() * escalaX);
                int my = (int)(e.getY() * escalaY);

                especieHover = null;
                posHover = e.getPoint();

                for (Object[] h : HABITATS) {
                    int hx = (int) h[1], hy = (int) h[2], r = (int) h[3];
                    if (Math.sqrt(Math.pow(mx-hx,2)+Math.pow(my-hy,2)) <= r) {
                        especieHover = (String) h[0];
                        break;
                    }
                }
                if (especieHover == null) {
                    for (Object[] h : habitatsPersonalizados) {
                        int hx = (int) h[1], hy = (int) h[2], r = (int) h[3];
                        if (Math.sqrt(Math.pow(mx-hx,2)+Math.pow(my-hy,2)) <= r) {
                            especieHover = (String) h[0];
                            break;
                        }
                    }
                }
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarManadas();
                repaint();

                double escalaX = 1080.0 / getWidth();
                double escalaY = 1080.0 / getHeight();
                int mx = (int)(e.getX() * escalaX);
                int my = (int)(e.getY() * escalaY);

                // Modo selección de hábitat para Opcion6
                if (callbackHabitat != null) {
                    String coords = "x:" + mx + ",y:" + my + ",r:80";
                    callbackHabitat.accept(coords);
                    callbackHabitat = null;
                    return;
                }

                // Doble click → filtrar ciudadanos por especie en Opcion5
                if (e.getClickCount() == 2 && onEspecieClic != null) {
                    // Buscar en hábitats base
                    for (Object[] h : HABITATS) {
                        int hx = (int) h[1], hy = (int) h[2], r = (int) h[3];
                        if (Math.sqrt(Math.pow(mx-hx,2)+Math.pow(my-hy,2)) <= r) {
                            onEspecieClic.accept((String) h[0]);
                            return;
                        }
                    }
                    // Buscar en hábitats personalizados
                    for (Object[] h : habitatsPersonalizados) {
                        int hx = (int) h[1], hy = (int) h[2], r = (int) h[3];
                        if (Math.sqrt(Math.pow(mx-hx,2)+Math.pow(my-hy,2)) <= r) {
                            onEspecieClic.accept((String) h[0]);
                            return;
                        }
                    }
                }
            }
        });
    }

    // ─── Dibujo ───────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.drawImage(mapaImg, 0, 0, getWidth(), getHeight(), this);

        double escalaX = (double) getWidth()  / 1080.0;
        double escalaY = (double) getHeight() / 1080.0;

        // Hábitats base con íconos
        for (Object[] h : HABITATS) {
            dibujarHabitat(g2, h, escalaX, escalaY, true);
        }

        // Hábitats personalizados sin ícono
        for (Object[] h : habitatsPersonalizados) {
            dibujarHabitat(g2, h, escalaX, escalaY, false);
        }

        // Tooltip
        if (especieHover != null && posHover != null)
            dibujarTooltip(g2, especieHover, posHover);

        // Indicador modo selección
        if (callbackHabitat != null) {
            g2.setColor(new Color(255, 215, 0, 200));
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.drawString("🎯 Haz clic para definir el hábitat base de la especie",
                20, getHeight() - 20);
            g2.setStroke(new BasicStroke(4));
            g2.drawRect(2, 2, getWidth() - 4, getHeight() - 4);
        }
    }

    private void dibujarHabitat(Graphics2D g2, Object[] h,
                                 double escalaX, double escalaY, boolean conIcono) {
        String especie = (String) h[0];
        int hx = (int)(((int) h[1]) * escalaX);
        int hy = (int)(((int) h[2]) * escalaY);
        int r  = (int)(((int) h[3]) * Math.min(escalaX, escalaY));
        Color color = (Color) h[4];

        g2.setColor(color);
        g2.fillOval(hx - r, hy - r, r * 2, r * 2);

        boolean hover = especie.equals(especieHover);
        g2.setColor(hover ? Color.WHITE : new Color(255, 255, 255, 80));
        g2.setStroke(new BasicStroke(hover ? 3 : 1));
        g2.drawOval(hx - r, hy - r, r * 2, r * 2);

        if (conIcono) {
            Image icono = iconosEspecies.get(especie);
            if (icono != null) {
                int tam = (int)(r * 1.2);
                g2.drawImage(icono, hx - tam/2, hy - tam/2, tam, tam, this);
            }
        } else {
            // Especie personalizada: mostrar nombre como texto
            int fontSize = Math.max(9, (int)(r * 0.35));
            g2.setFont(new Font("Arial", Font.BOLD, fontSize));
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(especie);
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(hx - tw/2 - 4, hy - 8, tw + 8, 16, 6, 6);
            g2.setColor(Color.WHITE);
            g2.drawString(especie, hx - tw/2, hy + 4);
        }

        if (hover) {
            String hint = "↩ Doble click → ciudadanos";
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(hint);
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRoundRect(hx - tw/2 - 4, hy + r + 2, tw + 8, 16, 6, 6);
            g2.setColor(new Color(255, 255, 180));
            g2.drawString(hint, hx - tw/2, hy + r + 14);
        }
    }

    // ─── Tooltip ──────────────────────────────────────────────────────────

    private void dibujarTooltip(Graphics2D g2, String especie, Point pos) {
        List<String> lineas = new ArrayList<>();
        lineas.add("── " + especie.toUpperCase() + " ──");
        int totalMiembros = 0;
        for (Map<String, Object> m : manadas) {
            if (especie.equalsIgnoreCase((String) m.get("especie"))) {
                int miembros = ((Number) m.get("totalMiembros")).intValue();
                int cupo     = ((Number) m.get("cupoMaximo")).intValue();
                totalMiembros += miembros;
                lineas.add(m.get("nombre") + ": " + miembros + "/" + cupo);
            }
        }
        lineas.add("Total: " + totalMiembros + " ciudadanos");
        lineas.add("↩ Doble click para ver ciudadanos");

        int padding = 8, lineH = 18, w = 240;
        int h = lineas.size() * lineH + padding * 2;
        int tx = Math.min(pos.x + 15, getWidth()  - w - 5);
        int ty = Math.min(pos.y + 15, getHeight() - h - 5);

        g2.setColor(new Color(20, 20, 20, 210));
        g2.fillRoundRect(tx, ty, w, h, 10, 10);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(tx, ty, w, h, 10, 10);

        for (int i = 0; i < lineas.size(); i++) {
            g2.setFont(i == 0 ? new Font("Arial", Font.BOLD, 13)
                : i == lineas.size()-1 ? new Font("Arial", Font.ITALIC, 11)
                : new Font("Arial", Font.PLAIN, 12));
            g2.setColor(i == lineas.size()-1 ? new Color(180, 220, 255) : Color.WHITE);
            g2.drawString(lineas.get(i), tx + padding,
                ty + padding + (i + 1) * lineH - 4);
        }
    }
}