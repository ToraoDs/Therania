package GUI;

import javax.swing.*;
import java.awt.*;

public class Opcion2 extends JPanel {
    private JLabel label;

    public Opcion2() {
        
        JPanel PanelT = new JPanel(new BorderLayout());
        
        PanelT.setBackground(Color.BLUE);
        add(PanelT,BorderLayout.CENTER);
        JLabel label = new JLabel("Este es un panel personalizado Panel 2");
        PanelT.add(label);
        
    }
}