package Manadas;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import Especies.AfiliacionManada;
import Especies.CiudadanoTherian;

public class CalculadoraIAA {

    private static final double PESO_RITUALES    = 0.30;
    private static final double PESO_TIEMPO      = 0.25;
    private static final double PESO_COMPROMISO  = 0.25;
    private static final double PESO_PUNTUACION  = 0.20;

    private static final int MAX_RITUALES    = 50;  
    private static final int MAX_MESES       = 60;  
    private static final int MAX_COMPROMISO  = 100;
    private static final int MAX_PUNTUACION  = 100;

    public static double calcularIAA(CiudadanoTherian ciudadano, int puntuacionInterna) {

        int ritualesAsistidos = ciudadano.contarRitualesAsistidos();
        double puntajeRituales = Math.min(ritualesAsistidos, MAX_RITUALES) / (double) MAX_RITUALES;

        long mesesEnEspecie = calcularMesesEnEspecie(ciudadano.getManadas());
        double puntajeTiempo = Math.min(mesesEnEspecie, MAX_MESES) / (double) MAX_MESES;

        double promedioCompromiso = calcularPromedioCompromiso(ciudadano.getManadas());
        double puntajeCompromiso = Math.min(promedioCompromiso, MAX_COMPROMISO) / MAX_COMPROMISO;

        double puntajePuntuacion = Math.min(puntuacionInterna, MAX_PUNTUACION) / (double) MAX_PUNTUACION;

        double IAA = ((puntajeRituales * PESO_RITUALES) +
                      (puntajeTiempo * PESO_TIEMPO) +
                      (puntajeCompromiso * PESO_COMPROMISO) +
                      (puntajePuntuacion * PESO_PUNTUACION)) * 100;

        return Math.round(IAA * 100.0) / 100.0;
    }

    private static long calcularMesesEnEspecie(List<AfiliacionManada> manadas) {
        if (manadas == null || manadas.isEmpty()) return 0;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate fechaMasAntigua = LocalDate.now();

        for (AfiliacionManada m : manadas) {
            LocalDate fecha = LocalDate.parse(m.getFechaIngreso(), formatter);
            if (fecha.isBefore(fechaMasAntigua)) {
                fechaMasAntigua = fecha;
            }
        }

        return ChronoUnit.MONTHS.between(fechaMasAntigua, LocalDate.now());
    }

    private static double calcularPromedioCompromiso(List<AfiliacionManada> manadas) {
        if (manadas == null || manadas.isEmpty()) return 0;

        int total = 0;
        for (AfiliacionManada m : manadas) {
            total += m.getCompromiso();
        }
        return total / (double) manadas.size();
    }
}