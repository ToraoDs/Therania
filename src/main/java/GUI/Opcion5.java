package GUI;

import com.google.gson.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class Opcion5 extends JPanel implements ActionListener {

    private JTable T1;
    private JComboBox<String> Tipo;
    private JComboBox<String> filtro;
    private final Consumer<Integer> onVerTrayectoria;

    private static final java.util.Set<String> COLUMNAS_EXCLUIDAS =
        new java.util.HashSet<>(java.util.Arrays.asList("afiliacionesEfectivas"));

    // Colores consistentes con el resto de la app
    private static final Color BG_DARK    = new Color(30, 30, 40);
    private static final Color BG_PANEL   = new Color(40, 40, 55);
    private static final Color BG_TABLE   = new Color(25, 25, 35);
    private static final Color FG_WHITE   = Color.WHITE;
    private static final Color FG_GRAY    = Color.LIGHT_GRAY;
    private static final Color ACCENT     = new Color(60, 100, 160);
    private static final Color ACCENT_ALT = new Color(50, 80, 130);
    private static final Color ROW_ODD    = new Color(35, 35, 48);
    private static final Color ROW_EVEN   = new Color(28, 28, 40);
    private static final Color ROW_SEL    = new Color(60, 100, 160, 180);

    private ImageIcon redimensionarIcono(String ruta, int ancho, int alto) {
        try {
            return new ImageIcon(new ImageIcon(ruta)
                .getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH));
        } catch (Exception e) { return null; }
    }

    public Opcion5(Consumer<Integer> onVerTrayectoria) {
        this.onVerTrayectoria = onVerTrayectoria;
        setLayout(new BorderLayout(0, 8));
        setBackground(BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(construirPanelNorte(), BorderLayout.NORTH);
        add(construirPanelTabla(), BorderLayout.CENTER);

        // Carga inicial
        Tipo.setSelectedItem("ciudadanos");
        cargarTabla("Archivos/ciudadanos.json");
    }

    // ── Panel Norte: título + iconos + controles ──────────────────────────
    private JPanel construirPanelNorte() {
        JPanel norte = new JPanel(new BorderLayout(0, 6));
        norte.setBackground(BG_DARK);

        // Título
        JLabel titulo = new JLabel("Historial Global de Registros", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setForeground(FG_WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        // Iconos de especies
        JPanel panelIconos = new JPanel(new GridLayout(1, 10, 4, 0));
        panelIconos.setBackground(BG_PANEL);
        panelIconos.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        String[] nombres = {"Alce","Cebra","Ciervo","Foca","Halcon","León","Lobo","Orca","Paloma","Tigre"};
        for (String nombre : nombres) {
            ImageIcon ic = redimensionarIcono(
                "Imagenes/Iconos therania/" + nombre + ".png", 55, 55);
            JLabel lbl = ic != null ? new JLabel(ic, SwingConstants.CENTER)
                                    : new JLabel(nombre, SwingConstants.CENTER);
            lbl.setForeground(FG_GRAY);
            lbl.setFont(new Font("Arial", Font.PLAIN, 10));
            panelIconos.add(lbl);
        }

        // Controles de filtro
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        panelFiltros.setBackground(BG_PANEL);
        panelFiltros.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(70, 70, 100)),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)));

        Tipo = new JComboBox<>(new String[]{"ciudadanos","especies","manadas","rituales"});
        estilizarCombo(Tipo);
        Tipo.addActionListener(ev -> {
            actualizarFiltros();
            cargarTabla("Archivos/" + Tipo.getSelectedItem() + ".json");
        });

        filtro = new JComboBox<>(new String[]{"Todos"});
        estilizarCombo(filtro);
        filtro.addActionListener(ev -> aplicarFiltro());

        JButton btnBuscar = new JButton("⟳ Actualizar");
        btnBuscar.setBackground(ACCENT);
        btnBuscar.setForeground(FG_WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));
        btnBuscar.setFont(new Font("Arial", Font.BOLD, 12));
        btnBuscar.addActionListener(this);
        btnBuscar.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnBuscar.setBackground(ACCENT_ALT); }
            public void mouseExited(MouseEvent e)  { btnBuscar.setBackground(ACCENT); }
        });

        JLabel hint = new JLabel("  ↑ Doble clic en ciudadano → ver trayectoria");
        hint.setFont(new Font("Arial", Font.ITALIC, 11));
        hint.setForeground(new Color(150, 210, 255));

        JLabel lTipo   = etiqueta("Tipo:");
        JLabel lFiltro = etiqueta("Filtro:");

        panelFiltros.add(lTipo);
        panelFiltros.add(Tipo);
        panelFiltros.add(lFiltro);
        panelFiltros.add(filtro);
        panelFiltros.add(btnBuscar);
        panelFiltros.add(hint);

        norte.add(titulo,       BorderLayout.NORTH);
        norte.add(panelIconos,  BorderLayout.CENTER);
        norte.add(panelFiltros, BorderLayout.SOUTH);
        return norte;
    }

    // ── Panel tabla ───────────────────────────────────────────────────────
    private JScrollPane construirPanelTabla() {
        T1 = new JTable() {
            // Filas alternadas
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(ROW_SEL);
                    c.setForeground(FG_WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD);
                    c.setForeground(FG_GRAY);
                }
                return c;
            }
        };

        T1.setBackground(BG_TABLE);
        T1.setForeground(FG_GRAY);
        T1.setGridColor(new Color(55, 55, 75));
        T1.setFont(new Font("Arial", Font.PLAIN, 12));
        T1.setRowHeight(28);
        T1.setShowGrid(true);
        T1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        T1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Cabecera
        JTableHeader header = T1.getTableHeader();
        header.setBackground(new Color(50, 50, 70));
        header.setForeground(FG_WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setReorderingAllowed(false);

        // Doble clic → trayectoria
        T1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2
                    && "ciudadanos".equals(Tipo.getSelectedItem())) {
                    int fila = T1.getSelectedRow();
                    if (fila < 0) return;
                    for (int col = 0; col < T1.getColumnCount(); col++) {
                        if ("id".equals(T1.getColumnName(col))) {
                            try {
                                int id = Integer.parseInt(
                                    T1.getValueAt(fila, col).toString());
                                if (onVerTrayectoria != null)
                                    onVerTrayectoria.accept(id);
                            } catch (NumberFormatException ignored) {}
                            break;
                        }
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(T1);
        scroll.setBackground(BG_TABLE);
        scroll.getViewport().setBackground(BG_TABLE);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 100)));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return scroll;
    }

    // ── Helpers de estilo ─────────────────────────────────────────────────
    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(FG_GRAY);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        return l;
    }

    private void estilizarCombo(JComboBox<String> combo) {
        combo.setBackground(BG_PANEL);
        combo.setForeground(FG_WHITE);
        combo.setFont(new Font("Arial", Font.PLAIN, 12));
        combo.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 120)));
    }

    // ── Actualizar opciones de filtro según tipo ──────────────────────────
    private void actualizarFiltros() {
        filtro.removeAllItems();
        filtro.addItem("Todos");
        switch ((String) Tipo.getSelectedItem()) {
            case "ciudadanos":
                for (String esp : new String[]{
                    "Lobo","Leon","Ciervo","Alce","Tigre",
                    "Halcon","Orca","Cebra","Foca","Paloma"})
                    filtro.addItem("Especie: " + esp);
                break;
            case "manadas":
                try {
                    for (java.util.Map<String, Object> m :
                        Servicios.ManadaServicio.cargarManadas())
                        filtro.addItem("Manada: " + m.get("nombre"));
                } catch (Exception ignored) {}
                break;
            case "rituales":
                for (String t : new String[]{
                    "AULLIDO_LUNAR","CAZA_EN_MANADA","RUGIDO_REAL",
                    "PATRULLA_TERRITORIAL","DANZA_DEL_BOSQUE","CONTEMPLACION_NATURAL",
                    "BRAMIDO_MONTANIA","MARCAJE_TERRITORIAL","ACECHO_NOCTURNO",
                    "CEREMONIA_SOLITARIA","VUELO_SUPREMO","OBSERVACION_ALTA",
                    "CANTO_MARINO","CACERIA_GRUPAL","GALOPE_LIBRE","VIGILANCIA_GRUPAL",
                    "ZAMBULLIDA_POLAR","JUEGO_GLACIAR","VUELO_MENSAJERO","CIRCULO_DE_PAZ"})
                    filtro.addItem("Tipo: " + t);
                break;
        }
    }

    private void aplicarFiltro() {
        String archivo   = (String) Tipo.getSelectedItem();
        String filtroVal = (String) filtro.getSelectedItem();
        if (filtroVal == null || filtroVal.equals("Todos")) {
            cargarTabla("Archivos/" + archivo + ".json");
            return;
        }
        switch (archivo) {
            case "ciudadanos":
                if (filtroVal.startsWith("Especie: "))
                    cargarTablaFiltrada("Archivos/ciudadanos.json", "especie",
                        filtroVal.replace("Especie: ", ""));
                break;
            case "manadas":
                if (filtroVal.startsWith("Manada: "))
                    cargarTablaFiltrada("Archivos/manadas.json", "nombre",
                        filtroVal.replace("Manada: ", ""));
                break;
            case "rituales":
                if (filtroVal.startsWith("Tipo: "))
                    cargarTablaFiltrada("Archivos/rituales.json", "tipo",
                        filtroVal.replace("Tipo: ", ""));
                break;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) { aplicarFiltro(); }

    // ── Formatear valores de celdas ───────────────────────────────────────
    private String formatearValor(String clave, JsonElement valor) {
        if (valor == null || valor.isJsonNull()) return "";

        if ("afiliaciones".equals(clave) && valor.isJsonArray()) {
            StringBuilder sb = new StringBuilder();
            for (JsonElement item : valor.getAsJsonArray()) {
                if (item.isJsonObject()) {
                    JsonElement nombre = item.getAsJsonObject().get("nombreManada");
                    if (nombre != null && !nombre.isJsonNull()) {
                        if (sb.length() > 0) sb.append(" → ");
                        sb.append(nombre.getAsString());
                    }
                }
            }
            return sb.toString();
        }

        if ("historialIAA".equals(clave) && valor.isJsonArray()) {
            // Solo los últimos 3 puntajes, sin fechas
            JsonArray arr = valor.getAsJsonArray();
            int total = arr.size();
            int desde = Math.max(0, total - 3);
            StringBuilder sb = new StringBuilder();
            for (int i = desde; i < total; i++) {
                JsonObject punto = arr.get(i).getAsJsonObject();
                if (punto.has("iaa")) {
                    if (sb.length() > 0) sb.append(" | ");
                    sb.append(String.format("%.1f", punto.get("iaa").getAsDouble()));
                }
            }
            return sb.toString();
        }

        return valor.isJsonPrimitive() ? valor.getAsString() : valor.toString();
    }

    // ── Ajustar ancho de columnas al contenido ────────────────────────────
    private void ajustarColumnas() {
        for (int col = 0; col < T1.getColumnCount(); col++) {
            int maxAncho = 60;
            // Cabecera
            TableColumn column = T1.getColumnModel().getColumn(col);
            TableCellRenderer headerRenderer = T1.getTableHeader().getDefaultRenderer();
            Component headerComp = headerRenderer.getTableCellRendererComponent(
                T1, column.getHeaderValue(), false, false, 0, col);
            maxAncho = Math.max(maxAncho, headerComp.getPreferredSize().width + 10);
            // Filas
            for (int row = 0; row < T1.getRowCount(); row++) {
                TableCellRenderer cellRenderer = T1.getCellRenderer(row, col);
                Component cellComp = T1.prepareRenderer(cellRenderer, row, col);
                maxAncho = Math.max(maxAncho, cellComp.getPreferredSize().width + 10);
            }
            column.setPreferredWidth(Math.min(maxAncho, 300)); // máximo 300px por columna
        }
    }

    // ── Cargar tabla completa ─────────────────────────────────────────────
    private void cargarTabla(String ruta) {
        try {
            String contenido = new String(Files.readAllBytes(Paths.get(ruta)));
            JsonArray arreglo = JsonParser.parseString(contenido).getAsJsonArray();
            DefaultTableModel modelo = new DefaultTableModel() {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };

            if (arreglo.size() > 0) {
                JsonObject primero = arreglo.get(0).getAsJsonObject();
                java.util.List<String> claves = new java.util.ArrayList<>();
                for (String clave : primero.keySet()) {
                    if (!COLUMNAS_EXCLUIDAS.contains(clave)) {
                        claves.add(clave);
                        modelo.addColumn(clave);
                    }
                }
                for (JsonElement item : arreglo) {
                    JsonObject fila = item.getAsJsonObject();
                    Object[] datos = new Object[claves.size()];
                    for (int j = 0; j < claves.size(); j++)
                        datos[j] = formatearValor(claves.get(j), fila.get(claves.get(j)));
                    modelo.addRow(datos);
                }
            }
            T1.setModel(modelo);
            T1.setRowHeight(28);
            ajustarColumnas();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar datos:\n" + e.getMessage());
        }
    }

    // ── Cargar tabla filtrada ─────────────────────────────────────────────
    private void cargarTablaFiltrada(String ruta, String campo, String valor) {
        try {
            String contenido = new String(Files.readAllBytes(Paths.get(ruta)));
            JsonArray arreglo = JsonParser.parseString(contenido).getAsJsonArray();
            JsonArray filtrado = new JsonArray();
            for (JsonElement item : arreglo) {
                JsonObject obj = item.getAsJsonObject();
                JsonElement campEl = obj.get(campo);
                if (campEl != null && campEl.getAsString().equalsIgnoreCase(valor))
                    filtrado.add(obj);
            }
            if (filtrado.size() == 0) {
                JOptionPane.showMessageDialog(null, "No hay resultados para: " + valor);
                return;
            }
            DefaultTableModel modelo = new DefaultTableModel() {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            JsonObject primero = filtrado.get(0).getAsJsonObject();
            java.util.List<String> claves = new java.util.ArrayList<>();
            for (String clave : primero.keySet()) {
                if (!COLUMNAS_EXCLUIDAS.contains(clave)) {
                    claves.add(clave);
                    modelo.addColumn(clave);
                }
            }
            for (JsonElement item : filtrado) {
                JsonObject fila = item.getAsJsonObject();
                Object[] datos = new Object[claves.size()];
                for (int j = 0; j < claves.size(); j++)
                    datos[j] = formatearValor(claves.get(j), fila.get(claves.get(j)));
                modelo.addRow(datos);
            }
            T1.setModel(modelo);
            T1.setRowHeight(28);
            ajustarColumnas();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al filtrar: " + e.getMessage());
        }
    }
}