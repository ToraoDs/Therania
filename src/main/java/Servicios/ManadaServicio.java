package Servicios;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import Especies.*;
import Manadas.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class ManadaServicio {

    private static final String RUTA = "Archivos/manadas.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // ─── Guardar ─────────────────────────────────────────────────────────

    public static void guardarManadas(List<Manada> manadas) {
        try {
            new File("Archivos").mkdirs();
            List<Map<String, Object>> datos = new ArrayList<>();

            for (Manada m : manadas) {
                Map<String, Object> mapa = new LinkedHashMap<>();
                mapa.put("nombre",      m.getNombreManada());
                mapa.put("especie",      m.getEspecie());
                mapa.put("descripcion", m.getDescripcion());
                mapa.put("lema",        m.getLema());
                mapa.put("territorio",  m.getTerritorio());
                mapa.put("cupoMaximo",  m.getCupoMaximo());
                mapa.put("iaaMinimo",   m.getIAAMinimo());
                mapa.put("iaaMaximo",   m.getIAAMaximo());
                mapa.put("totalMiembros", m.getMiembros().size());
                mapa.put("enTransito",   m instanceof ManadaDePaso);

                // IDs de los miembros para no duplicar info
                List<Integer> idsMiembros = new ArrayList<>();
                for (CiudadanoTherian c : m.getMiembros()) {
                    idsMiembros.add(c.getId());
                }
                mapa.put("miembros", idsMiembros);

                // Nombres de los rituales para no duplicar info
                List<String> nombresRituales = new ArrayList<>();
                for (Ritual r : m.getRituales()) {
                    nombresRituales.add(r.getNombreRitual());
                }
                mapa.put("rituales", nombresRituales);

                datos.add(mapa);

                Manada paso = ManadaDePaso.getInstance();
                if (!paso.getMiembros().isEmpty()) {}
            }

            FileWriter writer = new FileWriter(RUTA);
            gson.toJson(datos, writer);
            writer.close();
            System.out.println("Manadas guardadas correctamente.");

        } catch (IOException e) {
            throw new TherianException(TherianException.TipoError.MANADA_NO_ENCONTRADA,
                                       "Error al guardar manadas: " + e.getMessage());
        }
    }

    // ─── Cargar ──────────────────────────────────────────────────────────

    public static List<Map<String, Object>> cargarManadas() {
        try {
            File archivo = new File(RUTA);
            if (!archivo.exists()) {
                System.out.println("No existe archivo de manadas.");
                return new ArrayList<>();
            }

            FileReader reader = new FileReader(archivo);
            Type tipo = new TypeToken<List<Map<String, Object>>>(){}.getType();
            List<Map<String, Object>> datos = gson.fromJson(reader, tipo);
            reader.close();
            return datos;

        } catch (IOException e) {
            throw new TherianException(TherianException.TipoError.MANADA_NO_ENCONTRADA,
                                       "Error al cargar manadas: " + e.getMessage());
        }
    }

    // ─── Buscar por nombre ───────────────────────────────────────────────

    public static Map<String, Object> buscarPorNombre(String nombre) {
        List<Map<String, Object>> manadas = cargarManadas();
        for (Map<String, Object> m : manadas) {
            if (nombre.equalsIgnoreCase((String) m.get("nombre"))) return m;
        }
        throw new TherianException(TherianException.TipoError.MANADA_NO_ENCONTRADA,
                                   "Manada: " + nombre);
    }

    // ─── Reporte de manadas ──────────────────────────────────────────────

    public static void imprimirReporte() {
        List<Map<String, Object>> manadas = cargarManadas();
        System.out.println("\n===== REPORTE DE MANADAS =====");
        for (Map<String, Object> m : manadas) {
            System.out.println("\nManada: "    + m.get("nombre"));
            System.out.println("Lema: "        + m.get("lema"));
            System.out.println("Territorio: "  + m.get("territorio"));
            System.out.println("Miembros: "    + m.get("totalMiembros") + "/" + m.get("cupoMaximo"));
            System.out.println("Rango IAA: "   + m.get("iaaMinimo") + " - " + m.get("iaaMaximo"));
        }
    }
}