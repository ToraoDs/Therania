package GUI;

import Servicios.CiudadanoServicio;
import Servicios.ManadaServicio;
import Simulacion.Reloj;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import Especies.CiudadanoTherian;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class Opcion6 extends JPanel {

    private JTextField campoBusqueda;
    private JLabel lblNombre, lblEspecie, lblIAA, lblRol;
    private JComboBox<String> comboEstado;
    private JComboBox<String> comboEspecie;
    private JComboBox<String> comboRol;
    private JButton btnBuscar, btnGuardar;
    private JTextArea areaInfo;
    private final Reloj reloj;

    private Map<String, Object> ciudadanoActual = null;

    private static final String[] ESTADOS   = {"Activo", "Suspendido", "En Revision"};
    private static final String[] ESPECIES   = {"Lobo","Leon","Ciervo","Alce","Tigre","Halcon","Orca","Cebra","Foca","Paloma"};
    private static final String[] ROLES      = {"Alfa","Beta","Omega","Observador"};

    public Opcion6(Reloj reloj) {
        this.reloj = reloj;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(30, 30, 40));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(construirPanelBusqueda(), BorderLayout.NORTH);
        add(construirPanelFormulario(), BorderLayout.CENTER);
        add(construirPanelInfo(), BorderLayout.SOUTH);
    }

    // ─── Panel de búsqueda ────────────────────────────────────────────────

    private JPanel construirPanelBusqueda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(40, 40, 55));
        panel.setBorder(tituloBorde("Buscar ciudadano"));

        JLabel lbl = new JLabel("ID ciudadano:");
        lbl.setForeground(Color.LIGHT_GRAY);

        campoBusqueda = new JTextField(10);
        btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(new Color(60, 100, 160));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.addActionListener(e -> buscarCiudadano());

        panel.add(lbl);
        panel.add(campoBusqueda);
        panel.add(btnBuscar);
        return panel;
    }

    // ─── Panel de formulario ──────────────────────────────────────────────

    private JPanel construirPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(40, 40, 55));
        panel.setBorder(tituloBorde("Datos del ciudadano"));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.anchor = GridBagConstraints.WEST;

        // Fila 0: Nombre (solo lectura)
        g.gridx = 0; g.gridy = 0;
        panel.add(etiqueta("Nombre:"), g);
        g.gridx = 1;
        lblNombre = new JLabel("—");
        lblNombre.setForeground(Color.WHITE);
        panel.add(lblNombre, g);

        // Fila 1: Especie actual (solo lectura)
        g.gridx = 0; g.gridy = 1;
        panel.add(etiqueta("Especie actual:"), g);
        g.gridx = 1;
        lblEspecie = new JLabel("—");
        lblEspecie.setForeground(new Color(150, 210, 255));
        panel.add(lblEspecie, g);

        // Fila 2: IAA actual (solo lectura)
        g.gridx = 0; g.gridy = 2;
        panel.add(etiqueta("IAA actual:"), g);
        g.gridx = 1;
        lblIAA = new JLabel("—");
        lblIAA.setForeground(new Color(120, 220, 120));
        panel.add(lblIAA, g);

        // Fila 3: Rol actual (solo lectura)
        g.gridx = 0; g.gridy = 3;
        panel.add(etiqueta("Rol actual:"), g);
        g.gridx = 1;
        lblRol = new JLabel("—");
        lblRol.setForeground(new Color(255, 200, 100));
        panel.add(lblRol, g);

        // Fila 4: Estado (editable)
        g.gridx = 0; g.gridy = 4;
        panel.add(etiqueta("Nuevo estado:"), g);
        g.gridx = 1;
        comboEstado = new JComboBox<>(ESTADOS);
        panel.add(comboEstado, g);

        // Fila 5: Especie (editable)
        g.gridx = 0; g.gridy = 5;
        panel.add(etiqueta("Nueva especie:"), g);
        g.gridx = 1;
        comboEspecie = new JComboBox<>(ESPECIES);
        panel.add(comboEspecie, g);

        // Fila 6: Rol (editable)
        g.gridx = 0; g.gridy = 6;
        panel.add(etiqueta("Nuevo rol:"), g);
        g.gridx = 1;
        comboRol = new JComboBox<>(ROLES);
        panel.add(comboRol, g);

        // Fila 7: Botón guardar
        g.gridx = 0; g.gridy = 7; g.gridwidth = 2;
        g.fill = GridBagConstraints.HORIZONTAL;
        btnGuardar = new JButton("💾  Guardar cambios");
        btnGuardar.setBackground(new Color(60, 140, 60));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setEnabled(false);
        btnGuardar.addActionListener(e -> guardarCambios());
        panel.add(btnGuardar, g);

        return panel;
    }

    // ─── Panel de info / resultado ────────────────────────────────────────

    private JPanel construirPanelInfo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(40, 40, 55));
        panel.setBorder(tituloBorde("Resultado"));

        areaInfo = new JTextArea(4, 40);
        areaInfo.setEditable(false);
        areaInfo.setBackground(new Color(25, 25, 35));
        areaInfo.setForeground(new Color(180, 230, 180));
        areaInfo.setFont(new Font("Monospaced", Font.PLAIN, 12));
        panel.add(new JScrollPane(areaInfo), BorderLayout.CENTER);
        return panel;
    }

    // ─── Lógica de búsqueda ───────────────────────────────────────────────

    private void buscarCiudadano() {
        String idTexto = campoBusqueda.getText().trim();
        if (idTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa un ID válido.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<Map<String, Object>> todos = CiudadanoServicio.cargarCiudadanos();
            ciudadanoActual = null;
            for (Map<String, Object> c : todos) {
                int id = ((Number) c.get("id")).intValue();
                if (id == Integer.parseInt(idTexto)) {
                    ciudadanoActual = c;
                    break;
                }
            }

            if (ciudadanoActual == null) {
                areaInfo.setText("No se encontró ciudadano con ID: " + idTexto);
                limpiarFormulario();
                return;
            }

            // Rellenar campos con datos actuales
            lblNombre.setText(ciudadanoActual.get("nombre") + " " + ciudadanoActual.get("apellido"));
            lblEspecie.setText((String) ciudadanoActual.get("especie"));
            lblIAA.setText(String.format("%.2f", ((Number) ciudadanoActual.get("iaa")).doubleValue()));
            lblRol.setText((String) ciudadanoActual.get("rol"));

            comboEstado.setSelectedItem(ciudadanoActual.get("estadoCiudadania"));
            comboEspecie.setSelectedItem(ciudadanoActual.get("especie"));
            comboRol.setSelectedItem(ciudadanoActual.get("rol"));

            btnGuardar.setEnabled(true);
            areaInfo.setText("Ciudadano encontrado. Modifica los campos y presiona Guardar.");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El ID debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            areaInfo.setText("Error al buscar: " + ex.getMessage());
        }
    }

    // ─── Lógica de guardado ───────────────────────────────────────────────

    private void guardarCambios() {
        if (ciudadanoActual == null) return;

        String nuevaEspecie    = (String) comboEspecie.getSelectedItem();
        String nuevoEstado     = (String) comboEstado.getSelectedItem();
        String nuevoRol        = (String) comboRol.getSelectedItem();
        String especieAnterior = (String) ciudadanoActual.get("especie");
        int    idBuscado       = ((Number) ciudadanoActual.get("id")).intValue();
        boolean cambioEspecie  = !nuevaEspecie.equals(especieAnterior);

        // Intentar modificar objeto real del Reloj
        CiudadanoTherian ciudadanoReal = null;
        if (reloj != null && reloj.getCiudadanos() != null) {
            for (CiudadanoTherian c : reloj.getCiudadanos()) {
                if (c.getId() == idBuscado) { ciudadanoReal = c; break; }
            }
        }

        if (ciudadanoReal != null) {
            // ── Camino A: Reloj activo → modificar objeto real ──
            ciudadanoReal.setEstadoCiudadania(nuevoEstado);
            ciudadanoReal.setRol(nuevoRol);

            if (cambioEspecie) {
                String[] att = obtenerAtributosEspecie(nuevaEspecie);
                ciudadanoReal.cambiarEspecie(
                    nuevaEspecie, esPredadora(nuevaEspecie),
                    att[0], att[1], att[2],
                    reloj.getManadas()
                );
            }
            CiudadanoServicio.guardarCiudadanos(reloj.getCiudadanos());
            ManadaServicio.guardarManadas(reloj.getManadas());

        } else {
            // ── Camino B: Reloj no listo → modificar JSON directamente ──
            try {
                List<Map<String, Object>> todos = CiudadanoServicio.cargarCiudadanos();
                for (Map<String, Object> c : todos) {
                    if (((Number) c.get("id")).intValue() == idBuscado) {
                        c.put("estadoCiudadania", nuevoEstado);
                        c.put("rol",              nuevoRol);
                        c.put("especie",          nuevaEspecie);
                        c.put("esPredador",       esPredadora(nuevaEspecie));

                        if (cambioEspecie) {
                            // Actualizar historial
                            Object hist = c.get("historialEspecie");
                            List<Object> historial = hist instanceof List
                                ? (List<Object>) hist : new java.util.ArrayList<>();
                            if (!historial.contains(especieAnterior)) historial.add(especieAnterior);
                            c.put("historialEspecie", historial);
                        }
                        break;
                    }
                }
                CiudadanoServicio.guardarCiudadanosRaw(todos);
            } catch (Exception ex) {
                areaInfo.setText("Error al guardar: " + ex.getMessage());
                return;
            }
        }

        // Actualizar etiquetas y reanudar
        lblEspecie.setText(nuevaEspecie);
        lblRol.setText(nuevoRol);
        reloj.reanudar();

        areaInfo.setText("✔ Cambios guardados correctamente.\n"
            + "  Especie: " + especieAnterior + (cambioEspecie ? " → " + nuevaEspecie : " (sin cambio)") + "\n"
            + "  Estado:  " + nuevoEstado + "\n"
            + "  Rol:     " + nuevoRol + "\n"
            + (cambioEspecie ? "  → Ingresó a ManadaDePaso por 2 meses." : ""));
    }

        private boolean esPredadora(String especie) {
            return especie.equals("Lobo")  || especie.equals("Leon") ||
                especie.equals("Tigre") || especie.equals("Halcon") ||
                especie.equals("Orca");
    }

    private String[] obtenerAtributosEspecie(String especie) {
        switch (especie) {
            case "Lobo":   return new String[]{"Aullido profundo",    "x:490,y:745,r:75", "Territorial, social, estratégico"};
            case "Leon":   return new String[]{"Rugido grave",        "x:815,y:490,r:75", "Dominante, protector, noble"};
            case "Ciervo": return new String[]{"Bramido suave",       "x:510,y:490,r:80", "Sensible, ágil, intuitivo"};
            case "Alce":   return new String[]{"Bramido resonante",   "x:215,y:285,r:80", "Majestuoso, solitario, territorial"};
            case "Tigre":  return new String[]{"Gruñido sibilante",   "x:245,y:690,r:80", "Solitario, determinado, sigiloso"};
            case "Halcon": return new String[]{"Chillido penetrante", "x:855,y:215,r:70", "Preciso, independiente, observador"};
            case "Orca":   return new String[]{"Clicks y silbidos",   "x:110,y:890,r:60", "Inteligente, familiar, organizada"};
            case "Cebra":  return new String[]{"Ladrido corto",       "x:295,y:470,r:75", "Resiliente, alerta, gregaria"};
            case "Foca":   return new String[]{"Gruñidos y ladridos", "x:530,y:165,r:70", "Juguetona, curiosa, social"};
            default:       return new String[]{"Arrullo suave",       "x:760,y:790,r:70", "Pacífica, empática, leal"};
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────

    private void limpiarFormulario() {
        lblNombre.setText("—");
        lblEspecie.setText("—");
        lblIAA.setText("—");
        lblRol.setText("—");
        btnGuardar.setEnabled(false);
    }

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(Color.LIGHT_GRAY);
        return l;
    }

    private TitledBorder tituloBorde(String titulo) {
        TitledBorder b = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 120)), titulo);
        b.setTitleColor(Color.LIGHT_GRAY);
        return b;
    }
}