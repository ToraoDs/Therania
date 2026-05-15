package Servicios;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import Especies.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class EspecieServicio {

    private static final String RUTA = "Archivos/especies.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // ─── Guardar ─────────────────────────────────────────────────────────

    public static void guardarEspecies(List<CiudadanoTherian> ciudadanos) {
        try {
            new File("Archivos").mkdirs();

            // Agrupa ciudadanos por especie sin duplicar
            Map<String, Map<String, Object>> especiesMap = new LinkedHashMap<>();

            for (CiudadanoTherian c : ciudadanos) {
                String especie = c.getEspecieActual();
                if (!especiesMap.containsKey(especie)) {
                    Map<String, Object> mapa = new LinkedHashMap<>();
                    mapa.put("nombre",      especie);
                    mapa.put("esPredador",  c.esPredador());
                    mapa.put("instinto",    c.describirInstinto());
                    mapa.put("totalCiudadanos", 0);
                    especiesMap.put(especie, mapa);
                }
                // Incrementa el contador
                int total = (int) especiesMap.get(especie).get("totalCiudadanos");
                especiesMap.get(especie).put("totalCiudadanos", total + 1);
            }

            FileWriter writer = new FileWriter(RUTA);
            gson.toJson(new ArrayList<>(especiesMap.values()), writer);
            writer.close();
            System.out.println("Especies guardadas correctamente.");

        } catch (IOException e) {
            throw new TherianException(TherianException.TipoError.CIUDADANO_NO_ENCONTRADO,
                                       "Error al guardar especies: " + e.getMessage());
        }
    }

    // ─── Cargar ──────────────────────────────────────────────────────────

    public static List<Map<String, Object>> cargarEspecies() {
        try {
            File archivo = new File(RUTA);
            if (!archivo.exists()) {
                System.out.println("No existe archivo de especies.");
                return new ArrayList<>();
            }

            FileReader reader = new FileReader(archivo);
            Type tipo = new TypeToken<List<Map<String, Object>>>(){}.getType();
            List<Map<String, Object>> datos = gson.fromJson(reader, tipo);
            reader.close();
            return datos;

        } catch (IOException e) {
            throw new TherianException(TherianException.TipoError.CIUDADANO_NO_ENCONTRADO,
                                       "Error al cargar especies: " + e.getMessage());
        }
    }

    // ─── Buscar por nombre ───────────────────────────────────────────────

    public static Map<String, Object> buscarPorNombre(String nombre) {
        List<Map<String, Object>> especies = cargarEspecies();
        for (Map<String, Object> e : especies) {
            if (nombre.equalsIgnoreCase((String) e.get("nombre"))) return e;
        }
        throw new TherianException(TherianException.TipoError.CIUDADANO_NO_ENCONTRADO,
                                   "Especie no encontrada: " + nombre);
    }

    // ─── Reporte ─────────────────────────────────────────────────────────

    public static void imprimirReporte() {
        List<Map<String, Object>> especies = cargarEspecies();
        System.out.println("\n===== REPORTE DE ESPECIES =====");
        for (Map<String, Object> e : especies) {
            System.out.println("\nEspecie: "    + e.get("nombre"));
            System.out.println("Tipo: "         + (((boolean) e.get("esPredador")) ? "Predador" : "Presa"));
            System.out.println("Ciudadanos: "   + e.get("totalCiudadanos"));
            System.out.println("Instinto: "     + e.get("instinto"));
        }
    }
}