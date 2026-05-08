package Servicios;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import Especies.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class CiudadanoServicio {

    private static final String RUTA = "Archivos/ciudadanos.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // ─── Guardar ─────────────────────────────────────────────────────────

    public static void guardarCiudadanos(List<CiudadanoTherian> ciudadanos) {
        try {
            new File("Archivos").mkdirs();
            List<Map<String, Object>> datos = new ArrayList<>();

            for (CiudadanoTherian c : ciudadanos) {
                Map<String, Object> mapa = new LinkedHashMap<>();
                mapa.put("nombre",            c.getNombre());
                mapa.put("apellido",          c.getApellido());
                mapa.put("id",                c.getId());
                mapa.put("fechaNacimiento",   c.getFechaNacimiento());
                mapa.put("estadoCiudadania",  c.getEstadoCiudadania());
                mapa.put("especie",           c.getEspecieActual());
                mapa.put("esPredador",        c.esPredador());
                mapa.put("rol",               c.getRol());
                mapa.put("iaa",               c.getIAA());
                mapa.put("puntuacionManada",  c.getPuntuacionManada());
                mapa.put("ratioCaza",         c.getRatioCaza());
                mapa.put("ratioEscape",       c.getRatioEscape());
                mapa.put("inicioCargoAlfa",   c.getInicioCargoAlfa());
                mapa.put("duracionCargoMeses",c.getDuracionCargoMeses());

                // Historial de afiliaciones
                List<Map<String, Object>> afiliaciones = new ArrayList<>();
                for (AfiliacionManada a : c.getManadas()) {
                    Map<String, Object> af = new LinkedHashMap<>();
                    af.put("fechaIngreso", a.getFechaIngreso());
                    af.put("fechaSalida",  a.getFechaSalida());
                    af.put("rol",          a.getRol());
                    af.put("compromiso",   a.getCompromiso());
                    afiliaciones.add(af);
                }
                mapa.put("afiliaciones", afiliaciones);

                datos.add(mapa);
            }

            FileWriter writer = new FileWriter(RUTA);
            gson.toJson(datos, writer);
            writer.close();
            System.out.println("Ciudadanos guardados correctamente.");

        } catch (IOException e) {
            throw new TherianException(TherianException.TipoError.CIUDADANO_NO_ENCONTRADO,
                                       "Error al guardar ciudadanos: " + e.getMessage());
        }
    }

    // ─── Cargar ──────────────────────────────────────────────────────────

    public static List<Map<String, Object>> cargarCiudadanos() {
        try {
            File archivo = new File(RUTA);
            if (!archivo.exists()) {
                System.out.println("No existe archivo de ciudadanos.");
                return new ArrayList<>();
            }

            FileReader reader = new FileReader(archivo);
            Type tipo = new TypeToken<List<Map<String, Object>>>(){}.getType();
            List<Map<String, Object>> datos = gson.fromJson(reader, tipo);
            reader.close();
            return datos;

        } catch (IOException e) {
            throw new TherianException(TherianException.TipoError.CIUDADANO_NO_ENCONTRADO,
                                       "Error al cargar ciudadanos: " + e.getMessage());
        }
    }

    // ─── Buscar por ID ───────────────────────────────────────────────────

    public static Map<String, Object> buscarPorId(String id) {
        List<Map<String, Object>> ciudadanos = cargarCiudadanos();
        for (Map<String, Object> c : ciudadanos) {
            if (id.equals(c.get("id"))) return c;
        }
        throw new TherianException(TherianException.TipoError.CIUDADANO_NO_ENCONTRADO,
                                   "ID: " + id);
    }

    // ─── Buscar por especie ──────────────────────────────────────────────

    public static List<Map<String, Object>> buscarPorEspecie(String especie) {
        List<Map<String, Object>> todos = cargarCiudadanos();
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Map<String, Object> c : todos) {
            if (especie.equalsIgnoreCase((String) c.get("especie"))) {
                resultado.add(c);
            }
        }
        return resultado;
    }
}