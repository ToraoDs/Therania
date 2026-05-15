package GUI;

import com.google.gson.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Opcion5 extends JPanel implements ActionListener {

    // Tabla global
    private JTable T1;

    // Selector global
    private Choice Tipo;

    private ImageIcon redimensionarIcono(String ruta, int ancho, int alto) {

        ImageIcon icono = new ImageIcon(ruta);

        Image imagen = icono.getImage().getScaledInstance(
                ancho,
                alto,
                Image.SCALE_SMOOTH
        );

        return new ImageIcon(imagen);
    }

    public Opcion5()
    {

        UIManager.put("Label.foreground", Color.BLACK);
        UIManager.put("Button.foreground", Color.LIGHT_GRAY);
        UIManager.put("TextField.foreground", Color.LIGHT_GRAY);

        UIManager.put("Button.background", Color.DARK_GRAY);

        setLayout(new BorderLayout());

//*******************************************************************************************************************
        //Panel Principal
        JPanel PanelT = new JPanel(new BorderLayout());

//*******************************************************************************************************************

        //Panel Norte
        JPanel PanelN = new JPanel(new GridLayout(2,1));
        PanelN.setBackground(Color.LIGHT_GRAY);

        JPanel PanelN1 = new JPanel(new BorderLayout());
        PanelN1.setBackground(Color.LIGHT_GRAY);

        JPanel PanelN2 = new JPanel(new GridLayout(1,10));
        PanelN2.setBackground(Color.LIGHT_GRAY);

        PanelN.add(PanelN1);
        PanelN.add(PanelN2);

//*******************************************************************************************************************

        //Panel Centro
        JPanel PanelC = new JPanel();
        PanelC.setLayout(new GridLayout(2,2));
        PanelC.setBackground(Color.LIGHT_GRAY);

//*******************************************************************************************************************

        //Panel Sur
        JPanel PanelS = new JPanel(new BorderLayout());

//*******************************************************************************************************************

        //Mensaje Inicial
        JLabel Mensaje = new JLabel(
                "Historial Global de Registros",
                SwingConstants.CENTER
        );

        PanelN1.add(Mensaje, BorderLayout.CENTER);

        Mensaje.setFont(new Font(null, WIDTH, 32));

//*******************************************************************************************************************

        //Iconos
        ImageIcon Alce = redimensionarIcono("Imagenes/Iconos therania/Alce.png",80,80);

        ImageIcon Cebra = redimensionarIcono("Imagenes/Iconos therania/Cebra.png",80,80);

        ImageIcon Ciervo = redimensionarIcono("Imagenes/Iconos therania/Ciervo.png",80,80);

        ImageIcon Foca = redimensionarIcono("Imagenes/Iconos therania/Foca.png",80,80);

        ImageIcon Halcon = redimensionarIcono("Imagenes/Iconos therania/Halcon.png",80,80);

        ImageIcon Leon = redimensionarIcono("Imagenes/Iconos therania/León.png",80,80);

        ImageIcon Lobo = redimensionarIcono("Imagenes/Iconos therania/Lobo.png",80,80);

        ImageIcon Orca = redimensionarIcono( "Imagenes/Iconos therania/Orca.png", 80, 80);

        ImageIcon Paloma = redimensionarIcono("Imagenes/Iconos therania/Paloma.png",80,80);

        ImageIcon Tigre = redimensionarIcono("Imagenes/Iconos therania/Tigre.png", 80, 80);

        PanelN2.add(new JLabel(Alce));
        PanelN2.add(new JLabel(Cebra));
        PanelN2.add(new JLabel(Ciervo));
        PanelN2.add(new JLabel(Foca));
        PanelN2.add(new JLabel(Halcon));
        PanelN2.add(new JLabel(Leon));
        PanelN2.add(new JLabel(Lobo));
        PanelN2.add(new JLabel(Orca));
        PanelN2.add(new JLabel(Paloma));
        PanelN2.add(new JLabel(Tigre));

//*******************************************************************************************************************

        //Selector de archivos a visualizar
        Tipo = new Choice();

        Tipo.addItem("Seleccione una busqueda");
        Tipo.addItem("ciudadanos");
        Tipo.addItem("especies");
        Tipo.addItem("manadas");
        Tipo.addItem("rituales");

        PanelC.add(Tipo);

//*******************************************************************************************************************

        //Botón Buscar
        JButton B1 = new JButton("Buscar");

        B1.addActionListener(this);

        PanelC.add(B1);

//*******************************************************************************************************************

        //Tabla dinámica
        DefaultTableModel modelo = new DefaultTableModel();

        T1 = new JTable(modelo);

        JScrollPane SC = new JScrollPane(T1);

        PanelS.add(SC, BorderLayout.CENTER);

//*******************************************************************************************************************

        //Incluir Paneles
        PanelT.add(PanelN, BorderLayout.NORTH);
        PanelT.add(PanelC, BorderLayout.SOUTH);
        PanelT.add(PanelS, BorderLayout.CENTER);

        add(PanelT, BorderLayout.CENTER);

    }

//*******************************************************************************************************************

    // Cargar datos desde JSON a la tabla y que se adapte sola al tamaño del JSON
    private void cargarTabla(String ruta)
        {
    try
    {

        String contenido = new String(
                Files.readAllBytes(Paths.get(ruta))
        );

        JsonElement elementoPrincipal =
                JsonParser.parseString(contenido);

        DefaultTableModel modelo =
                new DefaultTableModel();

        // Verificar que sea un arreglo JSON
        if(elementoPrincipal.isJsonArray())
        {

            JsonArray arreglo =
                    elementoPrincipal.getAsJsonArray();

            if(arreglo.size() > 0)
            {

                JsonObject primerObjeto =
                        arreglo.get(0).getAsJsonObject();

                // Crear columnas dinámicamente
                for(String clave : primerObjeto.keySet())
                {
                    modelo.addColumn(clave);
                }

                // Agregar filas
                for(JsonElement elemento : arreglo)
                {

                    JsonObject fila =
                            elemento.getAsJsonObject();

                    Object[] datos =
                            new Object[modelo.getColumnCount()];

                    int j = 0;

                    for(String clave : primerObjeto.keySet())
                    {

                        JsonElement valor =
                                fila.get(clave);

                        // Convertir cualquier tipo JSON a texto
                        if(valor == null || valor.isJsonNull())
                        {
                            datos[j] = "";
                        }
                        else if(valor.isJsonPrimitive())
                        {
                            datos[j] = valor.getAsString();
                        }
                        else
                        {
                            // Objetos o arrays
                            datos[j] = valor.toString();
                        }

                        j++;
                    }

                    modelo.addRow(datos);
                }
            }
        }

        // Actualizar tabla
        T1.setModel(modelo);

        // Altura de filas
        T1.setRowHeight(30);

        // Ajustar automáticamente columnas

    }
    catch(Exception e)
    {

        JOptionPane.showMessageDialog(
                null,
                "Error al cargar JSON:\n" + e.getMessage()
        );
    }
}

private void ajustarAlturaFilas()
{

    for(int fila = 0; fila < T1.getRowCount(); fila++)
    {

        int alturaMaxima = 30;

        for(int columna = 0;
            columna < T1.getColumnCount();
            columna++)
        {

            JTextArea area = new JTextArea(
                    T1.getValueAt(fila, columna).toString()
            );

            area.setLineWrap(true);

            area.setWrapStyleWord(true);

            area.setFont(T1.getFont());

            area.setSize(
                    T1.getColumnModel()
                            .getColumn(columna)
                            .getWidth(),
                    Short.MAX_VALUE
            );

            alturaMaxima = Math.max(
                    alturaMaxima,
                    area.getPreferredSize().height
            );
        }

        T1.setRowHeight(fila, alturaMaxima);
    }
}

class TextAreaRenderer extends JTextArea
        implements javax.swing.table.TableCellRenderer
{

    public TextAreaRenderer()
    {
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column)
    {

        setText(value == null ? "" : value.toString());

        if(isSelected)
        {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        }
        else
        {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }

        setFont(table.getFont());

        return this;
    }
}


//*******************************************************************************************************************

    @Override
    public void actionPerformed(ActionEvent e)
    {

        String archivo = Tipo.getSelectedItem();

        switch(archivo)
        {

                case "ciudadanos":

                cargarTabla("Archivos/ciudadanos.json");

                break;

                case "especies":

                cargarTabla("Archivos/especies.json");

                break;

                case "manadas":

                cargarTabla("Archivos/manadas.json");

                break;
                case "rituales":

                    cargarTabla("Archivos/rituales.json");

                    break;
            default:

                JOptionPane.showMessageDialog(
                        null,
                        "Seleccione una opción"
                );
        }
    }
}