package Simulacion;

import Especies.*;
import Manadas.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulacionTherania {

    private static final Random random = new Random();

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
        manadas.add(ManadaDePaso.getInstance());
    }

    // ─── Asignacion de ciudadanos a manadas ─────────────────────────────

    private void asignarCiudadanosAManadas() {
        for (CiudadanoTherian c : ciudadanos) {
            String fechaIngreso = generarFechaAleatoria(2020, 2024);
            int compromiso      = random.nextInt(101);
            Manada manda = obtenerManadaParaCiudadano(c);
            String nombreManada = (manda != null) ? manda.getNombreManada(): "Sin manada";
            AfiliacionManada afiliacion = new AfiliacionManada(nombreManada, fechaIngreso, "Miembro", compromiso, null);
            Manada manada = obtenerManadaParaCiudadano(c);
            if (manada != null && !manada.estaLlena()) {
                try {
                    c.AgregarManada(afiliacion);
                    manada.agregarMiembro(c, c.getIAA());
                } catch (TherianException e) {
                    // ciudadano ya tiene manada activa
                }
            }
        }
    }

    private Manada obtenerManadaParaCiudadano(CiudadanoTherian c) {
        String especie = c.getEspecieActual();
        double iaa     = c.getIAA();

        if (iaa <= 40) {
            switch (especie) {
                case "Lobo":   return Lobo.MANADA_SOMBRA;
                case "Leon":   return Leon.MANADA_DESIERTO;
                case "Ciervo": return Ciervo.MANADA_ROCIO;
                case "Alce":   return Alce.MANADA_PRADERA;
            }
        } else if (iaa <= 70) {
            switch (especie) {
                case "Lobo":   return Lobo.MANADA_LUNA;
                case "Leon":   return Leon.MANADA_SAVANA;
                case "Ciervo": return Ciervo.MANADA_ARBOLEDA;
                case "Alce":   return Alce.MANADA_PRADERA;
            }
        } else {
            switch (especie) {
                case "Lobo":   return Lobo.MANADA_AURORA;
                case "Leon":   return Leon.MANADA_REAL;
                case "Ciervo": return Ciervo.MANADA_ARBOLEDA;
                case "Alce":   return Alce.MANADA_CUMBRE;
            }
        }
        return null;
    }

    // ─── Generacion de rituales ──────────────────────────────────────────

    private void generarRituales() {
        Ritual.TipoRitual[] tipos = Ritual.TipoRitual.values();

        for (int i = 0; i < 500; i++) {
            String fecha           = generarFechaAleatoria(2024, 2024);
            Ritual.TipoRitual tipo = tipos[random.nextInt(tipos.length)];
            double intensidad      = 1.0 + (random.nextDouble() * 9.0);
            String especie         = obtenerEspecieAleatoria();
            int duracion           = 30 + random.nextInt(151);
            Manada manada = obtenerManadaPorEspecie(especie);
            String nombreManada = (manada != null) ? manada.getNombreManada() : "Sin manada";

            Ritual ritual = new Ritual(
                "Ritual-" + (i + 1),
                tipo,
                fecha,
                duracion,
                especie,
                nombreManada,  
                intensidad
            );
            int numParticipantes = 3 + random.nextInt(8);
            List<CiudadanoTherian> candidatos = filtrarPorEspecie(especie);
            for (int j = 0; j < Math.min(numParticipantes, candidatos.size()); j++) {
                CiudadanoTherian participante = candidatos.get(random.nextInt(candidatos.size()));
                ritual.agregarParticipante(participante);
                if (!participante.getRituales().isEmpty()) {
                    participante.getRituales()
                        .get(participante.getRituales().size() - 1)
                        .setAsistio(true);
                }
            }

            if (manada != null) manada.agregarRitual(ritual);

            rituales.add(ritual);
        }

        // Actualizar IAA y manada de todos después de los rituales
        for (CiudadanoTherian c : ciudadanos) {
            actualizarCiudadano(c);
        }
    }

    // ─── Actualizar IAA, rol y manada de un ciudadano ────────────────────

    private void actualizarCiudadano(CiudadanoTherian ciudadano) {
        // 1 - Recalcula IAA
        double nuevoIAA = CalculadoraIAA.calcularIAA(ciudadano);
        ciudadano.setIAA(nuevoIAA);

        // 2 - Reasigna rol
        Encuentro.asignarRol(ciudadano);

        // 3 - Verifica si debe cambiar de manada
        Manada manadaCorrecta = obtenerManadaParaCiudadano(ciudadano);
        if (manadaCorrecta == null) return;

        boolean yaEstaEnManadaCorrecta = manadaCorrecta.getMiembros().contains(ciudadano);
        if (!yaEstaEnManadaCorrecta && !manadaCorrecta.estaLlena()) {
            // Sale de manada actual
            for (Manada m : manadas) {
                if (m.getMiembros().contains(ciudadano)) {
                    m.getMiembros().remove(ciudadano);
                    for (AfiliacionManada a : ciudadano.getManadas()) {
                        if (a.estaActivo()) {
                            a.setFechaSalida(java.time.LocalDate.now().toString());
                        }
                    }
                    break;
                }
            }
            ManadaDePaso.getInstance().agregarMiembro(ciudadano, nuevoIAA);

            // Entra a nueva manada
            try {
                ManadaDePaso.getInstance().removerMiembro(ciudadano);
                AfiliacionManada nuevaAfiliacion = new AfiliacionManada(
                    manadaCorrecta.getNombreManada(),
                    java.time.LocalDate.now().toString(),
                    ciudadano.getRol(),
                    ciudadano.getPuntuacionManada(),
                    null
                );
                ciudadano.AgregarManada(nuevaAfiliacion);
                manadaCorrecta.agregarMiembro(ciudadano, nuevoIAA);
                System.out.println(ciudadano.getNombre() + " cambio a " + manadaCorrecta.getNombreManada());
            } catch (TherianException e) {
                // No se mueve si hay error
            }
        }
    }

    // ─── Calculo de IAA para todos ───────────────────────────────────────

    private void calcularIAADetodos() {
        for (CiudadanoTherian c : ciudadanos) {
            double iaa = CalculadoraIAA.calcularIAA(c);
            c.setIAA(iaa);
            Encuentro.asignarRol(c);
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────

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