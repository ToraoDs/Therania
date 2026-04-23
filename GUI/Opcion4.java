package GUI;

import javax.swing.*;
import java.awt.*;

public class Opcion4 extends JPanel {
    private JLabel label;
//Aquí las busquedas y la tabla solo muestran vehiculos activos
    public Opcion4() {
        
        JPanel PanelT = new JPanel(new BorderLayout());
        
        PanelT.setBackground(Color.BLUE);
        add(PanelT,BorderLayout.CENTER);
        JLabel label = new JLabel("Este es un panel personalizado Panel 4");
        PanelT.add(label);
        
    }

    public void setText(String text) {
        label.setText(text);
    }
}