package Simulacion;

import Especies.*;
import Manadas.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulacionTherania {

    private static final Random random = new Random();

    // Datos de prueba
    private static final String[] NOMBRES = {
        "Aiden", "Luna", "Kira", "Dante", "Sora", "Nyx", "Raven", "Zion",
        "Lyra", "Orion", "Ember", "Storm", "Ash", "Nova", "Sage", "Blaze",
        "Cyan", "Dusk", "Echo", "Frost", "Gale", "Haze", "Iris", "Jade"
    };

    private static final String[] APELLIDOS = {
        "Nightwood", "Stormclaw", "Ashfire", "Moonwhisper", "Ironpelt",
        "Darkfang", "Silverrun", "Coldwater", "Embercrest", "Wildmane",
        "Thornback", "Swiftpaw", "Ravenmoor", "Duskfall", "Frostholm"
    };

    private List<CiudadanoTherian> ciudadanos;
    private List<Manada> manadas;
    private List<Ritual> rituales;

    public SimulacionTherania() {
        this.ciudadanos = new ArrayList<>();
        this.manadas    = new ArrayList<>();
        this.rituales   = new ArrayList<>();
    }

    // ─── Metodo principal ────────────────────────────────────────────────

    public void iniciar() {
        System.out.println("Iniciando simulacion de Therania...");
        generarCiudadanos();
        registrarManadas();
        asignarCiudadanosAManadas();
        generarRituales();
        calcularIAADetodos();
        System.out.println("Simulacion completada.");
        System.out.println("Ciudadanos: " + ciudadanos.size());
        System.out.println("Manadas activas: " + manadas.size());
        System.out.println("Rituales generados: " + rituales.size());
    }

    // ─── Generacion de ciudadanos ────────────────────────────────────────

    private void generarCiudadanos() {
        // 25 ciudadanos por especie = 100 total (4 especies)
        for (int i = 0; i < 25; i++) generarCiudadano("Lobo",   true);
        for (int i = 0; i < 25; i++) generarCiudadano("Leon",   true);
        for (int i = 0; i < 25; i++) generarCiudadano("Ciervo", false);
        for (int i = 0; i < 25; i++) generarCiudadano("Alce",   false);
    }

    private void generarCiudadano(String especie, boolean esPredador) {
        String nombre   = NOMBRES[random.nextInt(NOMBRES.length)];
        String apellido = APELLIDOS[random.nextInt(APELLIDOS.length)];
        String id       = especie.substring(0, 2).toUpperCase() + (ciudadanos.size() + 1);
        String fecha    = generarFechaAleatoria(1970, 2005);
        String estado   = "Activo";

        CiudadanoTherian ciudadano;
        switch (especie) {
            case "Lobo":   ciudadano = new Lobo(nombre, apellido, id, fecha, estado);   break;
            case "Leon":   ciudadano = new Leon(nombre, apellido, id, fecha, estado);   break;
            case "Ciervo": ciudadano = new Ciervo(nombre, apellido, id, fecha, estado); break;
            default:       ciudadano = new Alce(nombre, apellido, id, fecha, estado);   break;
        }

        ciudadano.setPuntuacionManada(random.nextInt(101));
        ciudadanos.add(ciudadano);
    }

    // ─── Registro de manadas ─────────────────────────────────────────────

    private void registrarManadas() {
        // 3 manadas por especie = 12 total, usamos 10 activas
        manadas.add(Lobo.MANADA_SOMBRA);
        manadas.add(Lobo.MANADA_LUNA);
        manadas.add(Lobo.MANADA_AURORA);
        manadas.add(Leon.MANADA_DESIERTO);
        manadas.add(Leon.MANADA_SAVANA);
        manadas.add(Leon.MANADA_REAL);
        manadas.add(Ciervo.MANADA_ROCIO);
        manadas.add(Ciervo.MANADA_ARBOLEDA);
        manadas.add(Alce.MANADA_PRADERA);
        manadas.add(Alce.MANADA_CUMBRE);
    }

    // ─── Asignacion de ciudadanos a manadas ─────────────────────────────

    private void asignarCiudadanosAManadas() {
        for (CiudadanoTherian c : ciudadanos) {
            String fechaIngreso = generarFechaAleatoria(2020, 2024);
            int compromiso      = random.nextInt(101);
            AfiliacionManada afiliacion = new AfiliacionManada(fechaIngreso, "Miembro", compromiso, null);

            Manada manada = obtenerManadaParaCiudadano(c);
            if (manada != null && !manada.estaLlena()) {
                try {
                    c.AgregarManada(afiliacion);
                    manada.agregarMiembro(c, c.getIAA());
                } catch (TherianException e) {
                    // ciudadano ya tiene manada activa, se omite
                }
            }
        }
    }

    private Manada obtenerManadaParaCiudadano(CiudadanoTherian c) {
        String especie = c.getEspecieActual();
        int indice     = random.nextInt(3);
        switch (especie) {
            case "Lobo":   return indice == 0 ? Lobo.MANADA_SOMBRA   : indice == 1 ? Lobo.MANADA_LUNA     : Lobo.MANADA_AURORA;
            case "Leon":   return indice == 0 ? Leon.MANADA_DESIERTO  : indice == 1 ? Leon.MANADA_SAVANA   : Leon.MANADA_REAL;
            case "Ciervo": return indice == 0 ? Ciervo.MANADA_ROCIO   : indice == 1 ? Ciervo.MANADA_ARBOLEDA : Ciervo.MANADA_ROCIO;
            case "Alce":   return indice == 0 ? Alce.MANADA_PRADERA   : Alce.MANADA_CUMBRE;
            default:       return null;
        }
    }

    // ─── Generacion de rituales ──────────────────────────────────────────

    private void generarRituales() {
        Ritual.TipoRitual[] tipos = Ritual.TipoRitual.values();

        for (int i = 0; i < 500; i++) {
            String fecha           = generarFechaAleatoria(2024, 2024);
            Ritual.TipoRitual tipo = tipos[random.nextInt(tipos.length)];
            double intensidad      = 1.0 + (random.nextDouble() * 9.0);
            String especie         = obtenerEspecieAleatoria();
            int duracion           = 30 + random.nextInt(151); // 30 a 180 minutos

            Ritual ritual = new Ritual(
                "Ritual-" + (i + 1), tipo, fecha, duracion, especie, intensidad
            );

            // Asignar entre 3 y 10 participantes aleatorios
            int numParticipantes = 3 + random.nextInt(8);
            List<CiudadanoTherian> candidatos = filtrarPorEspecie(especie);
            for (int j = 0; j < Math.min(numParticipantes, candidatos.size()); j++) {
                CiudadanoTherian participante = candidatos.get(random.nextInt(candidatos.size()));
                ritual.agregarParticipante(participante);
                participante.getRituales().get(participante.getRituales().size() - 1).setAsistio(true);
            }

            // Agregar el ritual a la manada correspondiente
            Manada manada = obtenerManadaPorEspecie(especie);
            if (manada != null) manada.agregarRitual(ritual);

            rituales.add(ritual);
        }
    }

    private List<CiudadanoTherian> filtrarPorEspecie(String especie) {
        List<CiudadanoTherian> resultado = new ArrayList<>();
        for (CiudadanoTherian c : ciudadanos) {
            if (c.getEspecieActual().equals(especie)) resultado.add(c);
        }
        return resultado;
    }

    private Manada obtenerManadaPorEspecie(String especie) {
        switch (especie) {
            case "Lobo":   return Lobo.MANADA_LUNA;
            case "Leon":   return Leon.MANADA_SAVANA;
            case "Ciervo": return Ciervo.MANADA_ARBOLEDA;
            case "Alce":   return Alce.MANADA_BOSQUE;
            default:       return null;
        }
    }

    private String obtenerEspecieAleatoria() {
        String[] especies = {"Lobo", "Leon", "Ciervo", "Alce"};
        return especies[random.nextInt(especies.length)];
    }

    // ─── Calculo de IAA para todos ───────────────────────────────────────

    private void calcularIAADetodos() {
        for (CiudadanoTherian c : ciudadanos) {
            double iaa = CalculadoraIAA.calcularIAA(c);
            c.setIAA(iaa);
        }
    }

    // ─── Fecha aleatoria ─────────────────────────────────────────────────

    private String generarFechaAleatoria(int anioInicio, int anioFin) {
        int anio = anioInicio + random.nextInt(anioFin - anioInicio + 1);
        int mes  = 1 + random.nextInt(12);
        int dia  = 1 + random.nextInt(28);
        return String.format("%04d-%02d-%02d", anio, mes, dia);
    }

    // ─── Getters ─────────────────────────────────────────────────────────

    public List<CiudadanoTherian> getCiudadanos() { return ciudadanos; }
    public List<Manada> getManadas()               { return manadas; }
    public List<Ritual> getRituales()              { return rituales; }
}