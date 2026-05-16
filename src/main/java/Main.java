import GUI.MenuFrame;
import Simulacion.Reloj;

import javax.swing.*;
import java.io.File;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // ── Menú de selección de modo ────────────────────────────────────
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║        THERANIA — INICIO         ║");
        System.out.println("╠══════════════════════════════════╣");
        System.out.println("║  1. Modo Simulación (consola)    ║");
        System.out.println("║  2. Programa Completo (GUI)      ║");
        System.out.println("║  0. Salir                        ║");
        System.out.println("╚══════════════════════════════════╝");
        System.out.print("Selecciona una opción: ");

        int opcion = leerOpcion();

        switch (opcion) {
            case 1: iniciarModoSimulacion(); break;
            case 2: iniciarModoCompleto();   break;
            case 0: System.exit(0);          break;
            default:
                System.err.println("Opción no válida.");
                System.exit(1);
        }
    }

    // ── Modo simulación (solo consola, sin logs normales) ─────────────────
    private static void iniciarModoSimulacion() {
        System.out.println("Iniciando modo simulación...");
        System.out.println("Presiona ENTER para detener.");

        Reloj reloj = new Reloj();
        reloj.setModoSilencioso(false);

        // Hilo que espera ENTER para detener
        Thread hiloEscucha = new Thread(() -> {
            try {
                System.in.read();   // espera cualquier tecla + ENTER
                System.err.println("Deteniendo simulación...");
                reloj.detener();
            } catch (Exception e) {
                System.err.println("Error al leer entrada: " + e.getMessage());
            }
        });
        hiloEscucha.setDaemon(true);
        hiloEscucha.start();

        reloj.iniciar(2);   // corre en el hilo principal
        System.out.println("Simulación terminada.");
    }

    // ── Modo completo (GUI + simulación) ──────────────────────────────────
    private static void iniciarModoCompleto() {
        // Diálogo nueva partida / continuar
        File archivo = new File("Archivos/ciudadanos.json");
        boolean hayGuardado = archivo.exists() && archivo.length() > 10;

        if (hayGuardado) {
            String[] opciones = {"Continuar partida", "Nueva partida"};
            int eleccion = JOptionPane.showOptionDialog(null,
                "Se encontró una partida guardada.\n¿Continuar o iniciar desde cero?",
                "Therania — Inicio",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opciones, opciones[0]);

            if (eleccion < 0)  { System.exit(0); }
            if (eleccion == 1) { borrarArchivos(); }
        }

        // Una sola instancia de Reloj compartida con la GUI
        Reloj reloj = new Reloj();
        reloj.setModoSilencioso(true);

        Thread hilo = new Thread(() -> reloj.iniciar(2));
        hilo.setDaemon(true);
        hilo.start();

        SwingUtilities.invokeLater(() -> new MenuFrame(reloj).setVisible(true));
    }

    // ─────────────────────────────────────────────────────────────────────
    private static int leerOpcion() {
        try {
            Scanner sc = new Scanner(System.in);
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void borrarArchivos() {
        for (String f : new String[]{
            "Archivos/ciudadanos.json", "Archivos/manadas.json",
            "Archivos/rituales.json",   "Archivos/especies.json"}) {
            new File(f).delete();
        }
    }
}