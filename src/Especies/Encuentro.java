package Especies;

public class Encuentro {

    private static final double AUMENTO_VICTORIA = 5.0;  // cuánto sube al ganar
    private static final double DISMINUCION_DERROTA = 2.0; // cuánto baja al perder
    private static final double MAXIMO_RATIO = 100.0;
    private static final double MINIMO_RATIO = 0.0;

    // Registra el resultado de un encuentro entre predador y presa
    public static void registrarEncuentro(CiudadanoTherian predador,
                                          CiudadanoTherian presa,
                                          boolean predadorGano) {
        if (predadorGano) {
            // Predador captura → sube ratioCaza
            predador.setRatioCaza(
                Math.min(predador.getRatioCaza() + AUMENTO_VICTORIA, MAXIMO_RATIO)
            );
            // Presa pierde → baja ratioEscape
            presa.setRatioEscape(
                Math.max(presa.getRatioEscape() - DISMINUCION_DERROTA, MINIMO_RATIO)
            );
        } else {
            // Presa escapa → sube ratioEscape
            presa.setRatioEscape(
                Math.min(presa.getRatioEscape() + AUMENTO_VICTORIA, MAXIMO_RATIO)
            );
            // Predador falla → baja ratioCaza
            predador.setRatioCaza(
                Math.max(predador.getRatioCaza() - DISMINUCION_DERROTA, MINIMO_RATIO)
            );
        }

        asignarRol(predador);
        asignarRol(presa);
    }


    public static void asignarRol(CiudadanoTherian ciudadano) {
        double iaa = ciudadano.getIAA();
        double victoria = ciudadano.esPredador()
                        ? ciudadano.getRatioCaza()
                        : ciudadano.getRatioEscape();

        double puntaje = (iaa * 0.6) + (victoria * 0.4);

        if (puntaje >= 70) {
            ciudadano.setRol("Alfa");
        } else if (puntaje >= 40) {
            ciudadano.setRol("Beta");
        } else {
            ciudadano.setRol("Omega");
        }
    }
}