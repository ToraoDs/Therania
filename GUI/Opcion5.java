package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Opcion5 extends JPanel implements ActionListener{

    private ImageIcon redimensionarIcono(String ruta, int ancho, int alto) {
        ImageIcon icono = new ImageIcon(ruta);
        Image imagen = icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
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

        //PanelCentro
        JPanel PanelC = new JPanel();
        PanelC.setLayout(new GridLayout(2,2));
        PanelC.setBackground(Color.LIGHT_GRAY);

//*******************************************************************************************************************   

        //Panel Sur
        JPanel PanelS = new JPanel(new BorderLayout());

 //*******************************************************************************************************************           
        
        //Mensaje Inicial
        JLabel Mensaje = new JLabel("Historial Global de Registros", SwingConstants.CENTER);
        PanelN1.add(Mensaje, BorderLayout.CENTER);
        
        Mensaje.setFont(new Font(null, WIDTH, 32));

        //Iconos
        ImageIcon Alce = redimensionarIcono("Imagenes/Iconos therania/Alce.png", 80, 80);
        ImageIcon Cebra = redimensionarIcono("Imagenes/Iconos therania/Cebra.png", 80, 80);
        ImageIcon Ciervo = redimensionarIcono("Imagenes/Iconos therania/Ciervo.png", 80, 80);
        ImageIcon Foca = redimensionarIcono("Imagenes/Iconos therania/Foca.png", 80, 80);
        ImageIcon Halcon = redimensionarIcono("Imagenes/Iconos therania/Halcon.png", 80, 80);
        ImageIcon Leon = redimensionarIcono("Imagenes/Iconos therania/León.png", 80, 80);
        ImageIcon Lobo = redimensionarIcono("Imagenes/Iconos therania/Lobo.png", 80, 80);
        ImageIcon Orca = redimensionarIcono("Imagenes/Iconos therania/Orca.png", 80, 80);
        ImageIcon Paloma = redimensionarIcono("Imagenes/Iconos therania/Paloma.png", 80, 80);
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

//******************************************************************************************************************** */
        
        //Campo Busqueda 
        JLabel L1 = new JLabel("Buscar: ", SwingConstants.CENTER);
        JTextField P1 = new JTextField(null,10);
        P1.setFont(new Font(null,WIDTH,24));
        PanelC.add(L1);
        PanelC.add(P1);
        
        //Campo de selección de busqueda
        Choice Tipo = new Choice();
        Tipo.addItem("Filtrar por:");
        Tipo.addItem("Nombre");
        Tipo.addItem("Raza");
        Tipo.addItem("Manada");
        PanelC.add(Tipo);

        //Botón de buscar
        JButton B1 = new JButton("Buscar");
        B1.addActionListener(this);
        PanelC.add(B1);
        
        //Tabla de datos
        JTable T1 = new JTable(50,12);
        JScrollPane SC = new JScrollPane(T1);
        PanelS.add(SC, BorderLayout.CENTER);

//*******************************************************************************************************************            
        //Incluir Paneles
        PanelT.add(PanelN, BorderLayout.NORTH);
        PanelT.add(PanelC, BorderLayout.SOUTH);
        PanelT.add(PanelS, BorderLayout.CENTER);
        add(PanelT, BorderLayout.CENTER);
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

}