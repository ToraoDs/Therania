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
    private JButton option7Button;
    private final Reloj reloj;

    JPanel mainPanel;
    private Image imagen;

    public MenuFrame(Reloj reloj) {
        this.reloj = reloj;
        setTitle("Therania");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        UIManager.put("Label.foreground", Color.LIGHT_GRAY);
        UIManager.put("Button.foreground", Color.LIGHT_GRAY);
        UIManager.put("Button.background", Color.DARK_GRAY);

        JPanel panel = new JPanel(new GridLayout(7, 1));
        panel.setBackground(Color.DARK_GRAY);

        JLabel Bienvenida = new JLabel(
            "Sistema de distribucion de habitantes del reino constitucional de Therania",
            SwingConstants.CENTER);
        Bienvenida.setFont(new Font("Arial", Font.BOLD, 32));

        imagen = new ImageIcon("Imagenes/Principal.png").getImage();
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imagen != null) {
                    int x = (getWidth()  - imagen.getWidth(this))  / 2;
                    int y = (getHeight() - imagen.getHeight(this)) / 2;
                    g.drawImage(imagen, x, y, this);
                }
            }
        };
        mainPanel.setBackground(Color.DARK_GRAY);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(Bienvenida, BorderLayout.NORTH);

        option1Button = new JButton("Mapa");
        option1Button.addActionListener(this);
        panel.add(option1Button);

        option2Button = new JButton("Manadas");
        option2Button.addActionListener(this);
        panel.add(option2Button);

        option6Button = new JButton("Registrar");
        option6Button.addActionListener(this);
        panel.add(option6Button);

        option3Button = new JButton("Actualizar");
        option3Button.addActionListener(this);
        panel.add(option3Button);

        option4Button = new JButton("Trayectoria");
        option4Button.addActionListener(this);
        panel.add(option4Button);

        option5Button = new JButton("Global");
        option5Button.addActionListener(this);
        panel.add(option5Button);

        option7Button = new JButton("Salir");
        option7Button.setBackground(Color.RED);
        option7Button.addActionListener(this);
        panel.add(option7Button);

        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) { }
        });

        add(panel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        // Abrir con Opcion1 con callback de especie
        SwingUtilities.invokeLater(() ->
            MPAdd(new Opcion1(especie -> {
                reloj.pausar();
                MPAdd(new Opcion5(especie, id -> {
                    reloj.pausar();
                    MPAdd(new Opcion4(id));
                }));
            }))
        );

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == option1Button) {
            reloj.reanudar();
            MPAdd(new Opcion1(especie -> {
                reloj.pausar();
                MPAdd(new Opcion5(especie, id -> {
                    reloj.pausar();
                    MPAdd(new Opcion4(id));
                }));
            }));

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
            }));

        } else if (e.getSource() == option6Button) {
            reloj.pausar();
            MPAdd(new Opcion6(reloj, id -> {
                reloj.pausar();
                MPAdd(new Opcion4(id));
            }));

        } else if (e.getSource() == option7Button) {
            JOptionPane.showMessageDialog(this, "Cerrando programa");
            System.exit(0);
        }
    }

    public void MPAdd(JPanel panel) {
        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}