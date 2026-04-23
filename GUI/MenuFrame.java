package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuFrame extends javax.swing.JFrame implements ActionListener {
    
    private JButton option1Button;
    private JButton option2Button;
    private JButton option3Button;
    private JButton option4Button;
    private JButton option5Button;
    
    JPanel mainPanel = new JPanel(/*new GridBagLayout()*/);
    

    public MenuFrame() {
        setTitle("Theriana");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1000, 650);
        setLocationRelativeTo(null); // Centrar la ventana en la pantalla

//********************************************************************************************************************************
        // Cambiar el color predeterminado del texto para todo
        UIManager.put("Label.foreground", Color.LIGHT_GRAY);
        UIManager.put("Button.foreground", Color.LIGHT_GRAY);
        
        // Cambiar el color predeterminado de los botones
        UIManager.put("Button.background", Color.DARK_GRAY);
//********************************************************************************************************************************
        // Panel para las opciones del menú en la parte izquierda
        JPanel panel = new JPanel(new GridLayout(5,1));
        panel.setBackground(Color.DARK_GRAY);

        // PAnel principal abierto
        mainPanel.setBackground(Color.GREEN);// Panel principal verde (Se puede poner un logo para theriana o mensaje de bienvenida)
        mainPanel.setLayout(new GridLayout(1,1));
        
//********************************************************************************************************************************
        // Opción 1
        option1Button = new JButton("O1");
        option1Button.addActionListener(this);
        panel.add(option1Button);

        // Opción 2
        option2Button = new JButton("O2");
        option2Button.addActionListener(this);
        panel.add(option2Button);

        // Opción 3
        option3Button = new JButton("O3");
        option3Button.addActionListener(this);
        panel.add(option3Button);

        // Opción 4
        option4Button = new JButton("O4");
        option4Button.addActionListener(this);
        panel.add(option4Button);

        // Opción 5
        option5Button = new JButton("Salir");
        option5Button.setBackground(Color.RED); // Color de fondo
        option5Button.addActionListener(this);
        panel.add(option5Button);
       
//********************************************************************************************************************************
        // Agregar un ComponentListener para detectar cambios en el tamaño del panel
            mainPanel.addComponentListener(new ComponentAdapter()
            {   
            @Override
            public void componentResized(ComponentEvent e) 
            {
                // Ajustar el contenido del panel según su nuevo tamaño
                Dimension size = mainPanel.getSize();
            }
        });
//********************************************************************************************************************************
        // Agregar el panel del menú al panel principal en la parte izquierda
        add(panel,BorderLayout.WEST);
        // Agregar el panel al frame
        add(mainPanel,BorderLayout.CENTER);
        
      }
//********************************************************************************************************************************    
    @Override
    public void actionPerformed(ActionEvent e) {
        
//********************************************************************************************************************************
        //Botones
        if (e.getSource() == option1Button) {
            Opcion1 O1 = new Opcion1();
            MPAdd(O1);
        } else if (e.getSource() == option2Button) {
            Opcion2 O2 = new Opcion2();
            MPAdd(O2);
        } else if (e.getSource() == option3Button) {
            Opcion3 O3 = new Opcion3();
            MPAdd(O3);
        } else if (e.getSource() == option4Button) {
            Opcion4 O4 = new Opcion4();
            MPAdd(O4);
        } else if (e.getSource() == option5Button) {
            JOptionPane.showMessageDialog(this, "Cerrando programa");
            System.exit(0);
        }

    }
//********************************************************************************************************************************    
    public void MPAdd(JPanel panel)
    {         
        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
}

