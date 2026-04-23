package GUI;

import javax.swing.*;
import java.awt.*;

public class Opcion3 extends JPanel {
    private JLabel label;

    public Opcion3() {
        
        JPanel PanelT = new JPanel(new BorderLayout());
        
        PanelT.setBackground(Color.BLUE);
        add(PanelT,BorderLayout.CENTER);
        JLabel label = new JLabel("Este es un panel personalizado Panel 3");
        PanelT.add(label);
        
    }
}