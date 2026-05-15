package GUI;

import Servicios.ManadaServicio;
import Simulacion.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Opcion2 extends JPanel {

    private Image mapaImagen;
    private List<PuntoManada> puntosManadas = new ArrayList<>();
    private PuntoManada manadaSeleccionada = null;
    private JLabel infoLabel;
    private final Reloj reloj; 


    // Clase interna para representar una manada en el mapa
    private static class PuntoManada {
        String nombre;
        String especie;
        int miembros;
        int x, y, r;
        boolean esPredadora;

        PuntoManada(String nombre, String especie, int miembros,
                    int x, int y, int r, boolean esPredadora) {
            this.nombre     = nombre;
            this.especie    = especie;
            this.miembros   = miembros;
            this.x          = x;
            this.y          = y;
            this.r          = r;
            this.esPredadora = esPredadora;
        }
    }

    public Opcion2(Reloj reloj) {
        this.reloj = reloj;
        setLayout(new BorderLayout());

        // Cargar imagen del mapa
        ImageIcon icono = new ImageIcon("Imagenes/Mapa.jpeg");
        mapaImagen = icono.getImage();

        // Panel del mapa con dibujo personalizado
        JPanel panelMapa = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarMapa(g);
            }
        };
        panelMapa.setPreferredSize(new Dimension(800, 800));
        panelMapa.setBackground(Color.BLACK);

        // Detectar clic sobre manada
        panelMapa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                detectarClicManada(e.getX(), e.getY());
                panelMapa.repaint();
            }
        });

        // Detectar hover sobre manada
        panelMapa.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                detectarHoverManada(e.getX(), e.getY(), panelMapa);
            }
        });

        // Panel de info en la parte inferior
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBackground(new Color(30, 30, 30));
        panelInfo.setPreferredSize(new Dimension(0, 80));

        infoLabel = new JLabel("Haz clic sobre una manada para ver su información",
                               SwingConstants.CENTER);
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panelInfo.add(infoLabel, BorderLayout.CENTER);

        // Botón refrescar
        JButton btnRefrescar = new JButton("↺ Actualizar manadas");
        btnRefrescar.setBackground(new Color(60, 60, 60));
        btnRefrescar.setForeground(Color.WHITE);
        btnRefrescar.addActionListener(e -> {
            cargarManadas();
            panelMapa.repaint();
            infoLabel.setText("Manadas actualizadas desde JSON");
        });
        panelInfo.add(btnRefrescar, BorderLayout.EAST);

        // Leyenda
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leyenda.setBackground(new Color(30, 30, 30));
        JLabel predLabel = new JLabel("  ● Predadora");
        predLabel.setForeground(new Color(220, 80, 80));
        JLabel noPreLabel = new JLabel("  ● No predadora");
        noPreLabel.setForeground(new Color(80, 180, 80));
        leyenda.add(predLabel);
        leyenda.add(noPreLabel);
        panelInfo.add(leyenda, BorderLayout.WEST);

        add(panelMapa, BorderLayout.CENTER);
        add(panelInfo, BorderLayout.SOUTH);

        // Cargar manadas al iniciar
        cargarManadas();
    }

    // Cargar manadas desde JSON y parsear coordenadas del territorio
    private void cargarManadas() {
        puntosManadas.clear();
        List<Map<String, Object>> manadas = ManadaServicio.cargarManadas();

        for (Map<String, Object> m : manadas) {
            String territorio = (String) m.get("territorio");
            String nombre     = (String) m.get("nombre");
            String especie    = (String) m.get("especie");
            int miembros      = ((Number) m.get("totalMiembros")).intValue();
            boolean enTransito = Boolean.TRUE.equals(m.get("enTransito"));

            if (enTransito || territorio == null) continue;

            // Parsear "x:530,y:165,r:70"
            try {
                String[] partes = territorio.split(",");
                int x = Integer.parseInt(partes[0].split(":")[1].trim());
                int y = Integer.parseInt(partes[1].split(":")[1].trim());
                int r = Integer.parseInt(partes[2].split(":")[1].trim());

                boolean predadora = esPredadora(especie);
                puntosManadas.add(new PuntoManada(nombre, especie,
                                                   miembros, x, y, r, predadora));
            } catch (Exception ignored) { }
        }
    }

    private boolean esPredadora(String especie) {
        return especie.equals("Lobo") || especie.equals("Leon") ||
               especie.equals("Tigre") || especie.equals("Halcon") ||
               especie.equals("Orca");
    }

    // Dibujar mapa y puntos de manadas
    private void dibujarMapa(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        // Escalar mapa al tamaño del panel
        int w = getWidth();
        int h = getHeight() - 80;
        if (mapaImagen != null) {
            g2.drawImage(mapaImagen, 0, 0, w, h, null);
        }

        if (puntosManadas.isEmpty()) return;

        // Factores de escala (imagen original 1080x1080)
        double scaleX = (double) w / 1080;
        double scaleY = (double) h / 1080;

        for (PuntoManada p : puntosManadas) {
            int px = (int)(p.x * scaleX);
            int py = (int)(p.y * scaleY);
            int pr = (int)(p.r * Math.min(scaleX, scaleY));

            // Color según tipo
            Color color = p.esPredadora
                ? new Color(220, 80, 80, 120)
                : new Color(80, 180, 80, 120);
            Color borde = p.esPredadora
                ? new Color(220, 80, 80)
                : new Color(80, 180, 80);

            // Si está seleccionada, resaltar
            if (p == manadaSeleccionada) {
                g2.setColor(new Color(255, 215, 0, 150));
                g2.fillOval(px - pr - 5, py - pr - 5, (pr + 5) * 2, (pr + 5) * 2);
            }

            // Círculo del territorio
            g2.setColor(color);
            g2.fillOval(px - pr, py - pr, pr * 2, pr * 2);
            g2.setColor(borde);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(px - pr, py - pr, pr * 2, pr * 2);

            // Nombre de la manada
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 10));
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(p.nombre);
            g2.drawString(p.nombre, px - tw / 2, py - 5);

            // Número de miembros
            g2.setFont(new Font("Arial", Font.PLAIN, 9));
            String info = p.especie + " · " + p.miembros;
            int iw = g2.getFontMetrics().stringWidth(info);
            g2.drawString(info, px - iw / 2, py + 10);
        }
    }

    // Detectar clic y mostrar info en el label
    private void detectarClicManada(int mx, int my) {
        int w = getWidth();
        int h = getHeight() - 80;
        double scaleX = (double) w / 1080;
        double scaleY = (double) h / 1080;

        manadaSeleccionada = null;
        for (PuntoManada p : puntosManadas) {
            int px = (int)(p.x * scaleX);
            int py = (int)(p.y * scaleY);
            int pr = (int)(p.r * Math.min(scaleX, scaleY));
            double dist = Math.sqrt(Math.pow(mx - px, 2) + Math.pow(my - py, 2));
            if (dist <= pr) {
                manadaSeleccionada = p;
                infoLabel.setText("▶ " + p.nombre + "  |  Especie: " + p.especie
                    + "  |  Miembros: " + p.miembros
                    + "  |  Tipo: " + (p.esPredadora ? "Predadora 🔴" : "No predadora 🟢"));
                return;
            }
        }
        infoLabel.setText("Haz clic sobre una manada para ver su información");
    }

    // Cambiar cursor al pasar sobre una manada
    private void detectarHoverManada(int mx, int my, JPanel panel) {
        int w = getWidth();
        int h = getHeight() - 80;
        double scaleX = (double) w / 1080;
        double scaleY = (double) h / 1080;

        for (PuntoManada p : puntosManadas) {
            int px = (int)(p.x * scaleX);
            int py = (int)(p.y * scaleY);
            int pr = (int)(p.r * Math.min(scaleX, scaleY));
            double dist = Math.sqrt(Math.pow(mx - px, 2) + Math.pow(my - py, 2));
            if (dist <= pr) {
                panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return;
            }
        }
        panel.setCursor(Cursor.getDefaultCursor());
    }
}