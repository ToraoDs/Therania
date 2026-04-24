package GUI;

import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.awt.*;

public class Opcion1 extends JPanel {

    private Image imagen;

    public Opcion1() {
        imagen = new ImageIcon("Imagenes/Mapa.jpeg").getImage();
        capturarPosicionMouse(this);
    }

    public void capturarPosicionMouse(JPanel panel) {
    panel.addMouseMotionListener(new MouseMotionAdapter() {
        @Override
        public void mouseMoved(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            System.out.println("Posición del mouse: X=" + x + " Y=" + y); //Aquí iria la logica para mostrar la info de cada therian dependiendo de la coordenada del mouse, se puede usar un mapa de coordenadas para cada therian y mostrar la info dependiendo de la coordenada del mouse (Dentro de un ciclo if y ahí decidimos que accion tomar).
        }
    });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
    }
}