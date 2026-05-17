package GUI;

import Especies.*;
import Manadas.Manada;
import Servicios.CiudadanoServicio;
import Servicios.ManadaServicio;
import Servicios.RitualServicio;
import Simulacion.Reloj;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class Opcion3 extends JPanel {

    private final Reloj reloj;

    // ── Tab Actualizar ─────────────────────────────────────────────────────
    private JTextField    campoBusqueda;
    private JLabel        lblNombre, lblEspecie, lblIAA, lblRol, lblManadaActual;
    private JComboBox<String> comboEstado, comboEspecie, comboRol, comboManada;
    private JButton       btnBuscar, btnGuardar;
    private JTextArea     areaInfo;
    private Map<String, Object> ciudadanoActual = null;

    // ── Tab Ritual ─────────────────────────────────────────────────────────
    private JTextField    campoIdRitual;
    private JComboBox<String> comboTipoRitual;
    private JSlider       sliderIntensidad;
    private JLabel        lblIntensidad;
    private JTextArea     areaInfoRitual;

    private static final String[] ESTADOS = {"Activo", "Suspendido", "En Revision"};
    private static final String[] ROLES   = {"Alfa", "Beta", "Omega", "Observador"};
    private static final String[] ESPECIES = {
        "Lobo","Leon","Ciervo","Alce","Tigre",
        "Halcon","Orca","Cebra","Foca","Paloma"
    };

    private static final Color BG_DARK  = new Color(30, 30, 40);
    private static final Color BG_PANEL = new Color(40, 40, 55);
    private static final Color FG_GRAY  = Color.LIGHT_GRAY;
    private static final Color FG_WHITE = Color.WHITE;

    // ── Constructor ───────────────────────────────────────────────────────
    public Opcion3(Reloj reloj) {
        this.reloj = reloj;
        setLayout(new BorderLayout());
        setBackground(BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(BG_PANEL);
        tabs.setForeground(FG_WHITE);
        tabs.setFont(new Font("Arial", Font.BOLD, 13));

        tabs.addTab("✏ Actualizar ciudadano", construirPanelActualizar());
        tabs.addTab("🎭 Registrar ritual",     construirPanelRitual());

        add(tabs, BorderLayout.CENTER);
    }

    // ══════════════════════════════════════════════════════════════════════
    // TAB 1 — ACTUALIZAR CIUDADANO
    // ══════════════════════════════════════════════════════════════════════

    private JPanel construirPanelActualizar() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(construirPanelBusqueda(),   BorderLayout.NORTH);
        panel.add(construirPanelFormulario(), BorderLayout.CENTER);
        panel.add(construirPanelInfo(),       BorderLayout.SOUTH);
        return panel;
    }

    private JPanel construirPanelBusqueda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(BG_PANEL);
        panel.setBorder(tituloBorde("Buscar ciudadano"));

        JLabel lbl = new JLabel("ID ciudadano:");
        lbl.setForeground(FG_GRAY);

        campoBusqueda = new JTextField(10);
        btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(new Color(60, 100, 160));
        btnBuscar.setForeground(FG_WHITE);
        btnBuscar.addActionListener(e -> buscarCiudadano());

        panel.add(lbl);
        panel.add(campoBusqueda);
        panel.add(btnBuscar);
        return panel;
    }

    private JPanel construirPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_PANEL);
        panel.setBorder(tituloBorde("Datos del ciudadano"));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 10, 7, 10);
        g.anchor = GridBagConstraints.WEST;
        g.fill   = GridBagConstraints.HORIZONTAL;

        // ── Solo lectura ──────────────────────────────────────────────────
        g.gridx = 0; g.gridy = 0; panel.add(etiqueta("Nombre:"), g);
        g.gridx = 1; lblNombre = new JLabel("—");
        lblNombre.setForeground(FG_WHITE); panel.add(lblNombre, g);

        g.gridx = 0; g.gridy = 1; panel.add(etiqueta("Especie actual:"), g);
        g.gridx = 1; lblEspecie = new JLabel("—");
        lblEspecie.setForeground(new Color(150, 210, 255)); panel.add(lblEspecie, g);

        g.gridx = 0; g.gridy = 2; panel.add(etiqueta("IAA actual:"), g);
        g.gridx = 1; lblIAA = new JLabel("—");
        lblIAA.setForeground(new Color(120, 220, 120)); panel.add(lblIAA, g);

        g.gridx = 0; g.gridy = 3; panel.add(etiqueta("Rol actual:"), g);
        g.gridx = 1; lblRol = new JLabel("—");
        lblRol.setForeground(new Color(255, 200, 100)); panel.add(lblRol, g);

        g.gridx = 0; g.gridy = 4; panel.add(etiqueta("Manada actual:"), g);
        g.gridx = 1; lblManadaActual = new JLabel("—");
        lblManadaActual.setForeground(new Color(200, 180, 255)); panel.add(lblManadaActual, g);

        // ── Editables ─────────────────────────────────────────────────────
        g.gridx = 0; g.gridy = 5; panel.add(etiqueta("Nuevo estado:"), g);
        g.gridx = 1; comboEstado = estilizarCombo(new JComboBox<>(ESTADOS));
        panel.add(comboEstado, g);

        g.gridx = 0; g.gridy = 6; panel.add(etiqueta("Nueva especie:"), g);
        g.gridx = 1; comboEspecie = estilizarCombo(new JComboBox<>(ESPECIES));
        comboEspecie.addActionListener(e -> actualizarManadas());
        panel.add(comboEspecie, g);

        g.gridx = 0; g.gridy = 7; panel.add(etiqueta("Nueva manada:"), g);
        g.gridx = 1; comboManada = estilizarCombo(new JComboBox<>());
        panel.add(comboManada, g);

        g.gridx = 0; g.gridy = 8; panel.add(etiqueta("Nuevo rol:"), g);
        g.gridx = 1; comboRol = estilizarCombo(new JComboBox<>(ROLES));
        panel.add(comboRol, g);

        // ── Botón guardar ─────────────────────────────────────────────────
        g.gridx = 0; g.gridy = 9; g.gridwidth = 2;
        btnGuardar = new JButton("💾  Guardar cambios");
        btnGuardar.setBackground(new Color(60, 140, 60));
        btnGuardar.setForeground(FG_WHITE);
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 13));
        btnGuardar.setEnabled(false);
        btnGuardar.addActionListener(e -> guardarCambios());
        panel.add(btnGuardar, g);

        return panel;
    }

    private JPanel construirPanelInfo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_PANEL);
        panel.setBorder(tituloBorde("Resultado"));
        panel.setPreferredSize(new Dimension(0, 100));

        areaInfo = new JTextArea(4, 40);
        areaInfo.setEditable(false);
        areaInfo.setBackground(new Color(25, 25, 35));
        areaInfo.setForeground(new Color(180, 230, 180));
        areaInfo.setFont(new Font("Monospaced", Font.PLAIN, 12));
        panel.add(new JScrollPane(areaInfo), BorderLayout.CENTER);
        return panel;
    }

    // ── Lógica: buscar ciudadano ──────────────────────────────────────────
    private void buscarCiudadano() {
        String idTexto = campoBusqueda.getText().trim();
        if (idTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa un ID válido.",
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int idBuscado = Integer.parseInt(idTexto);
            ciudadanoActual = null;
            for (Map<String, Object> c : CiudadanoServicio.cargarCiudadanos()) {
                if (((Number) c.get("id")).intValue() == idBuscado) {
                    ciudadanoActual = c;
                    break;
                }
            }
            if (ciudadanoActual == null) {
                areaInfo.setText("No se encontró ciudadano con ID: " + idTexto);
                limpiarFormulario();
                return;
            }

            // Rellenar campos
            lblNombre.setText(ciudadanoActual.get("nombre") + " " + ciudadanoActual.get("apellido"));
            lblEspecie.setText((String) ciudadanoActual.get("especie"));
            lblIAA.setText(String.format("%.2f", ((Number) ciudadanoActual.get("iaa")).doubleValue()));
            lblRol.setText((String) ciudadanoActual.get("rol"));

            // Manada actual desde afiliaciones
            String manadaActual = obtenerManadaActual(ciudadanoActual);
            lblManadaActual.setText(manadaActual);

            comboEstado.setSelectedItem(ciudadanoActual.get("estadoCiudadania"));
            comboEspecie.setSelectedItem(ciudadanoActual.get("especie"));
            comboRol.setSelectedItem(ciudadanoActual.get("rol"));

            actualizarManadas();
            comboManada.setSelectedItem(manadaActual);

            btnGuardar.setEnabled(true);
            areaInfo.setText("Ciudadano encontrado. Modifica los campos y presiona Guardar.");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El ID debe ser un número.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Poblar comboManada según especie seleccionada
    private void actualizarManadas() {
        String especie = (String) comboEspecie.getSelectedItem();
        comboManada.removeAllItems();
        comboManada.addItem("— Sin cambio —");

        List<Manada> fuente = (reloj != null && reloj.getManadas() != null)
            ? reloj.getManadas()
            : cargarManadasDesdeJSON();

        for (Manada m : fuente) {
            if (especie != null && especie.equals(m.getEspecie())
                    && !(m instanceof Manadas.ManadaDePaso)) {
                comboManada.addItem(m.getNombreManada());
            }
        }
    }

    private List<Manada> cargarManadasDesdeJSON() {
        List<Manada> lista = new ArrayList<>();
        try {
            for (Map<String, Object> m : ManadaServicio.cargarManadas()) {
                String nombre  = (String) m.get("nombre");
                String especie = (String) m.get("especie");
                String lema    = (String) m.getOrDefault("lema", "");
                String terr    = (String) m.getOrDefault("territorio", "x:540,y:540,r:80");
                int    cupo    = ((Number) m.getOrDefault("cupoMaximo", 20)).intValue();
                double iMin    = ((Number) m.getOrDefault("iaaMinimo", 0)).doubleValue();
                double iMax    = ((Number) m.getOrDefault("iaaMaximo", 100)).doubleValue();
                if (nombre != null && especie != null)
                    lista.add(new Manada(nombre, especie, "", cupo, iMin, iMax, lema, terr));
            }
        } catch (Exception ignored) {}
        return lista;
    }

    // ── Lógica: guardar cambios ───────────────────────────────────────────
    private void guardarCambios() {
        if (ciudadanoActual == null) return;

        String nuevaEspecie    = (String) comboEspecie.getSelectedItem();
        String nuevoEstado     = (String) comboEstado.getSelectedItem();
        String nuevoRol        = (String) comboRol.getSelectedItem();
        String nuevaManada     = (String) comboManada.getSelectedItem();
        String especieAnterior = (String) ciudadanoActual.get("especie");
        int    idBuscado       = ((Number) ciudadanoActual.get("id")).intValue();
        boolean cambioEspecie  = !nuevaEspecie.equals(especieAnterior);
        boolean cambioManada   = nuevaManada != null
                              && !nuevaManada.equals("— Sin cambio —")
                              && !nuevaManada.equals(obtenerManadaActual(ciudadanoActual));

        CiudadanoTherian ciudadanoReal = null;
        if (reloj != null && reloj.getCiudadanos() != null) {
            for (CiudadanoTherian c : reloj.getCiudadanos()) {
                if (c.getId() == idBuscado) { ciudadanoReal = c; break; }
            }
        }

        if (ciudadanoReal != null) {
            ciudadanoReal.setEstadoCiudadania(nuevoEstado);
            ciudadanoReal.setRol(nuevoRol);

            if (cambioEspecie) {
                String[] att = obtenerAtributosEspecie(nuevaEspecie);
                ciudadanoReal.cambiarEspecie(
                    nuevaEspecie, esPredadora(nuevaEspecie),
                    att[0], att[1], att[2], reloj.getManadas());
            } else if (cambioManada) {
                cambiarManadaDirecta(ciudadanoReal, nuevaManada);
            }

            CiudadanoServicio.guardarCiudadanos(reloj.getCiudadanos());
            ManadaServicio.guardarManadas(reloj.getManadas());

        } else {
            // Fallback: modificar JSON directamente
            try {
                List<Map<String, Object>> todos = CiudadanoServicio.cargarCiudadanos();
                for (Map<String, Object> c : todos) {
                    if (((Number) c.get("id")).intValue() == idBuscado) {
                        c.put("estadoCiudadania", nuevoEstado);
                        c.put("rol",              nuevoRol);
                        c.put("especie",          nuevaEspecie);
                        c.put("esPredador",       esPredadora(nuevaEspecie));
                        break;
                    }
                }
                CiudadanoServicio.guardarCiudadanosRaw(todos);
            } catch (Exception ex) {
                areaInfo.setText("Error al guardar: " + ex.getMessage());
                return;
            }
        }

        lblEspecie.setText(nuevaEspecie);
        lblRol.setText(nuevoRol);
        if (cambioManada) lblManadaActual.setText(nuevaManada);
        reloj.reanudar();

        areaInfo.setText("✔ Cambios guardados.\n"
            + "  Especie: " + especieAnterior
            + (cambioEspecie ? " → " + nuevaEspecie : " (sin cambio)") + "\n"
            + "  Manada: " + (cambioManada ? "→ " + nuevaManada : "(sin cambio)") + "\n"
            + "  Estado: " + nuevoEstado + " | Rol: " + nuevoRol);
    }

    // Reubicar ciudadano en una manada específica sin transición
    private void cambiarManadaDirecta(CiudadanoTherian c, String nombreManada) {
        // Salir de manada actual
        if (reloj.getManadas() != null) {
            for (Manada m : reloj.getManadas()) {
                if (m.getMiembros().contains(c)) {
                    m.getMiembros().remove(c);
                    break;
                }
            }
        }
        // Cerrar afiliación activa
        String fecha = java.time.LocalDate.now().toString();
        for (AfiliacionManada a : c.getManadas()) {
            if (a.estaActivo()) a.setFechaSalida(fecha);
        }
        // Entrar a nueva manada
        if (reloj.getManadas() != null) {
            for (Manada m : reloj.getManadas()) {
                if (m.getNombreManada().equals(nombreManada) && !m.estaLlena()) {
                    try {
                        AfiliacionManada af = new AfiliacionManada(
                            nombreManada, fecha, c.getRol(), c.getPuntuacionManada(), null);
                        c.AgregarManada(af);
                        m.agregarMiembro(c, c.getIAA());
                    } catch (TherianException ignored) {}
                    break;
                }
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // TAB 2 — REGISTRAR RITUAL
    // ══════════════════════════════════════════════════════════════════════

    private JPanel construirPanelRitual() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(BG_PANEL);
        formulario.setBorder(tituloBorde("Nuevo ritual"));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 10, 8, 10);
        g.anchor = GridBagConstraints.WEST;
        g.fill   = GridBagConstraints.HORIZONTAL;

        // ID ciudadano
        g.gridx = 0; g.gridy = 0; formulario.add(etiqueta("ID ciudadano:"), g);
        g.gridx = 1;
        campoIdRitual = new JTextField(12);
        estilizarTextField(campoIdRitual);
        JButton btnCargarEspecie = new JButton("↺");
        btnCargarEspecie.setBackground(new Color(60, 100, 160));
        btnCargarEspecie.setForeground(FG_WHITE);
        btnCargarEspecie.setToolTipText("Cargar tipos de ritual según especie");
        btnCargarEspecie.addActionListener(e -> cargarTiposRitual());
        JPanel panelId = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        panelId.setBackground(BG_PANEL);
        panelId.add(campoIdRitual);
        panelId.add(btnCargarEspecie);
        formulario.add(panelId, g);

        // Tipo de ritual
        g.gridx = 0; g.gridy = 1; formulario.add(etiqueta("Tipo de ritual:"), g);
        g.gridx = 1;
        comboTipoRitual = estilizarCombo(new JComboBox<>());
        formulario.add(comboTipoRitual, g);

        // Intensidad
        g.gridx = 0; g.gridy = 2; formulario.add(etiqueta("Intensidad:"), g);
        g.gridx = 1;
        JPanel panelSlider = new JPanel(new BorderLayout(6, 0));
        panelSlider.setBackground(BG_PANEL);
        sliderIntensidad = new JSlider(1, 10, 5);
        sliderIntensidad.setBackground(BG_PANEL);
        sliderIntensidad.setPaintTicks(true);
        sliderIntensidad.setMajorTickSpacing(1);
        lblIntensidad = new JLabel("5");
        lblIntensidad.setForeground(new Color(120, 220, 120));
        lblIntensidad.setFont(new Font("Arial", Font.BOLD, 14));
        sliderIntensidad.addChangeListener(e ->
            lblIntensidad.setText(String.valueOf(sliderIntensidad.getValue())));
        panelSlider.add(sliderIntensidad, BorderLayout.CENTER);
        panelSlider.add(lblIntensidad,    BorderLayout.EAST);
        formulario.add(panelSlider, g);

        // Botón registrar
        g.gridx = 0; g.gridy = 3; g.gridwidth = 2;
        JButton btnRegistrar = new JButton("🎭  Registrar ritual");
        btnRegistrar.setBackground(new Color(100, 60, 140));
        btnRegistrar.setForeground(FG_WHITE);
        btnRegistrar.setFont(new Font("Arial", Font.BOLD, 13));
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        btnRegistrar.addActionListener(e -> registrarRitual());
        formulario.add(btnRegistrar, g);

        // Área de resultado
        areaInfoRitual = new JTextArea(5, 40);
        areaInfoRitual.setEditable(false);
        areaInfoRitual.setBackground(new Color(25, 25, 35));
        areaInfoRitual.setForeground(new Color(180, 230, 180));
        areaInfoRitual.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JPanel panelRes = new JPanel(new BorderLayout());
        panelRes.setBackground(BG_PANEL);
        panelRes.setBorder(tituloBorde("Resultado"));
        panelRes.add(new JScrollPane(areaInfoRitual), BorderLayout.CENTER);

        panel.add(formulario, BorderLayout.CENTER);
        panel.add(panelRes,   BorderLayout.SOUTH);
        return panel;
    }

    // Cargar tipos de ritual según especie del ciudadano
    private void cargarTiposRitual() {
        String idTexto = campoIdRitual.getText().trim();
        if (idTexto.isEmpty()) {
            areaInfoRitual.setText("Ingresa un ID primero.");
            return;
        }
        try {
            int id = Integer.parseInt(idTexto);
            String especie = null;

            if (reloj != null && reloj.getCiudadanos() != null) {
                for (CiudadanoTherian c : reloj.getCiudadanos()) {
                    if (c.getId() == id) { especie = c.getEspecieActual(); break; }
                }
            }
            if (especie == null) {
                for (Map<String, Object> c : CiudadanoServicio.cargarCiudadanos()) {
                    if (((Number) c.get("id")).intValue() == id) {
                        especie = (String) c.get("especie");
                        break;
                    }
                }
            }
            if (especie == null) {
                areaInfoRitual.setText("No se encontró ciudadano con ID: " + id);
                return;
            }

            comboTipoRitual.removeAllItems();
            for (Ritual.TipoRitual t : Ritual.tiposPorEspecie(especie)) {
                comboTipoRitual.addItem(t.name());
            }
            areaInfoRitual.setText("Especie: " + especie
                + "\nSelecciona el tipo de ritual y la intensidad.");

        } catch (NumberFormatException ex) {
            areaInfoRitual.setText("El ID debe ser un número.");
        }
    }

    // Registrar el ritual
    private void registrarRitual() {
        String idTexto = campoIdRitual.getText().trim();
        if (idTexto.isEmpty() || comboTipoRitual.getItemCount() == 0) {
            areaInfoRitual.setText("Carga primero el ciudadano con el botón ↺.");
            return;
        }
        try {
            int    id         = Integer.parseInt(idTexto);
            String tipoStr    = (String) comboTipoRitual.getSelectedItem();
            double intensidad = sliderIntensidad.getValue();
            String fecha      = java.time.LocalDate.now().toString();

            Ritual.TipoRitual tipo = Ritual.TipoRitual.valueOf(tipoStr);

            CiudadanoTherian ciudadano = null;
            if (reloj != null && reloj.getCiudadanos() != null) {
                for (CiudadanoTherian c : reloj.getCiudadanos()) {
                    if (c.getId() == id) { ciudadano = c; break; }
                }
            }
            if (ciudadano == null) {
                areaInfoRitual.setText("Ciudadano no encontrado en la simulación activa.");
                return;
            }

            String nombreManada = "Sin manada";
            if (reloj.getManadas() != null) {
                for (Manada m : reloj.getManadas()) {
                    if (m.getMiembros().contains(ciudadano)) {
                        nombreManada = m.getNombreManada();
                        break;
                    }
                }
            }

            Ritual ritual = new Ritual(
                "Ritual-Manual-" + id + "-" + fecha,
                tipo, fecha, 60,
                ciudadano.getEspecieActual(),
                nombreManada, intensidad);

            // agregarParticipante ya registra el ritual en el ciudadano internamente.
            // NO llamar ciudadano.getRituales().add() después — sería duplicado.
            ritual.agregarParticipante(ciudadano);
            ritual.setAsistio(true);

            // Subir IAA levemente
            ciudadano.setIAA(Math.min(100, ciudadano.getIAA() + intensidad * 0.5));

            // Guardar en rituales.json para que Opcion4 lo lea
            if (reloj.getRituales() != null) {
                reloj.getRituales().add(ritual);
                RitualServicio.guardarRituales(reloj.getRituales());
            }

            CiudadanoServicio.guardarCiudadanos(reloj.getCiudadanos());
            reloj.reanudar();

            areaInfoRitual.setText("✔ Ritual registrado:\n"
                + "  Ciudadano:  " + ciudadano.getNombre() + " " + ciudadano.getApellido() + "\n"
                + "  Tipo:       " + tipoStr + "\n"
                + "  Intensidad: " + (int) intensidad + "/10\n"
                + "  Manada:     " + nombreManada + "\n"
                + "  IAA nuevo:  " + String.format("%.1f", ciudadano.getIAA()));

        } catch (Exception ex) {
            areaInfoRitual.setText("Error al registrar: " + ex.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private String obtenerManadaActual(Map<String, Object> ciudadano) {
        try {
            List<Map<String, Object>> afils =
                (List<Map<String, Object>>) ciudadano.get("afiliaciones");
            if (afils != null) {
                for (Map<String, Object> a : afils) {
                    if (a.get("fechaSalida") == null) {
                        String nombre = (String) a.get("nombreManada");
                        if (nombre != null && !nombre.equals("Manada de Paso"))
                            return nombre;
                    }
                }
            }
        } catch (Exception ignored) {}
        return "Sin manada";
    }

    private boolean esPredadora(String e) {
        return e.equals("Lobo") || e.equals("Leon") ||
               e.equals("Tigre") || e.equals("Halcon") || e.equals("Orca");
    }

    private String[] obtenerAtributosEspecie(String especie) {
        switch (especie) {
            case "Lobo":   return new String[]{"Aullido profundo",    "x:520,y:870,r:100", "Territorial, social, estratégico"};
            case "Leon":   return new String[]{"Rugido grave",        "x:800,y:600,r:150", "Dominante, protector, noble"};
            case "Ciervo": return new String[]{"Bramido suave",       "x:500,y:500,r:100", "Sensible, ágil, intuitivo"};
            case "Alce":   return new String[]{"Bramido resonante",   "x:350,y:285,r:100", "Majestuoso, solitario, territorial"};
            case "Tigre":  return new String[]{"Gruñido sibilante",   "x:295,y:500,r:100", "Solitario, determinado, sigiloso"};
            case "Halcon": return new String[]{"Chillido penetrante", "x:690,y:380,r:120", "Preciso, independiente, observador"};
            case "Orca":   return new String[]{"Clicks y silbidos",   "x:110,y:890,r:160", "Inteligente, familiar, organizada"};
            case "Cebra":  return new String[]{"Ladrido corto",       "x:365,y:775,r:100", "Resiliente, alerta, gregaria"};
            case "Foca":   return new String[]{"Gruñidos y ladridos", "x:530,y:190,r:150", "Juguetona, curiosa, social"};
            default:       return new String[]{"Arrullo suave",       "x:740,y:800,r:100", "Pacífica, empática, leal"};
        }
    }

    private void limpiarFormulario() {
        lblNombre.setText("—"); lblEspecie.setText("—");
        lblIAA.setText("—");    lblRol.setText("—");
        lblManadaActual.setText("—");
        btnGuardar.setEnabled(false);
    }

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(FG_GRAY);
        return l;
    }

    private <T> JComboBox<T> estilizarCombo(JComboBox<T> c) {
        c.setBackground(BG_PANEL);
        c.setForeground(FG_WHITE);
        c.setFont(new Font("Arial", Font.PLAIN, 12));
        return c;
    }

    private void estilizarTextField(JTextField f) {
        f.setBackground(new Color(25, 25, 35));
        f.setForeground(FG_WHITE);
        f.setCaretColor(FG_WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 120)),
            BorderFactory.createEmptyBorder(3, 6, 3, 6)));
    }

    private TitledBorder tituloBorde(String titulo) {
        TitledBorder b = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 120)), titulo);
        b.setTitleColor(FG_GRAY);
        return b;
    }
}