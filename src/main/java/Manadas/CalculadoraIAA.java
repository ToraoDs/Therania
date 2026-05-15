package Manadas;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import Especies.AfiliacionManada;
import Especies.CiudadanoTherian;
import Especies.TherianException;


public class CalculadoraIAA {

    private static final double PESO_RITUALES   = 0.30;
    private static final double PESO_TIEMPO     = 0.25;
    private static final double PESO_COMPROMISO = 0.25;
    private static final double PESO_PUNTUACION = 0.20;

    private static final int MAX_RITUALES   = 50;
    private static final int MAX_MESES      = 60;
    private static final int MAX_COMPROMISO = 100;
    private static final int MAX_PUNTUACION = 100;

    // ─── Calculo del IAA ────────────────────────────────────────────────

    public static double calcularIAA(CiudadanoTherian ciudadano) {

        int ritualesAsistidos = ciudadano.contarRitualesAsistidos();
        double puntajeRituales = Math.min(ritualesAsistidos, MAX_RITUALES) / (double) MAX_RITUALES;

        long mesesEnEspecie = calcularMesesEnEspecie(ciudadano.getManadas());
        double puntajeTiempo = Math.min(mesesEnEspecie, MAX_MESES) / (double) MAX_MESES;

        double promedioCompromiso = calcularPromedioCompromiso(ciudadano.getManadas());
        double puntajeCompromiso = Math.min(promedioCompromiso, MAX_COMPROMISO) / MAX_COMPROMISO;

        int puntuacionInterna = ciudadano.getPuntuacionManada();
        double puntajePuntuacion = Math.min(puntuacionInterna, MAX_PUNTUACION) / (double) MAX_PUNTUACION;

        double IAA = ((puntajeRituales   * PESO_RITUALES)   +
                      (puntajeTiempo     * PESO_TIEMPO)     +
                      (puntajeCompromiso * PESO_COMPROMISO) +
                      (puntajePuntuacion * PESO_PUNTUACION)) * 100;

        return Math.round(IAA * 100.0) / 100.0;
    }

    // ─── Helpers del calculo ────────────────────────────────────────────

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

    // ─── Elite y rankings ───────────────────────────────────────────────

    // Todos los miembros de manadas con IAA minimo >= 71
    public static List<CiudadanoTherian> obtenerCiudadanosElite(List<Manada> todasLasManadas) {
        List<CiudadanoTherian> elite = new ArrayList<>();
        for (Manada m : todasLasManadas) {
            if (m.getIAAMinimo() >= 71) {
                elite.addAll(m.getMiembros());
            }
        }
        return elite;
    }

    // Alfa Honorario: ciudadano con mayor IAA del grupo elite
    public static CiudadanoTherian obtenerAlfaHonorario(List<Manada> todasLasManadas) {
        List<CiudadanoTherian> todos = obtenerTodosLosCiudadanos(todasLasManadas);
        if (todos.isEmpty()) {
            throw new TherianException(TherianException.TipoError.CIUDADANO_NO_ENCONTRADO,
                                    "No hay ciudadanos en Therania");
        }
        CiudadanoTherian alfa = todos.get(0);
        for (CiudadanoTherian c : todos) {
            if (c.getIAA() > alfa.getIAA()) alfa = c;
        }
        return alfa;
    }


    // Ranking interno de una manada especifica
    public static List<CiudadanoTherian> rankingManada(Manada manada) {
        List<CiudadanoTherian> miembros = new ArrayList<>(manada.getMiembros());
        Collections.sort(miembros, new Comparator<CiudadanoTherian>() {
            public int compare(CiudadanoTherian a, CiudadanoTherian b) {
                return Double.compare(b.getIAA(), a.getIAA());
            }
        });
        return miembros;
    }

    public static List<CiudadanoTherian> obtenerTop20(List<Manada> todasLasManadas) {
    List<CiudadanoTherian> todos = obtenerTodosLosCiudadanos(todasLasManadas);
    if (todos.isEmpty()) {
        throw new TherianException(TherianException.TipoError.CIUDADANO_NO_ENCONTRADO,
                                   "No hay ciudadanos en Therania");
        }
    todos.sort((a, b) -> Double.compare(b.getIAA(), a.getIAA()));
    return todos.subList(0, Math.min(20, todos.size()));
    }

    private static List<CiudadanoTherian> obtenerTodosLosCiudadanos(List<Manada> todasLasManadas) {
    List<CiudadanoTherian> todos = new ArrayList<>();
    for (Manada m : todasLasManadas) {
        if (m instanceof ManadaDePaso) continue; // excluir ManadaDePaso del ranking
        for (CiudadanoTherian c : m.getMiembros()) {
            if (!todos.contains(c)) todos.add(c);
            }
        }
    return todos;
    }
}