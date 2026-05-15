package Servicios;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import Especies.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class RitualServicio {

    private static final String RUTA = "Archivos/rituales.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // ─── Guardar ─────────────────────────────────────────────────────────

    public static void guardarRituales(List<Ritual> rituales) {
        try {
            new File("Archivos").mkdirs();
            List<Map<String, Object>> datos = new ArrayList<>();

            for (Ritual r : rituales) {
                Map<String, Object> mapa = new LinkedHashMap<>();
                mapa.put("nombre",            r.getNombreRitual());
                mapa.put("tipo",              r.getTipo().name());
                mapa.put("fecha",             r.getFecha());
                mapa.put("duracionMinutos",   r.getDuracionMinutos());
                mapa.put("especieAsociada",   r.getEspecieAsociada());
                mapa.put("manadaResponsable", r.getManadaResponsable()); 
                mapa.put("intensidad",        r.getIntensidadPercibida());
                mapa.put("asistio",           r.isAsistio());

                // Solo IDs de participantes para no duplicar info
                List<String> idsParticipantes = new ArrayList<>();
                for (CiudadanoTherian c : r.getParticipantes()) {
                    idsParticipantes.add(c.getId());
                }
                mapa.put("participantes", idsParticipantes);

                datos.add(mapa);
            }

            FileWriter writer = new FileWriter(RUTA);
            gson.toJson(datos, writer);
            writer.close();
            System.out.println("Rituales guardados correctamente.");

        } catch (IOException e) {
            throw new TherianException(TherianException.TipoError.RITUAL_DUPLICADO,
                                       "Error al guardar rituales: " + e.getMessage());
        }
    }

    // ─── Cargar ──────────────────────────────────────────────────────────

    public static List<Map<String, Object>> cargarRituales() {
        try {
            File archivo = new File(RUTA);
            if (!archivo.exists()) {
                System.out.println("No existe archivo de rituales.");
                return new ArrayList<>();
            }

            FileReader reader = new FileReader(archivo);
            Type tipo = new TypeToken<List<Map<String, Object>>>(){}.getType();
            List<Map<String, Object>> datos = gson.fromJson(reader, tipo);
            reader.close();
            return datos;

        } catch (IOException e) {
            throw new TherianException(TherianException.TipoError.RITUAL_DUPLICADO,
                                       "Error al cargar rituales: " + e.getMessage());
        }
    }

    // ─── Buscar por especie ──────────────────────────────────────────────

    public static List<Map<String, Object>> buscarPorEspecie(String especie) {
        List<Map<String, Object>> todos = cargarRituales();
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Map<String, Object> r : todos) {
            if (especie.equalsIgnoreCase((String) r.get("especieAsociada"))) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    // ─── Buscar por tipo ─────────────────────────────────────────────────

    public static List<Map<String, Object>> buscarPorTipo(String tipo) {
        List<Map<String, Object>> todos = cargarRituales();
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Map<String, Object> r : todos) {
            if (tipo.equalsIgnoreCase((String) r.get("tipo"))) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    // ─── Historial por ciudadano ─────────────────────────────────────────

    public static List<Map<String, Object>> historialPorCiudadano(String idCiudadano) {
        List<Map<String, Object>> todos = cargarRituales();
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Map<String, Object> r : todos) {
            List<String> participantes = (List<String>) r.get("participantes");
            if (participantes != null && participantes.contains(idCiudadano)) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    // ─── Reporte mensual ─────────────────────────────────────────────────

    public static void reporteMensual() {
        List<Map<String, Object>> rituales = cargarRituales();
        Map<String, Integer> porMes = new LinkedHashMap<>();
        String[] meses = {"01","02","03","04","05","06",
                          "07","08","09","10","11","12"};
        for (String m : meses) porMes.put(m, 0);

        for (Map<String, Object> r : rituales) {
            String fecha = (String) r.get("fecha");
            if (fecha != null && fecha.length() >= 7) {
                String mes = fecha.substring(5, 7);
                porMes.put(mes, porMes.getOrDefault(mes, 0) + 1);
            }
        }

        System.out.println("\n===== RITUALES POR MES =====");
        String[] nombresMeses = {"Ene","Feb","Mar","Abr","May","Jun",
                                 "Jul","Ago","Sep","Oct","Nov","Dic"};
        int i = 0;
        for (Map.Entry<String, Integer> entry : porMes.entrySet()) {
            int barras = entry.getValue() / 2;
            String barra = "█".repeat(barras);
            System.out.printf("%-4s | %-25s %d%n", nombresMeses[i++], barra, entry.getValue());
        }
    }
}