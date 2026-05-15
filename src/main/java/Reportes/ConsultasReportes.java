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
            List<String> ids = (List<String>) m.get("miembros");
            System.out.println("Especie predominante: " + especiePredominante(ids));
        }
    }

    private static String especiePredominante(List<String> idsMiembros) {
        if (idsMiembros == null || idsMiembros.isEmpty()) return "Sin miembros";

        Map<String, Integer> conteo = new HashMap<>();
        for (String id : idsMiembros) {
            // Los IDs empiezan con las iniciales de la especie (ej: LO1 = Lobo)
            String prefijo = id.substring(0, 2);
            String especie;
            switch (prefijo) {
                case "LO": especie = "Lobo";   break;
                case "LE": especie = "Leon";   break;
                case "CI": especie = "Ciervo"; break;
                case "AL": especie = "Alce";   break;
                default:   especie = "Otro";   break;
            }
            conteo.put(especie, conteo.getOrDefault(especie, 0) + 1);
        }

        String predominante = "";
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
            System.out.println("\nRitual:       " + r.get("nombre"));
            System.out.println("Tipo:         " + r.get("tipo"));
            System.out.println("Fecha:        " + r.get("fecha"));
            System.out.println("Duracion:     " + r.get("duracionMinutos") + " min");
            System.out.println("Intensidad:   " + r.get("intensidad"));
            List<String> participantes = (List<String>) r.get("participantes");
            System.out.println("Participantes:" + (participantes != null ? participantes.size() : 0));
            total++;
        }
        System.out.println("\nTotal rituales: " + total);
    }

    // ─── 4. Historial de rituales por ciudadano ───────────────────────────

    public static void historialRitualesPorCiudadano(String idCiudadano) {
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

    public static void evolucionMensualIAA(String idCiudadano) {
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
        System.out.println("\n===== TOP 20 CIUDADANOS POR IAA =====");

        // Ordenar por IAA de mayor a menor
        ciudadanos.sort((a, b) -> {
            double iaaA = ((Number) a.get("iaa")).doubleValue();
            double iaaB = ((Number) b.get("iaa")).doubleValue();
            return Double.compare(iaaB, iaaA);
        });

        int limite = Math.min(20, ciudadanos.size());
        for (int i = 0; i < limite; i++) {
            Map<String, Object> c = ciudadanos.get(i);
            System.out.printf("%2d. %-20s | Especie: %-8s | IAA: %.2f | Rol: %s%n",
                i + 1,
                c.get("nombre") + " " + c.get("apellido"),
                c.get("especie"),
                ((Number) c.get("iaa")).doubleValue(),
                c.get("rol")
            );
        }
    }
}