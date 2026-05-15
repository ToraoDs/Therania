package Reportes;
import Servicios.*;
import java.util.*;

public class ConsultasReportes {

    // ─── 1. Ciudadanos por especie ────────────────────────────────────────

    public static void reporteCiudadanosPorEspecie(String especie) {
        List<Map<String, Object>> ciudadanos = CiudadanoServicio.buscarPorEspecie(especie);
        System.out.println("\n===== CIUDADANOS DE ESPECIE: " + especie.toUpperCase() + " =====");
        if (ciudadanos.isEmpty()) {
            System.out.println("No hay ciudadanos de esta especie.");
            return;
        }
        for (Map<String, Object> c : ciudadanos) {
            System.out.println("\nNombre:  " + c.get("nombre") + " " + c.get("apellido"));
            System.out.println("ID:      " + c.get("id"));
            System.out.println("Rol:     " + c.get("rol"));
            System.out.println("IAA:     " + c.get("iaa"));
            System.out.println("Estado:  " + c.get("estadoCiudadania"));
        }
        System.out.println("\nTotal: " + ciudadanos.size() + " ciudadanos.");
    }

    // ─── 2. Manadas con miembros y especie predominante ──────────────────

    public static void reporteManadas() {
        List<Map<String, Object>> manadas = ManadaServicio.cargarManadas();
        System.out.println("\n===== REPORTE DE MANADAS =====");
        if (manadas.isEmpty()) {
            System.out.println("No hay manadas registradas.");
            return;
        }
        for (Map<String, Object> m : manadas) {
            System.out.println("\nManada:      " + m.get("nombre"));
            System.out.println("Lema:        " + m.get("lema"));
            System.out.println("Territorio:  " + m.get("territorio"));
            System.out.println("Miembros:    " + m.get("totalMiembros") + "/" + m.get("cupoMaximo"));
            System.out.println("Rango IAA:   " + m.get("iaaMinimo") + " - " + m.get("iaaMaximo"));

            // Especie predominante entre los miembros
            @SuppressWarnings("unchecked")
            List<Object> ids = (List<Object>) m.get("miembros");
            System.out.println("Especie predominante: " + especiePredominante(ids));
        }
    }

    private static String especiePredominante(List<Object> idsMiembros) {
        if (idsMiembros == null || idsMiembros.isEmpty()) return "Sin miembros";

        List<Map<String, Object>> todos = CiudadanoServicio.cargarCiudadanos();
        Map<String, Integer> conteo = new HashMap<>();

        for (Object idObj : idsMiembros) {
            int idBuscado = ((Number) idObj).intValue();
            for (Map<String, Object> c : todos) {
                int idCiudadano = ((Number) c.get("id")).intValue();
                if (idCiudadano == idBuscado) {
                    String especie = (String) c.get("especie");
                    conteo.put(especie, conteo.getOrDefault(especie, 0) + 1);
                    break;
                }
            }
        }

        String predominante = "Sin miembros";
        int max = 0;
        for (Map.Entry<String, Integer> entry : conteo.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                predominante = entry.getKey();
            }
        }
        return predominante + " (" + max + " miembros)";
    }

    // ─── 3. Historial de rituales por manada ─────────────────────────────

    public static void historialRitualesPorManada(String nombreManada) {
        List<Map<String, Object>> rituales = RitualServicio.cargarRituales();
        System.out.println("\n===== RITUALES DE MANADA: " + nombreManada.toUpperCase() + " =====");

        int total = 0;
        for (Map<String, Object> r : rituales) {
            // Filtrar por manada responsable
            String manada = (String) r.get("manadaResponsable");
            if (manada == null || !manada.equalsIgnoreCase(nombreManada)) continue; // ← filtro

            System.out.println("\nRitual:       " + r.get("nombre"));
            System.out.println("Tipo:         " + r.get("tipo"));
            System.out.println("Fecha:        " + r.get("fecha"));
            System.out.println("Duracion:     " + r.get("duracionMinutos") + " min");
            System.out.println("Intensidad:   " + r.get("intensidad"));
            @SuppressWarnings("unchecked")
            List<Object> participantes = (List<Object>) r.get("participantes");
            System.out.println("Participantes:" + (participantes != null ? participantes.size() : 0));
            total++;
        }

        if (total == 0) System.out.println("No hay rituales para esta manada.");
        else System.out.println("\nTotal rituales: " + total);
    }

    // ─── 4. Historial de rituales por ciudadano ───────────────────────────

    public static void historialRitualesPorCiudadano(int idCiudadano) {
        List<Map<String, Object>> rituales = RitualServicio.historialPorCiudadano(idCiudadano);
        System.out.println("\n===== RITUALES DE CIUDADANO: " + idCiudadano + " =====");
        if (rituales.isEmpty()) {
            System.out.println("Sin rituales registrados.");
            return;
        }
        for (Map<String, Object> r : rituales) {
            System.out.println("\nRitual:     " + r.get("nombre"));
            System.out.println("Tipo:       " + r.get("tipo"));
            System.out.println("Fecha:      " + r.get("fecha"));
            System.out.println("Intensidad: " + r.get("intensidad"));
            System.out.println("Asistio:    " + (((boolean) r.get("asistio")) ? "Si" : "No"));
        }
        System.out.println("\nTotal rituales: " + rituales.size());
    }

    // ─── 5. Evolucion mensual del IAA ────────────────────────────────────

    public static void evolucionMensualIAA(int idCiudadano) {
        List<Map<String, Object>> rituales = RitualServicio.historialPorCiudadano(idCiudadano);

        Map<String, Integer> porMes = new LinkedHashMap<>();
        String[] meses = {"01","02","03","04","05","06",
                          "07","08","09","10","11","12"};
        String[] nombresMeses = {"Ene","Feb","Mar","Abr","May","Jun",
                                 "Jul","Ago","Sep","Oct","Nov","Dic"};
        for (String m : meses) porMes.put(m, 0);

        for (Map<String, Object> r : rituales) {
            Boolean asistio = (Boolean) r.get("asistio");
            String fecha = (String) r.get("fecha");
            if (Boolean.TRUE.equals(asistio) && fecha != null && fecha.length() >= 7) {
                String mes = fecha.substring(5, 7);
                porMes.put(mes, porMes.getOrDefault(mes, 0) + 1);
            }
        }

        System.out.println("\n===== EVOLUCION IAA MENSUAL: " + idCiudadano + " =====");
        int acumulado = 0;
        int i = 0;
        for (Map.Entry<String, Integer> entry : porMes.entrySet()) {
            acumulado += entry.getValue();
            double iaaEstimado = Math.min(acumulado, 50) / 50.0 * 100;
            int barras = (int)(iaaEstimado / 5);
            String barra = "█".repeat(barras);
            System.out.printf("%-4s | %-20s %.2f%n", nombresMeses[i++], barra, iaaEstimado);
        }
    }

    // ─── 6. Top 20 desde archivo ──────────────────────────────────────────

    public static void reporteTop20() {
    List<Map<String, Object>> ciudadanos = CiudadanoServicio.cargarCiudadanos();
  
    List<Map<String, Object>> rituales   = RitualServicio.cargarRituales();

    System.out.println("\n===== TOP 20 CIUDADANOS POR IAA =====");

    ciudadanos.sort((a, b) -> Double.compare(
        ((Number) b.get("iaa")).doubleValue(),
        ((Number) a.get("iaa")).doubleValue()
    ));

    int limite = Math.min(20, ciudadanos.size());
    for (int i = 0; i < limite; i++) {
        Map<String, Object> c = ciudadanos.get(i);
        int idC = ((Number) c.get("id")).intValue();

        // Manada actual (última afiliación sin fechaSalida)
        String manadaActual = "Sin manada";
        List<Map<String, Object>> afiliaciones =
            (List<Map<String, Object>>) c.get("afiliaciones");
        if (afiliaciones != null) {
            for (Map<String, Object> af : afiliaciones) {
                if (af.get("fechaSalida") == null) {
                    manadaActual = (String) af.get("nombreManada");
                }
            }
        }

        // Contar rituales y calcular intensidad promedio
        int totalRituales = 0;
        double sumaIntensidad = 0;
        for (Map<String, Object> r : rituales) {
            List<Object> participantes = (List<Object>) r.get("participantes");
            if (participantes != null) {
                for (Object p : participantes) {
                    if (((Number) p).intValue() == idC) {
                        totalRituales++;
                        sumaIntensidad += ((Number) r.get("intensidad")).doubleValue();
                        break;
                    }
                }
            }
        }
        double intensidadProm = totalRituales > 0 ? sumaIntensidad / totalRituales : 0;

        System.out.printf("%2d. %-22s | Especie: %-8s | IAA: %5.2f | Rol: %-10s | Manada: %-22s | Rituales: %3d | Int.Prom: %.1f%n",
            i + 1,
            c.get("nombre") + " " + c.get("apellido"),
            c.get("especie"),
            ((Number) c.get("iaa")).doubleValue(),
            c.get("rol"),
            manadaActual,
            totalRituales,
            intensidadProm
        );
    }
}
}