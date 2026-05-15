import Simulacion.Reloj;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import GUI.MenuFrame;

public class Main {
public static void main(String[] args) {
    // 1. Detectar si hay partida guardada
    File archivo = new File("Archivos/ciudadanos.json");
    boolean hayGuardado = archivo.exists() && archivo.length() > 10;

    if (hayGuardado) {
        String[] opciones = {"Continuar partida", "Nueva partida"};
        int eleccion = JOptionPane.showOptionDialog(null,
            "Se encontró una partida guardada.\n¿Continuar o iniciar desde cero?",
            "Therania — Inicio",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, opciones, opciones[0]);

        if (eleccion < 0) { System.exit(0); }      // cerró el diálogo
        if (eleccion == 1) { borrarArchivos(); }    // nueva partida
        }

    // 2. Arrancar simulación
    Reloj reloj = new Reloj();

    Thread hilo = new Thread(() -> new Reloj().iniciar(50, 2));
    hilo.setDaemon(true);
    hilo.start();

    // 3. Abrir GUI
    SwingUtilities.invokeLater(() -> new MenuFrame(reloj).setVisible(true));
    }

private static void borrarArchivos() {
    for (String f : new String[]{
        "Archivos/ciudadanos.json", "Archivos/manadas.json",
        "Archivos/rituales.json",   "Archivos/especies.json"}) {
        new File(f).delete();
        }
    }

}