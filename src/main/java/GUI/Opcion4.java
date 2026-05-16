package GUI;

import Servicios.CiudadanoServicio;
import Servicios.RitualServicio;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Opcion4 extends JPanel {

    private JTextField campoBusqueda;
    private JButton btnBuscar;
    private JLabel lblNombreCiudadano;
    private PanelGrafico panelGrafico;
    private JTable tablaRituales;
    private DefaultTableModel modeloTabla;

    public Opcion4() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(30, 30, 40));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(construirPanelBusqueda(), BorderLayout.NORTH);
        add(construirPanelCentral(),  BorderLayout.CENTER);
    }

    public Opcion4(int idInicial) {
        this(); // llama al constructor principal
        campoBusqueda.setText(String.valueOf(idInicial));
        cargarTrayectoria(); // carga automáticamente
    }
    // ─── Panel búsqueda ───────────────────────────────────────────────────

    private JPanel construirPanelBusqueda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panel.setBackground(new Color(40, 40, 55));
        panel.setBorder(tituloBorde("Trayectoria IAA de un ciudadano"));

        JLabel lbl = new JLabel("ID ciudadano:");
        lbl.setForeground(Color.LIGHT_GRAY);

        campoBusqueda = new JTextField(10);

        btnBuscar = new JButton("Ver trayectoria");
        btnBuscar.setBackground(new Color(60, 100, 160));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.addActionListener(e -> cargarTrayectoria());

        // También buscar al presionar Enter
        campoBusqueda.addActionListener(e -> cargarTrayectoria());

        lblNombreCiudadano = new JLabel("");
        lblNombreCiudadano.setForeground(new Color(200, 200, 100));
        lblNombreCiudadano.setFont(new Font("Arial", Font.BOLD, 14));

        panel.add(lbl);
        panel.add(campoBusqueda);
        panel.add(btnBuscar);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(lblNombreCiudadano);
        return panel;
    }

    // ─── Panel central: gráfico + tabla ───────────────────────────────────

    private JSplitPane construirPanelCentral() {
        // Gráfico arriba
        panelGrafico = new PanelGrafico();
        panelGrafico.setPreferredSize(new Dimension(0, 280));

        JPanel wrapGrafico = new JPanel(new BorderLayout());
        wrapGrafico.setBackground(new Color(30, 30, 40));
        wrapGrafico.setBorder(tituloBorde("Evolución mensual del IAA"));
        wrapGrafico.add(panelGrafico, BorderLayout.CENTER);

        // Tabla abajo
        String[] columnas = {"#", "Ritual", "Fecha", "Tipo", "Intensidad", "Manada"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaRituales = new JTable(modeloTabla);
        tablaRituales.setBackground(new Color(25, 25, 35));
        tablaRituales.setForeground(Color.LIGHT_GRAY);
        tablaRituales.setGridColor(new Color(60, 60, 80));
        tablaRituales.setRowHeight(24);
        tablaRituales.getTableHeader().setBackground(new Color(50, 50, 70));
        tablaRituales.getTableHeader().setForeground(Color.WHITE);

        JPanel wrapTabla = new JPanel(new BorderLayout());
        wrapTabla.setBackground(new Color(30, 30, 40));
        wrapTabla.setBorder(tituloBorde("Historial de rituales"));
        wrapTabla.add(new JScrollPane(tablaRituales), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, wrapGrafico, wrapTabla);
        split.setDividerLocation(300);
        split.setBackground(new Color(30, 30, 40));
        return split;
    }

    // ─── Lógica principal ─────────────────────────────────────────────────

    private void cargarTrayectoria() {
        String idTexto = campoBusqueda.getText().trim();
        if (idTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa un ID de ciudadano.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idBuscado;
        try {
            idBuscado = Integer.parseInt(idTexto);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El ID debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Buscar ciudadano
        List<Map<String, Object>> todos = CiudadanoServicio.cargarCiudadanos();
        Map<String, Object> ciudadano = null;
        for (Map<String, Object> c : todos) {
            if (((Number) c.get("id")).intValue() == idBuscado) {
                ciudadano = c;
                break;
            }
        }

        if (ciudadano == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el ciudadano con ID " + idBuscado,
                "No encontrado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        lblNombreCiudadano.setText(ciudadano.get("nombre") + " " + ciudadano.get("apellido")
            + "  |  " + ciudadano.get("especie")
            + "  |  IAA: " + String.format("%.2f", ((Number) ciudadano.get("iaa")).doubleValue()));

        // Cargar rituales del ciudadano
        List<Map<String, Object>> rituales = RitualServicio.historialPorCiudadano(idBuscado);

        // Agrupar por "YYYY-MM" en orden cronológico
        Map<String, Integer> porMesAnio = new LinkedHashMap<>();
        modeloTabla.setRowCount(0);
        int fila = 1;

        for (Map<String, Object> r : rituales) {
            String fecha = (String) r.get("fecha");
            // fecha tiene formato "YYYY-MM-DD", tomar "YYYY-MM"
            if (fecha != null && fecha.length() >= 7) {
                String clave = fecha.substring(0, 7); // "2026-01"
                porMesAnio.put(clave, porMesAnio.getOrDefault(clave, 0) + 1);
            }

            modeloTabla.addRow(new Object[]{
                fila++,
                r.get("nombre"),
                fecha,
                r.get("tipo"),
                String.format("%.1f", ((Number) r.get("intensidad")).doubleValue()),
                r.get("manadaResponsable")
            });
        }

            // Leer historialIAA real del JSON del ciudadano
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> histIAA =
                (List<Map<String, Object>>) ciudadano.get("historialIAA");

            if (histIAA == null || histIAA.isEmpty()) {
                panelGrafico.setDatos(new String[]{"Sin datos"}, new double[]{0});
                panelGrafico.repaint();
                return;
            }

            // Ordenar cronológicamente
            histIAA.sort((a, b) ->
                ((String) a.get("mes")).compareTo((String) b.get("mes")));

            String primerMes = (String) histIAA.get(0).get("mes");
            String ultimoMes = (String) histIAA.get(histIAA.size() - 1).get("mes");
            List<String> todosMeses = generarRangoMeses(primerMes, ultimoMes);

            // Mapa mes → IAA real
            Map<String, Double> mapaIAA = new LinkedHashMap<>();
            for (Map<String, Object> punto : histIAA) {
                mapaIAA.put((String) punto.get("mes"),
                            ((Number) punto.get("iaa")).doubleValue());
            }

            // Construir etiquetas y valores
            String[] nombresMeses = {"Ene","Feb","Mar","Abr","May","Jun",
                                    "Jul","Ago","Sep","Oct","Nov","Dic"};
            String[] etiquetas  = new String[todosMeses.size()];
            double[] valoresIAA = new double[todosMeses.size()];

            for (int i = 0; i < todosMeses.size(); i++) {
                String clave = todosMeses.get(i);
                int    mes   = Integer.parseInt(clave.substring(5, 7));
                String anio  = clave.substring(2, 4);
                etiquetas[i]  = nombresMeses[mes - 1] + "'" + anio;
                valoresIAA[i] = mapaIAA.getOrDefault(clave,
                    i > 0 ? valoresIAA[i - 1] : 0.0);
            }

            panelGrafico.setDatos(etiquetas, valoresIAA);
            panelGrafico.repaint();
    }


    // Genera lista de "YYYY-MM" desde inicio hasta fin sin saltar meses
    private List<String> generarRangoMeses(String inicio, String fin) {
        List<String> lista = new ArrayList<>();
        int anioI = Integer.parseInt(inicio.substring(0, 4));
        int mesI  = Integer.parseInt(inicio.substring(5, 7));
        int anioF = Integer.parseInt(fin.substring(0, 4));
        int mesF  = Integer.parseInt(fin.substring(5, 7));

        int anio = anioI, mes = mesI;
        while (anio < anioF || (anio == anioF && mes <= mesF)) {
            lista.add(String.format("%04d-%02d", anio, mes));
            mes++;
            if (mes > 12) { mes = 1; anio++; }
        }
        return lista;
    }

    // ─── Panel de gráfico interno ─────────────────────────────────────────

    private static class PanelGrafico extends JPanel {

        private String[] etiquetas = new String[0];
        private double[] valores   = new double[0];

        PanelGrafico() {
            setBackground(new Color(22, 22, 32));
        }

        void setDatos(String[] etiquetas, double[] valores) {
            this.etiquetas = etiquetas;
            this.valores   = valores;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (valores.length == 0) {
                g.setColor(new Color(100, 100, 130));
                g.setFont(new Font("Arial", Font.PLAIN, 14));
                g.drawString("Busca un ciudadano para ver su trayectoria IAA", 40, getHeight() / 2);
                return;
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int margenIzq = 55, margenDer = 20, margenSup = 20, margenInf = 40;
            int ancho = getWidth()  - margenIzq - margenDer;
            int alto  = getHeight() - margenSup  - margenInf;

            // Ejes
            g2.setColor(new Color(80, 80, 110));
            g2.setStroke(new BasicStroke(1));
            // Líneas horizontales de referencia cada 20 puntos
            for (int y = 0; y <= 100; y += 20) {
                int py = margenSup + alto - (int)(y / 100.0 * alto);
                g2.drawLine(margenIzq, py, margenIzq + ancho, py);
                g2.setColor(new Color(130, 130, 160));
                g2.setFont(new Font("Arial", Font.PLAIN, 10));
                g2.drawString(String.valueOf(y), margenIzq - 28, py + 4);
                g2.setColor(new Color(80, 80, 110));
            }

            // Eje X e Y principales
            g2.setColor(new Color(160, 160, 200));
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(margenIzq, margenSup, margenIzq, margenSup + alto);
            g2.drawLine(margenIzq, margenSup + alto, margenIzq + ancho, margenSup + alto);

            int n = valores.length;

            if (n == 1) {
                int px = margenIzq + ancho / 2;
                int py = margenSup + alto - (int)(valores[0] / 100.0 * alto);
                g2.setColor(new Color(130, 200, 255));
                g2.fillOval(px - 6, py - 6, 12, 12);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(px - 6, py - 6, 12, 12);
                g2.setFont(new Font("Arial", Font.PLAIN, 11));
                g2.setColor(new Color(200, 220, 255));
                g2.drawString(String.format("%.0f", valores[0]), px - 10, py - 12);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawString(etiquetas[0], px - 15, margenSup + alto + 18);
                return;
            }

            int espacioX = ancho / (n - 1);
            // Área bajo la curva
            int[] pxs = new int[n];
            int[] pys = new int[n];
            for (int i = 0; i < n; i++) {
                pxs[i] = margenIzq + i * espacioX;
                pys[i] = margenSup + alto - (int)(valores[i] / 100.0 * alto);
            }

            // Polígono de relleno
            int[] polyX = new int[n + 2];
            int[] polyY = new int[n + 2];
            polyX[0] = pxs[0]; polyY[0] = margenSup + alto;
            for (int i = 0; i < n; i++) { polyX[i+1] = pxs[i]; polyY[i+1] = pys[i]; }
            polyX[n+1] = pxs[n-1]; polyY[n+1] = margenSup + alto;

            g2.setColor(new Color(60, 120, 200, 60));
            g2.fillPolygon(polyX, polyY, n + 2);

            // Línea de la curva
            g2.setColor(new Color(80, 160, 255));
            g2.setStroke(new BasicStroke(2.5f));
            for (int i = 0; i < n - 1; i++) {
                g2.drawLine(pxs[i], pys[i], pxs[i+1], pys[i+1]);
            }

            // Puntos y etiquetas de mes
            for (int i = 0; i < n; i++) {
                // Punto
                g2.setColor(new Color(130, 200, 255));
                g2.fillOval(pxs[i] - 5, pys[i] - 5, 10, 10);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(pxs[i] - 5, pys[i] - 5, 10, 10);

                // Valor sobre el punto
                g2.setFont(new Font("Arial", Font.PLAIN, 9));
                g2.setColor(new Color(200, 220, 255));
                g2.drawString(String.format("%.0f", valores[i]), pxs[i] - 8, pys[i] - 9);

                // Etiqueta mes
                g2.setColor(Color.LIGHT_GRAY);
                g2.setFont(new Font("Arial", Font.PLAIN, 11));
                int tw = g2.getFontMetrics().stringWidth(etiquetas[i]);
                g2.drawString(etiquetas[i], pxs[i] - tw / 2, margenSup + alto + 18);
            }

            // Título eje Y
            g2.setColor(new Color(150, 150, 190));
            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            g2.rotate(-Math.PI / 2);
            g2.drawString("IAA", -(getHeight() / 2 + 12), 14);
            g2.rotate(Math.PI / 2);
        }
    }

    // ─── Helper ───────────────────────────────────────────────────────────

    private TitledBorder tituloBorde(String titulo) {
        TitledBorder b = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 120)), titulo);
        b.setTitleColor(Color.LIGHT_GRAY);
        return b;
    }
}