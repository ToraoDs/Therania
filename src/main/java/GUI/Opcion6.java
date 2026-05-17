package GUI;

import Especies.*;
import Manadas.Manada;
import Manadas.ManadaDePaso;
import Servicios.CiudadanoServicio;
import Servicios.ManadaServicio;
import Simulacion.Reloj;
import com.google.gson.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class Opcion6 extends JPanel {

    private final Reloj reloj;
    private final Consumer<Integer> onVerTrayectoria;

    private JTextField    campoNombre;
    private JTextField    campoApellido;
    private JComboBox<String> comboEspecie;
    private JTextArea     areaResultado;
    private JTable        tabla;

    private JTextField    campoNombreManada;
    private JTextField    campoLema;
    private JComboBox<String> comboEspecieManada;
    private JTextField    campoCupo;
    private JTextField    campoIAAMin;
    private JTextField    campoIAAMax;
    private JLabel        lblTerritorioManada;
    private String        territorioManada = "x:540,y:540,r:80";

    private JTextField    campoNombreEspecie;
    private JTextField    campoSonido;
    private JTextField    campoCaracteristicas;
    private JLabel        lblCoordenadas;
    private JCheckBox     checkPredadora;
    private String        habitatSeleccionado = "x:540,y:540,r:80";

    private static final String[] ESPECIES = {
        "Lobo","Leon","Ciervo","Alce","Tigre",
        "Halcon","Orca","Cebra","Foca","Paloma"
    };

    private static final Color BG_PANEL = new Color(40, 40, 55);
    private static final Color BG_TABLE = new Color(25, 25, 35);
    private static final Color FG_WHITE = Color.WHITE;
    private static final Color FG_GRAY  = Color.LIGHT_GRAY;
    private static final Color ACCENT   = new Color(60, 100, 160);
    private static final Color ROW_ODD  = new Color(35, 35, 48);
    private static final Color ROW_EVEN = new Color(28, 28, 40);
    private static final Color ROW_SEL  = new Color(60, 100, 160, 180);

    // ── Constructor ───────────────────────────────────────────────────────
    public Opcion6(Reloj reloj, Consumer<Integer> onVerTrayectoria) {
        this.reloj            = reloj;
        this.onVerTrayectoria = onVerTrayectoria;

        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 40));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("Registro avanzado", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        add(titulo, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(new Color(40, 40, 55));
        tabs.setForeground(Color.WHITE);
        tabs.setFont(new Font("Arial", Font.BOLD, 13));

        tabs.addTab("Ciudadano", construirPanelCiudadano());
        tabs.addTab("Especie",   construirPanelEspecie());
        tabs.addTab("Manada",    construirPanelManada());

        add(tabs, BorderLayout.CENTER);
        cargarTablaPersonal();
    }

    // ── Tab Ciudadano: formulario + lista ─────────────────────────────────
    private JPanel construirPanelCiudadano() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 40));

        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            construirPanelCrear(),
            construirPanelLista());
        split.setDividerLocation(340);
        split.setResizeWeight(0.4);
        split.setBackground(new Color(30, 30, 40));
        split.setBorder(null);

        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    // ── Formulario de creación de ciudadano ───────────────────────────────
    private JPanel construirPanelCrear() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG_PANEL);
        panel.setBorder(borde("Nuevo ciudadano"));

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setBackground(BG_PANEL);
        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(8, 10, 8, 10);
        g.anchor  = GridBagConstraints.WEST;
        g.fill    = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        campos.add(etiqueta("Nombre:"), g);
        g.gridx = 1; g.weightx = 1;
        campoNombre = new JTextField(14);
        estilizarCampo(campoNombre);
        campos.add(campoNombre, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        campos.add(etiqueta("Apellido:"), g);
        g.gridx = 1; g.weightx = 1;
        campoApellido = new JTextField(14);
        estilizarCampo(campoApellido);
        campos.add(campoApellido, g);

        g.gridx = 0; g.gridy = 2; g.weightx = 0;
        campos.add(etiqueta("Especie:"), g);
        g.gridx = 1; g.weightx = 1;
        comboEspecie = new JComboBox<>(ESPECIES);
        estilizarCombo(comboEspecie);
        campos.add(comboEspecie, g);

        g.gridx = 0; g.gridy = 3; g.gridwidth = 2;
        JButton btnCrear = new JButton("✚  Crear ciudadano");
        btnCrear.setBackground(new Color(50, 130, 60));
        btnCrear.setForeground(FG_WHITE);
        btnCrear.setFont(new Font("Arial", Font.BOLD, 13));
        btnCrear.setFocusPainted(false);
        btnCrear.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        btnCrear.addActionListener(e -> crearCiudadano());
        campos.add(btnCrear, g);

        g.gridy = 4;
        areaResultado = new JTextArea(6, 20);
        areaResultado.setEditable(false);
        areaResultado.setBackground(new Color(25, 25, 35));
        areaResultado.setForeground(new Color(180, 230, 180));
        areaResultado.setFont(new Font("Monospaced", Font.PLAIN, 11));
        areaResultado.setLineWrap(true);
        areaResultado.setWrapStyleWord(true);
        JScrollPane scrollArea = new JScrollPane(areaResultado);
        scrollArea.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 100)));
        campos.add(scrollArea, g);

        panel.add(campos, BorderLayout.NORTH);
        return panel;
    }

    // ── Lista de ciudadanos creados por el usuario ────────────────────────
    private JPanel construirPanelLista() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(BG_PANEL);
        panel.setBorder(borde("Mis ciudadanos  —  doble clic para ver trayectoria"));

        tabla = new JTable() {
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
        tabla.setBackground(BG_TABLE);
        tabla.setForeground(FG_GRAY);
        tabla.setGridColor(new Color(55, 55, 75));
        tabla.setFont(new Font("Arial", Font.PLAIN, 12));
        tabla.setRowHeight(26);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(new Color(50, 50, 70));
        header.setForeground(FG_WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 12));

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tabla.getSelectedRow();
                    if (fila < 0) return;
                    for (int col = 0; col < tabla.getColumnCount(); col++) {
                        if ("id".equals(tabla.getColumnName(col))) {
                            try {
                                int id = Integer.parseInt(
                                    tabla.getValueAt(fila, col).toString());
                                if (onVerTrayectoria != null)
                                    onVerTrayectoria.accept(id);
                            } catch (NumberFormatException ignored) {}
                            break;
                        }
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBackground(BG_TABLE);
        scroll.getViewport().setBackground(BG_TABLE);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 100)));

        JButton btnRefrescar = new JButton("↺ Actualizar lista");
        btnRefrescar.setBackground(ACCENT);
        btnRefrescar.setForeground(FG_WHITE);
        btnRefrescar.setFocusPainted(false);
        btnRefrescar.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        btnRefrescar.addActionListener(e -> cargarTablaPersonal());

        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBtn.setBackground(BG_PANEL);
        panelBtn.add(btnRefrescar);

        panel.add(scroll,   BorderLayout.CENTER);
        panel.add(panelBtn, BorderLayout.SOUTH);
        return panel;
    }

    // ── Tab Especie ───────────────────────────────────────────────────────
    private JPanel construirPanelEspecie() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(new Color(40, 40, 55));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setBackground(new Color(40, 40, 55));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 10, 8, 10);
        g.anchor = GridBagConstraints.WEST;
        g.fill   = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        campos.add(etiqueta("Nombre de especie:"), g);
        g.gridx = 1; g.weightx = 1;
        campoNombreEspecie = new JTextField(14);
        estilizarCampo(campoNombreEspecie);
        campos.add(campoNombreEspecie, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        campos.add(etiqueta("Sonido predominante:"), g);
        g.gridx = 1; g.weightx = 1;
        campoSonido = new JTextField(14);
        estilizarCampo(campoSonido);
        campos.add(campoSonido, g);

        g.gridx = 0; g.gridy = 2; g.weightx = 0;
        campos.add(etiqueta("Características:"), g);
        g.gridx = 1; g.weightx = 1;
        campoCaracteristicas = new JTextField(14);
        estilizarCampo(campoCaracteristicas);
        campos.add(campoCaracteristicas, g);

        g.gridx = 0; g.gridy = 3; g.weightx = 0;
        campos.add(etiqueta("¿Es predadora?:"), g);
        g.gridx = 1;
        checkPredadora = new JCheckBox("Sí");
        checkPredadora.setBackground(new Color(40, 40, 55));
        checkPredadora.setForeground(Color.WHITE);
        campos.add(checkPredadora, g);

        g.gridx = 0; g.gridy = 4; g.weightx = 0;
        campos.add(etiqueta("Hábitat en el mapa:"), g);
        g.gridx = 1;
        JPanel panelHabitat = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelHabitat.setBackground(new Color(40, 40, 55));
        lblCoordenadas = new JLabel("x:540,y:540,r:80");
        lblCoordenadas.setForeground(new Color(150, 210, 255));
        JButton btnHabitat = new JButton("🗺 Definir en mapa");
        btnHabitat.setBackground(new Color(60, 80, 130));
        btnHabitat.setForeground(Color.WHITE);
        btnHabitat.setFocusPainted(false);
        btnHabitat.addActionListener(e -> activarSeleccionHabitat());
        panelHabitat.add(lblCoordenadas);
        panelHabitat.add(btnHabitat);
        campos.add(panelHabitat, g);

        g.gridx = 0; g.gridy = 5; g.gridwidth = 2;
        JButton btnCrear = new JButton("🐾  Crear especie");
        btnCrear.setBackground(new Color(80, 60, 130));
        btnCrear.setForeground(Color.WHITE);
        btnCrear.setFont(new Font("Arial", Font.BOLD, 13));
        btnCrear.setFocusPainted(false);
        btnCrear.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        btnCrear.addActionListener(e -> crearEspecie());
        campos.add(btnCrear, g);

        panel.add(campos, BorderLayout.NORTH);
        return panel;
    }

    // ── Tab Manada ────────────────────────────────────────────────────────
    private JPanel construirPanelManada() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(new Color(40, 40, 55));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setBackground(new Color(40, 40, 55));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 10, 8, 10);
        g.anchor = GridBagConstraints.WEST;
        g.fill   = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; campos.add(etiqueta("Nombre:"), g);
        g.gridx = 1; campoNombreManada = new JTextField(14);
        estilizarCampo(campoNombreManada); campos.add(campoNombreManada, g);

        g.gridx = 0; g.gridy = 1; campos.add(etiqueta("Lema:"), g);
        g.gridx = 1; campoLema = new JTextField(14);
        estilizarCampo(campoLema); campos.add(campoLema, g);

        g.gridx = 0; g.gridy = 2; campos.add(etiqueta("Especie:"), g);
        g.gridx = 1;
        comboEspecieManada = new JComboBox<>(
            obtenerEspeciesDisponibles().toArray(new String[0]));
        estilizarCombo(comboEspecieManada);
        campos.add(comboEspecieManada, g);

        g.gridx = 0; g.gridy = 3; campos.add(etiqueta("Cupo máximo:"), g);
        g.gridx = 1; campoCupo = new JTextField("20", 14);
        estilizarCampo(campoCupo); campos.add(campoCupo, g);

        g.gridx = 0; g.gridy = 4; campos.add(etiqueta("IAA mínimo:"), g);
        g.gridx = 1; campoIAAMin = new JTextField("0", 14);
        estilizarCampo(campoIAAMin); campos.add(campoIAAMin, g);

        g.gridx = 0; g.gridy = 5; campos.add(etiqueta("IAA máximo:"), g);
        g.gridx = 1; campoIAAMax = new JTextField("40", 14);
        estilizarCampo(campoIAAMax); campos.add(campoIAAMax, g);

        g.gridx = 0; g.gridy = 6; campos.add(etiqueta("Territorio:"), g);
        g.gridx = 1;
        JPanel panelTerr = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelTerr.setBackground(new Color(40, 40, 55));
        lblTerritorioManada = new JLabel(territorioManada);
        lblTerritorioManada.setForeground(new Color(150, 210, 255));
        JButton btnTerr = new JButton("🗺 Definir en mapa");
        btnTerr.setBackground(new Color(60, 80, 130));
        btnTerr.setForeground(Color.WHITE);
        btnTerr.setFocusPainted(false);
        btnTerr.addActionListener(e -> activarSeleccionTerritorioManada());
        panelTerr.add(lblTerritorioManada);
        panelTerr.add(btnTerr);
        campos.add(panelTerr, g);

        g.gridx = 0; g.gridy = 7; g.gridwidth = 2;
        JButton btnCrear = new JButton("🏕  Crear manada");
        btnCrear.setBackground(new Color(60, 100, 60));
        btnCrear.setForeground(Color.WHITE);
        btnCrear.setFont(new Font("Arial", Font.BOLD, 13));
        btnCrear.setFocusPainted(false);
        btnCrear.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        btnCrear.addActionListener(e -> crearManada());
        campos.add(btnCrear, g);

        panel.add(campos, BorderLayout.NORTH);
        return panel;
    }

    // ── Lógica: crear ciudadano ───────────────────────────────────────────
    private void crearCiudadano() {
        String nombre   = campoNombre.getText().trim();
        String apellido = campoApellido.getText().trim();
        String especie  = (String) comboEspecie.getSelectedItem();

        if (nombre.isEmpty() || apellido.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Nombre y apellido son obligatorios.", "Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int    id    = calcularNuevoId();
        String fecha = java.time.LocalDate.now().toString();

        CiudadanoTherian nuevo = instanciar(especie, nombre, apellido, id, fecha);
        nuevo.setCreadoPorUsuario(true);
        nuevo.setPuntuacionManada(50);
        nuevo.setIAA(10.0 + new Random().nextInt(16));

        if (reloj.getManadas() != null) {
            for (Manada m : reloj.getManadas()) {
                if (m instanceof ManadaDePaso) continue;
                if (m.getEspecie() != null
                        && m.getEspecie().equals(especie)
                        && m.aceptaIAA(nuevo.getIAA())
                        && !m.estaLlena()) {
                    try {
                        AfiliacionManada af = new AfiliacionManada(
                            m.getNombreManada(), fecha, "Omega", 50, null);
                        nuevo.AgregarManada(af);
                        m.agregarMiembro(nuevo, nuevo.getIAA());
                    } catch (TherianException ignored) {}
                    break;
                }
            }
        }

        if (reloj.getCiudadanos() != null) reloj.getCiudadanos().add(nuevo);
        CiudadanoServicio.guardarCiudadanos(reloj.getCiudadanos());
        ManadaServicio.guardarManadas(reloj.getManadas());

        campoNombre.setText("");
        campoApellido.setText("");

        areaResultado.setText("✔ Ciudadano creado:\n"
            + "  Nombre:  " + nombre + " " + apellido + "\n"
            + "  Especie: " + especie + "\n"
            + "  ID:      " + id + "\n"
            + "  IAA:     " + String.format("%.1f", nuevo.getIAA()) + "\n"
            + "\nDoble clic en la lista para ver su evolución.");

        cargarTablaPersonal();
        reloj.reanudar();
    }

    // ── Lógica: crear especie ─────────────────────────────────────────────
    private void crearEspecie() {
        String nombreEsp = campoNombreEspecie.getText().trim();
        String sonido    = campoSonido.getText().trim();
        String caract    = campoCaracteristicas.getText().trim();
        boolean esPred   = checkPredadora.isSelected();

        if (nombreEsp.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El nombre de la especie es obligatorio.",
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int    id    = calcularNuevoId();
        String fecha = java.time.LocalDate.now().toString();

        EspeciePersonalizada nuevo = new EspeciePersonalizada(
            "Explorador", "Therian", id, fecha, "Activo",
            nombreEsp, esPred, sonido, habitatSeleccionado, caract);
        nuevo.setCreadoPorUsuario(true);
        nuevo.setPuntuacionManada(50);
        nuevo.setIAA(15.0 + new Random().nextInt(16));

        if (reloj.getCiudadanos() != null) reloj.getCiudadanos().add(nuevo);
        CiudadanoServicio.guardarCiudadanos(reloj.getCiudadanos());

        JOptionPane.showMessageDialog(this,
            "✔ Especie creada: " + nombreEsp + "\n"
            + "  Predadora: " + (esPred ? "Sí" : "No") + "\n"
            + "  Hábitat: " + habitatSeleccionado + "\n"
            + "  Se creó un ciudadano de prueba (ID: " + id + ")",
            "Especie creada", JOptionPane.INFORMATION_MESSAGE);

        campoNombreEspecie.setText("");
        campoSonido.setText("");
        campoCaracteristicas.setText("");
        habitatSeleccionado = "x:540,y:540,r:80";
        lblCoordenadas.setText(habitatSeleccionado);
        reloj.reanudar();
    }

    // ── Lógica: crear manada ──────────────────────────────────────────────
    private void crearManada() {
        String nombre  = campoNombreManada.getText().trim();
        String lema    = campoLema.getText().trim();
        String especie = (String) comboEspecieManada.getSelectedItem();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El nombre de la manada es obligatorio.",
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int    cupo   = parsearInt(campoCupo.getText(),     20);
        double iaaMin = parsearDouble(campoIAAMin.getText(), 0);
        double iaaMax = parsearDouble(campoIAAMax.getText(), 40);

        Manada nuevaManada = new Manada(
            nombre, especie,
            "Manada personalizada de " + especie,
            cupo, iaaMin, iaaMax,
            lema.isEmpty() ? "Unidos somos más fuertes" : lema,
            territorioManada);

        if (reloj.getManadas() != null) reloj.getManadas().add(nuevaManada);
        ManadaServicio.guardarManadas(reloj.getManadas());

        JOptionPane.showMessageDialog(this,
            "✔ Manada creada: " + nombre + "\n"
            + "  Especie: " + especie + "\n"
            + "  Cupo: " + cupo + "\n"
            + "  IAA: " + iaaMin + " - " + iaaMax + "\n"
            + "  Territorio: " + territorioManada,
            "Manada creada", JOptionPane.INFORMATION_MESSAGE);

        campoNombreManada.setText("");
        campoLema.setText("");
        territorioManada = "x:540,y:540,r:80";
        lblTerritorioManada.setText(territorioManada);
        reloj.reanudar();
    }

    // ── Lógica: cargar tabla personal ─────────────────────────────────────
    private void cargarTablaPersonal() {
        try {
            String contenido = new String(Files.readAllBytes(
                Paths.get("Archivos/ciudadanos.json")));
            JsonArray todos = JsonParser.parseString(contenido).getAsJsonArray();

            String[] columnas = {"id","nombre","apellido","especie","iaa","rol","historialIAA"};
            DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };

            for (JsonElement el : todos) {
                JsonObject c = el.getAsJsonObject();
                JsonElement cpu = c.get("creadoPorUsuario");
                if (cpu == null || !cpu.getAsBoolean()) continue;

                String histIAA = "";
                JsonElement hist = c.get("historialIAA");
                if (hist != null && hist.isJsonArray()) {
                    JsonArray arr = hist.getAsJsonArray();
                    int desde = Math.max(0, arr.size() - 3);
                    StringBuilder sb = new StringBuilder();
                    for (int i = desde; i < arr.size(); i++) {
                        JsonObject p = arr.get(i).getAsJsonObject();
                        if (sb.length() > 0) sb.append(" | ");
                        sb.append(String.format("%.1f", p.get("iaa").getAsDouble()));
                    }
                    histIAA = sb.toString();
                }

                modelo.addRow(new Object[]{
                    c.has("id")       ? c.get("id").getAsInt()              : "?",
                    c.has("nombre")   ? c.get("nombre").getAsString()       : "",
                    c.has("apellido") ? c.get("apellido").getAsString()     : "",
                    c.has("especie")  ? c.get("especie").getAsString()      : "",
                    c.has("iaa")      ? String.format("%.1f", c.get("iaa").getAsDouble()) : "",
                    c.has("rol")      ? c.get("rol").getAsString()          : "",
                    histIAA
                });
            }
            tabla.setModel(modelo);
            tabla.setRowHeight(26);
        } catch (Exception e) {
            if (areaResultado != null)
                areaResultado.setText("Error al cargar: " + e.getMessage());
        }
    }

    // ── Selección de hábitat/territorio en el mapa ────────────────────────
    private void activarSeleccionHabitat() {
        JOptionPane.showMessageDialog(this,
            "Haz clic en el mapa para definir el hábitat.\n"
            + "El punto seleccionado se usará como centro.",
            "Seleccionar hábitat", JOptionPane.INFORMATION_MESSAGE);

        Container parent = SwingUtilities.getWindowAncestor(this);
        if (parent instanceof MenuFrame) {
            MenuFrame menu = (MenuFrame) parent;
            Opcion1 op1 = new Opcion1(null);
            op1.activarModoSeleccionHabitat(coords -> {
                habitatSeleccionado = coords;
                lblCoordenadas.setText(coords);
                menu.MPAdd(this);
            });
            menu.MPAdd(op1);
        }
    }

    private void activarSeleccionTerritorioManada() {
        JOptionPane.showMessageDialog(this,
            "Haz clic en el mapa para definir el territorio de la manada.",
            "Seleccionar territorio", JOptionPane.INFORMATION_MESSAGE);

        Container parent = SwingUtilities.getWindowAncestor(this);
        if (parent instanceof MenuFrame) {
            MenuFrame menu = (MenuFrame) parent;
            Opcion1 op1 = new Opcion1(null);
            op1.activarModoSeleccionHabitat(coords -> {
                territorioManada = coords;
                lblTerritorioManada.setText(coords);
                menu.MPAdd(this);
            });
            menu.MPAdd(op1);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private int calcularNuevoId() {
        if (reloj.getCiudadanos() != null && !reloj.getCiudadanos().isEmpty())
            return reloj.getCiudadanos().size() + 1;
        try {
            String contenido = new String(Files.readAllBytes(
                Paths.get("Archivos/ciudadanos.json")));
            return JsonParser.parseString(contenido).getAsJsonArray().size() + 1;
        } catch (Exception e) { return 1; }
    }

    private CiudadanoTherian instanciar(String especie, String nombre,
                                         String apellido, int id, String fecha) {
        return switch (especie) {
            case "Lobo"   -> new Lobo(nombre, apellido, id, fecha, "Activo");
            case "Leon"   -> new Leon(nombre, apellido, id, fecha, "Activo");
            case "Ciervo" -> new Ciervo(nombre, apellido, id, fecha, "Activo");
            case "Alce"   -> new Alce(nombre, apellido, id, fecha, "Activo");
            case "Tigre"  -> new Tigre(nombre, apellido, id, fecha, "Activo");
            case "Halcon" -> new Halcon(nombre, apellido, id, fecha, "Activo");
            case "Orca"   -> new Orca(nombre, apellido, id, fecha, "Activo");
            case "Cebra"  -> new Cebra(nombre, apellido, id, fecha, "Activo");
            case "Foca"   -> new Foca(nombre, apellido, id, fecha, "Activo");
            default       -> new Paloma(nombre, apellido, id, fecha, "Activo");
        };
    }

    private List<String> obtenerEspeciesDisponibles() {
        List<String> lista = new ArrayList<>(Arrays.asList(
            "Lobo","Leon","Ciervo","Alce","Tigre",
            "Halcon","Orca","Cebra","Foca","Paloma"));
        try {
            String contenido = new String(Files.readAllBytes(
                Paths.get("Archivos/ciudadanos.json")));
            JsonArray arr = JsonParser.parseString(contenido).getAsJsonArray();
            for (JsonElement el : arr) {
                String esp = el.getAsJsonObject().get("especie").getAsString();
                if (!lista.contains(esp)) lista.add(esp);
            }
        } catch (Exception ignored) {}
        return lista;
    }

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(FG_GRAY);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        return l;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setBackground(new Color(25, 25, 35));
        campo.setForeground(FG_WHITE);
        campo.setCaretColor(FG_WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 120)),
            BorderFactory.createEmptyBorder(3, 6, 3, 6)));
    }

    private void estilizarCombo(JComboBox<String> combo) {
        combo.setBackground(BG_PANEL);
        combo.setForeground(FG_WHITE);
        combo.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    private TitledBorder borde(String titulo) {
        TitledBorder b = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 120)), titulo);
        b.setTitleColor(FG_GRAY);
        return b;
    }

    private int parsearInt(String texto, int defecto) {
        try { return Integer.parseInt(texto.trim()); }
        catch (Exception e) { return defecto; }
    }

    private double parsearDouble(String texto, double defecto) {
        try { return Double.parseDouble(texto.trim()); }
        catch (Exception e) { return defecto; }
    }
}