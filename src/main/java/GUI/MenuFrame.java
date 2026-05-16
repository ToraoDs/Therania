package GUI;

import javax.swing.*;


import Simulacion.Reloj;

import java.awt.*;
import java.awt.event.*;

public class MenuFrame extends javax.swing.JFrame implements ActionListener {
    
    private JButton option1Button;
    private JButton option2Button;
    private JButton option3Button;
    private JButton option4Button;
    private JButton option5Button;
    private JButton option6Button;  
    private final Reloj reloj;

    JPanel mainPanel;

    private Image imagen;

    public MenuFrame(Reloj reloj) {
        this.reloj = reloj;
        setTitle("Therania");
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
        JPanel panel = new JPanel(new GridLayout(6,1));
        panel.setBackground(Color.DARK_GRAY);



        // Panel principal con imagen de fondo
        JLabel Bienvenida = new JLabel("Sistema  de distribucion de habitantes del reino constitucional de Therania", SwingConstants.CENTER);
        Bienvenida.setFont(new Font("Arial", Font.BOLD, 32));
        imagen = new ImageIcon("Imagenes/Principal.png").getImage();
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imagen != null) { //Posición y tamaño de la imagen de fondo principal (Pinguino)
                    int x = (getWidth() - imagen.getWidth(this)) / 2;
                    int y = (getHeight() - imagen.getHeight(this)) / 2;
                    g.drawImage(imagen, x, y, this);
                }
            }
        };
        mainPanel.setBackground(Color.DARK_GRAY);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(Bienvenida, BorderLayout.NORTH);
//********************************************************************************************************************************
        // Opción 1
        option1Button = new JButton("Mapa");
        option1Button.addActionListener(this);
        panel.add(option1Button);

        // Opción 2
        option2Button = new JButton("Registrar");
        option2Button.addActionListener(this);
        panel.add(option2Button);

        // Opción 3
        option3Button = new JButton("Actualizar");
        option3Button.addActionListener(this);
        panel.add(option3Button);

        // Opción 4
        option4Button = new JButton("Trayectoria");
        option4Button.addActionListener(this);
        panel.add(option4Button);

        // Opción 5
        option5Button = new JButton("Global");
        option5Button.addActionListener(this);
        panel.add(option5Button);

        // Opción 6
        option6Button = new JButton("Salir");
        option6Button.setBackground(Color.RED); // Color de fondo
        option6Button.addActionListener(this);
        panel.add(option6Button);
       
//********************************************************************************************************************************
        // Agregar un ComponentListener para detectar cambios en el tamaño del panel
            mainPanel.addComponentListener(new ComponentAdapter()
            {   
            @Override
            public void componentResized(ComponentEvent e) 
            {
                // Ajustar el contenido del panel según su nuevo tamaño
                //Dimension size = mainPanel.getSize();
            }
        });

//********************************************************************************************************************************
        // Agregar el panel del menú al panel principal en la parte izquierda
        add(panel,BorderLayout.WEST);
        // Agregar el panel al frame
        add(mainPanel,BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
        Opcion1 O1 = new Opcion1();
        MPAdd(O1);
            }
        );
            
        setVisible(true);
      }
//********************************************************************************************************************************    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == option1Button) {
            reloj.reanudar();               // mapa → el reloj corre
            MPAdd(new Opcion1());
        } else if (e.getSource() == option2Button) {
            reloj.pausar();
            MPAdd(new Opcion2(reloj));
        } else if (e.getSource() == option3Button) {
            reloj.pausar();
            MPAdd(new Opcion3(reloj));
        } else if (e.getSource() == option4Button) {
            reloj.pausar();
            MPAdd(new Opcion4());
        } else if (e.getSource() == option5Button) {
            reloj.pausar();
            MPAdd(new Opcion5(id -> {
                reloj.pausar();
                MPAdd(new Opcion4(id));
                }
            )
        )
    ;
        } else if (e.getSource() == option6Button) {
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

