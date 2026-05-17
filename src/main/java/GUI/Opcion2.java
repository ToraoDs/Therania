package GUI;

import Especies.AfiliacionManada;
import Especies.CiudadanoTherian;
import Especies.Encuentro;
import Servicios.CiudadanoServicio;
import Servicios.ManadaServicio;
import Simulacion.Reloj;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;  
import javax.swing.ImageIcon;        // ← explícito para evitar ambigüedad con java.util.Timer
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;;

public class Opcion2 extends JPanel {

    private Image mapaImagen;
    private final Reloj reloj;
    private JLabel infoLabel;
    private JTextArea areaLog;
    private Timer animTimer;

    private static final Random random = new Random();

    // ─── Límite de la isla (elipse) ───────────────────────────────────────
    private static final double ISL_CX = 530, ISL_CY = 535;
    private static final double ISL_RX = 420, ISL_RY = 450;

    private static boolean enIsla(double x, double y) {
        return Math.pow((x - ISL_CX) / ISL_RX, 2)
             + Math.pow((y - ISL_CY) / ISL_RY, 2) <= 1.0;
    }

    // Especies terrestres que NO pueden salir de la isla
    private static final Set<String> TERRESTRE = new HashSet<>(Arrays.asList(
        "Alce","Tigre","Ciervo","Cebra","Leon","Lobo"
    ));

    // ─── Zonas de movimiento por especie {cx, cy, radio} ──────────────────
    private static final Map<String, int[][]> ZONAS = new HashMap<>();
    static {
        ZONAS.put("Halcon", null);
        ZONAS.put("Paloma", null);
        ZONAS.put("Orca", new int[][]{
            {  60, 450, 210}, { 130, 860, 220},
            { 490,1020, 190}, { 990, 580, 220},
            { 920, 200, 200}, { 150, 170, 190}
        });
        ZONAS.put("Foca", new int[][]{
            { 530, 185, 260}, { 420,  90, 160}, { 650,  95, 160}
        });
        ZONAS.put("Alce", new int[][]{
            { 350, 290, 230}, { 270, 420, 190}, { 440, 370, 170}
        });
        ZONAS.put("Tigre", new int[][]{
            { 295, 500, 230}, { 340, 630, 190}, { 210, 390, 170}
        });
        ZONAS.put("Ciervo", new int[][]{
            { 505, 505, 270}, { 420, 390, 190}, { 600, 610, 190}
        });
        ZONAS.put("Cebra", new int[][]{
            { 365, 775, 230}, { 490, 700, 190}, { 255, 690, 170}
        });
        ZONAS.put("Leon", new int[][]{
            { 800, 625, 240}, { 690, 780, 190}, { 700, 540, 170}
        });
        ZONAS.put("Lobo", new int[][]{
            { 520, 870, 230}, { 390, 730, 190}, { 650, 780, 190}
        });
    }

    // ─── Manada animada ───────────────────────────────────────────────────
    private static class PuntoManada {
        String  nombre, especie;
        int     miembros;
        boolean esPredadora;

        double posX, posY;
        double baseX, baseY;
        double radioZona;
        double vx, vy;
        final double velocidad = 0.5;

        PuntoManada(String nombre, String especie, int miembros,
                    double x, double y, double radioZona, boolean esPredadora) {
            this.nombre      = nombre;
            this.especie     = especie;
            this.miembros    = miembros;
            this.esPredadora = esPredadora;
            this.baseX = x;  this.baseY = y;
            this.posX  = x;  this.posY  = y;
            this.radioZona = radioZona;
            double ang = random.nextDouble() * 2 * Math.PI;
            this.vx = Math.cos(ang) * velocidad;
            this.vy = Math.sin(ang) * velocidad;
        }

        void mover() {
            posX += vx;
            posY += vy;

            double dist = Math.sqrt(Math.pow(posX - baseX, 2) + Math.pow(posY - baseY, 2));

            // Fuerza de retorno suave al acercarse al límite de zona
            if (dist > radioZona * 0.7) {
                vx += (baseX - posX) * 0.04;
                vy += (baseY - posY) * 0.04;
            }

            // Límite duro de zona propia
            if (dist > radioZona) {
                posX = baseX + (posX - baseX) * (radioZona / dist);
                posY = baseY + (posY - baseY) * (radioZona / dist);
                vx = (baseX - posX) * 0.05 + (random.nextDouble() - 0.5) * 0.3;
                vy = (baseY - posY) * 0.05 + (random.nextDouble() - 0.5) * 0.3;
            }

            // Cambio de dirección aleatorio
            if (random.nextInt(80 + random.nextInt(120)) == 0) {
                double ang = random.nextDouble() * 2 * Math.PI;
                vx = Math.cos(ang) * velocidad;
                vy = Math.sin(ang) * velocidad;
            }

            // Limitar velocidad máxima
            double speed = Math.sqrt(vx * vx + vy * vy);
            if (speed > velocidad * 2) {
                vx = vx / speed * velocidad;
                vy = vy / speed * velocidad;
            }
        }

        void retroceder() {
            vx = (baseX - posX) * 0.1;
            vy = (baseY - posY) * 0.1;
        }
    }

    private List<PuntoManada> puntosManadas      = new ArrayList<>();
    private List<String>      logEnfrentamientos = new ArrayList<>();
    private PuntoManada       seleccionada       = null;
    private JPanel            panelMapa;

    // ─── Constructor ──────────────────────────────────────────────────────
    public Opcion2(Reloj reloj) {
        this.reloj = reloj;
        setLayout(new BorderLayout(5, 5));
        setBackground(new Color(20, 20, 30));

        construirPanelMapa();
        construirPanelDerecho();
        construirPanelSur();
        cargarManadas();

        animTimer = new Timer(100, e -> {
            moverManadas();
            detectarEnfrentamientos();
            panelMapa.repaint();
        });
        animTimer.start();
    }

    // ─── Construcción de paneles ──────────────────────────────────────────

    private void construirPanelMapa() {
        panelMapa = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujar(g);
            }
        };
        panelMapa.setBackground(Color.BLACK);
        panelMapa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                detectarClic(e.getX(), e.getY());
                panelMapa.repaint();
            }
        });
        add(panelMapa, BorderLayout.CENTER);
    }

    private void construirPanelDerecho() {
        areaLog = new JTextArea(10, 26);
        areaLog.setEditable(false);
        areaLog.setBackground(new Color(20, 20, 30));
        areaLog.setForeground(Color.WHITE);
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scroll = new JScrollPane(areaLog);
        scroll.setPreferredSize(new Dimension(270, 0));
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Enfrentamientos", 0, 0, null, Color.WHITE));
        add(scroll, BorderLayout.EAST);
    }

    private void construirPanelSur() {
        JPanel sur = new JPanel(new BorderLayout());
        sur.setBackground(new Color(30, 30, 40));
        sur.setPreferredSize(new Dimension(0, 65));

        infoLabel = new JLabel("Haz clic sobre una manada para ver información",
            SwingConstants.CENTER);
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leyenda.setBackground(new Color(30, 30, 40));
        JLabel lPred   = new JLabel("  ● Predadora");
        JLabel lNoPred = new JLabel("  ● No predadora");
        lPred.setForeground(new Color(220, 80, 80));
        lNoPred.setForeground(new Color(80, 200, 80));
        leyenda.add(lPred);
        leyenda.add(lNoPred);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.setBackground(new Color(30, 30, 40));

        JButton btnActualizar = new JButton("↺ Actualizar");
        btnActualizar.addActionListener(e -> { cargarManadas(); panelMapa.repaint(); });

        JButton btnPausar = new JButton("⏸ Pausar");
        btnPausar.addActionListener(e -> {
            if (animTimer.isRunning()) { animTimer.stop(); btnPausar.setText("▶ Reanudar"); }
            else                       { animTimer.start(); btnPausar.setText("⏸ Pausar"); }
        });

        botones.add(btnActualizar);
        botones.add(btnPausar);

        sur.add(infoLabel, BorderLayout.CENTER);
        sur.add(leyenda,   BorderLayout.WEST);
        sur.add(botones,   BorderLayout.EAST);
        add(sur, BorderLayout.SOUTH);
    }

    // ─── Cargar manadas con posiciones iniciales dentro de las ZONAS ──────

    private void cargarManadas() {
        puntosManadas.clear();
        List<Map<String, Object>> manadas = ManadaServicio.cargarManadas();
        Map<String, Integer> contadorEspecie = new HashMap<>();

        for (Map<String, Object> m : manadas) {
            String  nombre     = (String)  m.get("nombre");
            String  especie    = (String)  m.get("especie");
            boolean enTransito = Boolean.TRUE.equals(m.get("enTransito"));
            if (enTransito || especie == null || especie.equals("Mixta")) continue;

            int miembros = ((Number) m.get("totalMiembros")).intValue();
            int idx      = contadorEspecie.getOrDefault(especie, 0);
            contadorEspecie.put(especie, idx + 1);

            int[][] zonas = ZONAS.get(especie);
            double startX, startY, radioZona;

            if (zonas != null && zonas.length > 0) {
                int zonaIdx  = idx % zonas.length;
                double oMax  = zonas[zonaIdx][2] * 0.3;
                startX   = zonas[zonaIdx][0] + (random.nextDouble() - 0.5) * oMax;
                startY   = zonas[zonaIdx][1] + (random.nextDouble() - 0.5) * oMax;
                radioZona = zonas[zonaIdx][2] * 0.55;
            } else {
                startX   = 320 + idx * 180 + (random.nextDouble() - 0.5) * 60;
                startY   = 330 + (random.nextDouble() - 0.5) * 80;
                radioZona = 120;
            }

            puntosManadas.add(new PuntoManada(
                nombre, especie, miembros, startX, startY,
                radioZona, esPredadora(especie)));
        }
    }

    // ─── Movimiento con validación de límites ─────────────────────────────

    private void moverManadas() {
        for (PuntoManada p : puntosManadas) {
            p.mover();
            if (!movimientoValido(p.especie, p.posX, p.posY)) {
                p.retroceder();
            }
        }
    }

    /**
     * Valida si una posición es válida para la especie dada:
     * - Terrestres: dentro de la elipse de la isla
     * - Orca:       fuera de la elipse (agua)
     * - Foca:       tierra ártica (isla, y < 290) O agua norteña (fuera isla, y < 270)
     * - Voladores:  libres en cualquier punto del mapa
     * - Zona propia: dentro del radio de alguna de sus zonas
     */
    private boolean movimientoValido(String especie, double x, double y) {
        if (x < 10 || x > 1070 || y < 10 || y > 1070) return false;

        // Restricción terrestre/marina por la elipse de la isla
        if (TERRESTRE.contains(especie) && !enIsla(x, y)) return false;

        if (especie.equals("Orca") && enIsla(x, y)) return false;

        if (especie.equals("Foca")) {
            boolean arcticoTierra = enIsla(x, y) && y < 290;
            boolean aguaNorte     = !enIsla(x, y) && y < 270;
            if (!arcticoTierra && !aguaNorte) return false;
        }

        // Voladores: sin restricción adicional
        int[][] zonas = ZONAS.get(especie);
        if (zonas == null) return true;

        // Verificar que esté dentro de alguna zona asignada
        for (int[] z : zonas) {
            double dist = Math.sqrt(Math.pow(x - z[0], 2) + Math.pow(y - z[1], 2));
            if (dist <= z[2]) return true;
        }
        return false;
    }

    // ─── Detección de enfrentamientos ─────────────────────────────────────

    private void detectarEnfrentamientos() {
        int w = panelMapa.getWidth();
        int h = panelMapa.getHeight();
        if (w == 0 || h == 0) return;
        double sx = (double) w / 1080;
        double sy = (double) h / 1080;

        for (int i = 0; i < puntosManadas.size(); i++) {
            PuntoManada a = puntosManadas.get(i);
            for (int j = i + 1; j < puntosManadas.size(); j++) {
                PuntoManada b = puntosManadas.get(j);
                if (a.esPredadora == b.esPredadora) continue;

                double dx   = (a.posX - b.posX) * sx;
                double dy   = (a.posY - b.posY) * sy;
                double dist = Math.sqrt(dx*dx + dy*dy);

                if (dist < 38) {
                    PuntoManada pred  = a.esPredadora ? a : b;
                    PuntoManada presa = a.esPredadora ? b : a;
                    procesarEnfrentamiento(pred, presa);
                }
            }
        }
    }

    private void procesarEnfrentamiento(PuntoManada pred, PuntoManada presa) {
        double factorPred  = 0.55 + (pred.miembros  / 20.0) * 0.2;
        double factorPresa = 0.45 + (presa.miembros / 20.0) * 0.25;
        double total       = factorPred + factorPresa;
        double probCaza    = Math.min(90, (factorPred  / total) * 100);
        double probEscape  = Math.min(90, (factorPresa / total) * 100);
        boolean predGano   = random.nextDouble() * 100 < probCaza;

        if (reloj != null && reloj.getCiudadanos() != null)
            actualizarCiudadanosReales(pred, presa, predGano);

        String resultado = predGano
            ? "🔴 " + pred.nombre  + " cazó a "     + presa.nombre
            : "🟢 " + presa.nombre + " escapó de "  + pred.nombre;

        String entrada = String.format(
            "[%s vs %s]\nCaza: %.0f%% | Escape: %.0f%%\n→ %s\n",
            pred.especie, presa.especie, probCaza, probEscape, resultado);

        if (logEnfrentamientos.isEmpty() || !logEnfrentamientos
                .get(logEnfrentamientos.size() - 1).contains(pred.nombre)) {
            logEnfrentamientos.add(entrada);
            if (logEnfrentamientos.size() > 25) logEnfrentamientos.remove(0);
            actualizarLog();
        }
        presa.retroceder();
    }

    private void actualizarCiudadanosReales(PuntoManada pred, PuntoManada presa,
                                             boolean predGano) {
        List<CiudadanoTherian> ciudadanos = reloj.getCiudadanos();
        CiudadanoTherian cPred  = buscarCiudadanoPorManada(ciudadanos, pred.nombre);
        CiudadanoTherian cPresa = buscarCiudadanoPorManada(ciudadanos, presa.nombre);
        if (cPred == null || cPresa == null) return;

        Encuentro.registrarEncuentro(cPred, cPresa, predGano);
        if (predGano) {
            cPred.setIAA(Math.min(100, cPred.getIAA()  + 1.5));
            cPresa.setIAA(Math.max(0,  cPresa.getIAA() - 1.0));
        } else {
            cPresa.setIAA(Math.min(100, cPresa.getIAA() + 1.0));
            cPred.setIAA(Math.max(0,   cPred.getIAA()  - 0.8));
        }
        CiudadanoServicio.guardarCiudadanos(ciudadanos);
    }

    private CiudadanoTherian buscarCiudadanoPorManada(
            List<CiudadanoTherian> ciudadanos, String nombreManada) {
        List<CiudadanoTherian> candidatos = new ArrayList<>();
        for (CiudadanoTherian c : ciudadanos) {
            for (Especies.AfiliacionManada a : c.getManadas()) {
                if (a.estaActivo() && nombreManada.equals(a.getNombreManada())) {
                    candidatos.add(c); break;
                }
            }
        }
        return candidatos.isEmpty() ? null
             : candidatos.get(random.nextInt(candidatos.size()));
    }

    private void actualizarLog() {
        StringBuilder sb = new StringBuilder();
        for (int i = logEnfrentamientos.size() - 1; i >= 0; i--)
            sb.append(logEnfrentamientos.get(i));
        areaLog.setText(sb.toString());
        areaLog.setCaretPosition(0);
    }

    // ─── Dibujo ───────────────────────────────────────────────────────────

    private void dibujar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = panelMapa.getWidth();
        int h = panelMapa.getHeight();

        if (mapaImagen == null)
            mapaImagen = new ImageIcon("Imagenes/Mapa.jpeg").getImage();
        g2.drawImage(mapaImagen, 0, 0, w, h, null);

        double sx = (double) w / 1080;
        double sy = (double) h / 1080;

        for (PuntoManada p : puntosManadas) {
            int px = (int)(p.posX * sx);
            int py = (int)(p.posY * sy);
            int pr = Math.max(8, 8 + Math.min(p.miembros / 3, 10));

            if (p == seleccionada) {
                g2.setColor(new Color(255, 215, 0, 160));
                g2.fillOval(px - pr - 5, py - pr - 5, (pr+5)*2, (pr+5)*2);
            }

            Color color = p.esPredadora
                ? new Color(220, 80,  80)
                : new Color(80,  200, 80);

            g2.setColor(color);
            g2.setStroke(new BasicStroke(2));
            g2.fillOval(px - pr, py - pr, pr * 2, pr * 2);
            g2.setColor(Color.WHITE);
            g2.drawOval(px - pr, py - pr, pr * 2, pr * 2);

            g2.setFont(new Font("Arial", Font.BOLD, 9));
            FontMetrics fm = g2.getFontMetrics();
            String etiq = p.nombre.replace("Manada ", "");
            int tw = fm.stringWidth(etiq);
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(px - tw/2 - 2, py - pr - 14, tw + 4, 13, 4, 4);
            g2.setColor(Color.WHITE);
            g2.drawString(etiq, px - tw/2, py - pr - 4);
        }
    }

    // ─── Clic sobre manada ────────────────────────────────────────────────

    private void detectarClic(int mx, int my) {
        int w = panelMapa.getWidth();
        int h = panelMapa.getHeight();
        double sx = (double) w / 1080;
        double sy = (double) h / 1080;

        seleccionada = null;
        for (PuntoManada p : puntosManadas) {
            int px = (int)(p.posX * sx);
            int py = (int)(p.posY * sy);
            int pr = Math.max(8, 8 + Math.min(p.miembros / 3, 10));
            if (Math.sqrt(Math.pow(mx-px,2)+Math.pow(my-py,2)) <= pr + 5) {
                seleccionada = p;
                infoLabel.setText("▶ " + p.nombre
                    + "  |  " + p.especie
                    + "  |  " + p.miembros + " miembros"
                    + "  |  " + (p.esPredadora ? "🔴 Predadora" : "🟢 No predadora"));
                return;
            }
        }
        infoLabel.setText("Haz clic sobre una manada para ver información");
    }

    // ─── Helper ───────────────────────────────────────────────────────────

    private boolean esPredadora(String especie) {
        return especie.equals("Lobo")  || especie.equals("Leon")   ||
               especie.equals("Tigre") || especie.equals("Halcon") ||
               especie.equals("Orca");
    }
}