import Simulacion.Reloj;
import GUI.MenuFrame;

public class Main {
    public static void main(String[] args) {
        Reloj reloj = new Reloj();
        reloj.iniciar(12, 2);
        MenuFrame menuFrame = new MenuFrame();
        menuFrame.setVisible(true);
    }
}