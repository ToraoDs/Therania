package Simulacion;

import Especies.*;
import Manadas.*;
import Servicios.*;
import java.util.*;

public class Reloj {

    private List<CiudadanoTherian> ciudadanos;
    private List<Manada> manadas;
    private List<Ritual> rituales;
    private int mesActual;
    private int anioActual;
    private static final Random random = new Random();

    private static final String[] NOMBRES_MESES = {
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };

    public Reloj() {
        this.ciudadanos = new ArrayList<>();
        this.manadas    = new ArrayList<>();
        this.rituales   = new ArrayList<>();
        this.mesActual  = 1;
        this.anioActual = 2024;
    }

    // ─── Arranque ────────────────────────────────────────────────────────

    public void iniciar(int mesesASimular, int pausaSegundos) {
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║      RELOJ THERANIA INICIADO     ║");
        System.out.println("╚══════════════════════════════════╝");

        cargarOCrearEstado();

        for (int i = 0; i < mesesASimular; i++) {
            avanzarMes();
            try {
                Thread.sleep(pausaSegundos * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║      SIMULACION COMPLETADA       ║");
        System.out.println("╚══════════════════════════════════╝");
    }

    // ─── Cargar o crear estado ───────────────────────────────────────────

    private void cargarOCrearEstado() {
        List<Map<String, Object>> datosCiudadanos = CiudadanoServicio.cargarCiudadanos();

        if (datosCiudadanos.isEmpty()) {
            System.out.println("No hay datos previos. Creando simulacion desde cero...\n");
            SimulacionTherania sim = new SimulacionTherania();
            sim.iniciar();
            this.ciudadanos = sim.getCiudadanos();
            this.manadas    = sim.getManadas();
            this.rituales   = sim.getRituales();
            if (!manadas.contains(ManadaDePaso.getInstance())) {
                manadas.add(ManadaDePaso.getInstance());
            }
            guardarEstado();
        } else {
            System.out.println("Cargando estado previo (" + datosCiudadanos.size() + " ciudadanos)...\n");
            // Reconstruir ciudadanos desde JSON
            this.manadas = reconstruirManadas();
            this.ciudadanos = reconstruirCiudadanos(datosCiudadanos);
            // Recuperar mes y anio del ultimo registro
            recuperarFecha(datosCiudadanos);
        }
    }

    // ─── Avanzar un mes ──────────────────────────────────────────────────

    private void avanzarMes() {
        String encabezado = "[" + anioActual + " - " + NOMBRES_MESES[mesActual - 1] + "]";
        System.out.println("\n══════════════════════════════════════");
        System.out.println(encabezado);
        System.out.println("══════════════════════════════════════");

        List<String> eventos = new ArrayList<>();

        // 1 - Generar rituales del mes
        int ritualesDelMes = 30 + random.nextInt(20); // entre 30 y 50 rituales por mes
        generarRitualesMes(ritualesDelMes);
        System.out.println("Rituales generados este mes: " + ritualesDelMes);

        // 2 - Actualizar cada ciudadano
        for (CiudadanoTherian c : ciudadanos) {
            String rolAnterior   = c.getRol();
            double iaaAnterior   = c.getIAA();
            String manadaAnterior = obtenerNombreManadaActual(c);

            // Recalcular IAA y rol
            double nuevoIAA = CalculadoraIAA.calcularIAA(c);
            c.setIAA(nuevoIAA);
            Encuentro.asignarRol(c);

            // Verificar cambio de manada
            String nuevaManada = verificarCambioManada(c, nuevoIAA);

            // Registrar eventos importantes
            if (!c.getRol().equals(rolAnterior)) {
                eventos.add("  ⬆ " + c.getNombre() + " " + c.getApellido()
                    + " cambio de rol: " + rolAnterior + " → " + c.getRol()
                    + " (IAA: " + String.format("%.1f", nuevoIAA) + ")");
            }
            if (nuevaManada != null && !nuevaManada.equals(manadaAnterior)) {
                eventos.add("  ↔ " + c.getNombre() + " " + c.getApellido()
                    + " cambio a " + nuevaManada
                    + " (IAA: " + String.format("%.1f", nuevoIAA) + ")");
            }
            if (Math.abs(nuevoIAA - iaaAnterior) >= 5.0) {
                String signo = nuevoIAA > iaaAnterior ? "↑" : "↓";
                eventos.add("  " + signo + " " + c.getNombre() + " " + c.getApellido()
                    + " IAA: " + String.format("%.1f", iaaAnterior)
                    + " → " + String.format("%.1f", nuevoIAA));
            }
        }

        // 3 - Imprimir eventos del mes
        if (eventos.isEmpty()) {
            System.out.println("Sin cambios significativos este mes.");
        } else {
            System.out.println("Cambios del mes:");
            for (String evento : eventos) {
                System.out.println(evento);
            }
        }

        // 4 - Imprimir Alfa Honorario del mes
        try {
            CiudadanoTherian alfa = CalculadoraIAA.obtenerAlfaHonorario(manadas);
            System.out.println("\nAlfa Honorario: " + alfa.getNombre()
                + " " + alfa.getApellido()
                + " (" + alfa.getEspecieActual() + ")"
                + " | IAA: " + String.format("%.1f", alfa.getIAA()));
        } catch (TherianException e) {
            System.out.println("Sin Alfa Honorario este mes.");
        }

            // Reubicar ciudadanos en tránsito
        List<CiudadanoTherian> enTransito = new ArrayList<>(
            ManadaDePaso.getInstance().getMiembros()
        );

        for (CiudadanoTherian c : enTransito) {
            Manada manadaNueva = obtenerManadaCorrecta(c, c.getIAA());
        if (manadaNueva != null && !manadaNueva.estaLlena()) {
            ManadaDePaso.getInstance().removerMiembro(c);
            AfiliacionManada af = new AfiliacionManada(
                manadaNueva.getNombreManada(),
                anioActual + "-" + String.format("%02d", mesActual) + "-01",
                c.getRol(),
                c.getPuntuacionManada(),
                null
            );
            try {
                c.AgregarManada(af);
                manadaNueva.agregarMiembro(c, c.getIAA());
                eventos.add("  ✓ " + c.getNombre() + " " + c.getApellido()
                    + " salió de ManadaDePaso → " + manadaNueva.getNombreManada());
                } 
                catch (TherianException e) { }
            }
        }

        // 5 - Guardar estado en JSON
        guardarEstado();
        System.out.println("Estado guardado en JSON.");

        // 6 - Avanzar reloj
        avanzarReloj();
    }

    // ─── Generar rituales del mes ─────────────────────────────────────────

    private void generarRitualesMes(int cantidad) {
        Ritual.TipoRitual[] tipos = Ritual.TipoRitual.values();
        String[] especies = {"Lobo", "Leon", "Ciervo", "Alce"};

        for (int i = 0; i < cantidad; i++) {
            String especie = especies[random.nextInt(especies.length)];
            Ritual ritual = new Ritual(
                "Ritual-" + NOMBRES_MESES[mesActual - 1] + "-" + (i + 1),
                tipos[random.nextInt(tipos.length)],
                String.format("%04d-%02d-%02d", anioActual, mesActual, 1 + random.nextInt(28)),
                30 + random.nextInt(151),
                especie,
                obtenerManadaPorEspecie(especie) != null
                    ? obtenerManadaPorEspecie(especie).getNombreManada()
                    : "Sin manada",   // ← nuevo argumento
                1.0 + random.nextDouble() * 9.0
            );

            List<CiudadanoTherian> candidatos = filtrarPorEspecie(especie);
            int numParticipantes = 3 + random.nextInt(8);
            for (int j = 0; j < Math.min(numParticipantes, candidatos.size()); j++) {
                CiudadanoTherian p = candidatos.get(random.nextInt(candidatos.size()));
                ritual.agregarParticipante(p);
                if (!p.getRituales().isEmpty()) {
                    p.getRituales().get(p.getRituales().size() - 1).setAsistio(true);
                }
            }

            Manada manada = obtenerManadaPorEspecie(especie);
            if (manada != null) manada.agregarRitual(ritual);
            rituales.add(ritual);
        }
    }

    // ─── Verificar cambio de manada ───────────────────────────────────────

    private String verificarCambioManada(CiudadanoTherian ciudadano, double nuevoIAA) {
        Manada manadaCorrecta = obtenerManadaCorrecta(ciudadano, nuevoIAA);
        if (manadaCorrecta == null) return null;

        boolean yaEsta = manadaCorrecta.getMiembros().contains(ciudadano);
        if (!yaEsta && !manadaCorrecta.estaLlena()) {
            for (Manada m : manadas) {
                if (m.getMiembros().contains(ciudadano)) {
                    m.getMiembros().remove(ciudadano);
                    ManadaDePaso.getInstance().agregarMiembro(ciudadano, ciudadano.getIAA());
                    for (AfiliacionManada a : ciudadano.getManadas()) {
                        if (a.estaActivo()) {
                            a.setFechaSalida(anioActual + "-"
                                + String.format("%02d", mesActual) + "-01");
                        }
                    }
                    break;
                }
            }
            try {
                AfiliacionManada nueva = new AfiliacionManada(manadaCorrecta.getNombreManada(), anioActual + "-" + String.format("%02d", mesActual) + "-01",ciudadano.getRol(),ciudadano.getPuntuacionManada(),null);
                ciudadano.AgregarManada(nueva);
                manadaCorrecta.agregarMiembro(ciudadano, nuevoIAA);
                return manadaCorrecta.getNombreManada();
            } catch (TherianException e) {
                return null;
            }
        }
        return null;
    }

    // ─── Helpers ─────────────────────────────────────────────────────────

    private String obtenerNombreManadaActual(CiudadanoTherian c) {
        for (Manada m : manadas) {
            if (m.getMiembros().contains(c)) return m.getNombreManada();
        }
        return "Sin manada";
    }

    private Manada obtenerManadaCorrecta(CiudadanoTherian c, double iaa) {
        String especie = c.getEspecieActual();
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

    private Manada obtenerManadaPorEspecie(String especie) {
        switch (especie) {
            case "Lobo":   return Lobo.MANADA_LUNA;
            case "Leon":   return Leon.MANADA_SAVANA;
            case "Ciervo": return Ciervo.MANADA_ARBOLEDA;
            case "Alce":   return Alce.MANADA_PRADERA;
            default:       return null;
        }
    }

    private List<CiudadanoTherian> filtrarPorEspecie(String especie) {
        List<CiudadanoTherian> resultado = new ArrayList<>();
        for (CiudadanoTherian c : ciudadanos) {
            if (c.getEspecieActual().equals(especie)) resultado.add(c);
        }
        return resultado;
    }

    private void avanzarReloj() {
        mesActual++;
        if (mesActual > 12) {
            mesActual = 1;
            anioActual++;
        }
    }

    private void guardarEstado() {
        CiudadanoServicio.guardarCiudadanos(ciudadanos);
        ManadaServicio.guardarManadas(manadas);
        RitualServicio.guardarRituales(rituales);
        EspecieServicio.guardarEspecies(ciudadanos);
    }

    // ─── Reconstruccion desde JSON ────────────────────────────────────────

    private List<Manada> reconstruirManadas() {
        List<Manada> lista = new ArrayList<>();
        lista.add(Lobo.MANADA_SOMBRA);
        lista.add(Lobo.MANADA_LUNA);
        lista.add(Lobo.MANADA_AURORA);
        lista.add(Leon.MANADA_DESIERTO);
        lista.add(Leon.MANADA_SAVANA);
        lista.add(Leon.MANADA_REAL);
        lista.add(Ciervo.MANADA_ROCIO);
        lista.add(Ciervo.MANADA_ARBOLEDA);
        lista.add(Alce.MANADA_PRADERA);
        lista.add(Alce.MANADA_CUMBRE);
        lista.add(Tigre.MANADA_JUNGLA);
        lista.add(Tigre.MANADA_CAZADORES);
        lista.add(Tigre.MANADA_SIBERIA);
        lista.add(Halcon.MANADA_VIENTO);
        lista.add(Halcon.MANADA_RAPACES);
        lista.add(Halcon.MANADA_AGUILA);
        lista.add(Orca.MANADA_CORRIENTE);
        lista.add(Orca.MANADA_PROFUNDIDAD);
        lista.add(Orca.MANADA_ABISMO);
        lista.add(Cebra.MANADA_LLANURA);
        lista.add(Cebra.MANADA_RAYAS);
        lista.add(Cebra.MANADA_SAVANA_CEBRA);
        lista.add(Foca.MANADA_GLACIAR);
        lista.add(Foca.MANADA_NIEVE);
        lista.add(Foca.MANADA_ARTICO);
        lista.add(Paloma.MANADA_COSTA);
        lista.add(Paloma.MANADA_MENSAJERAS);
        lista.add(Paloma.MANADA_PALOMAR);
        lista.add(ManadaDePaso.getInstance());
        return lista;
    }

    private List<CiudadanoTherian> reconstruirCiudadanos(List<Map<String, Object>> datos) {
        List<CiudadanoTherian> lista = new ArrayList<>();
        for (Map<String, Object> d : datos) {
            String especie = (String) d.get("especie");
            String nombre  = (String) d.get("nombre");
            String apellido= (String) d.get("apellido");
            String id      = (String) d.get("id");
            String fecha   = (String) d.get("fechaNacimiento");
            String estado  = (String) d.get("estadoCiudadania");

            CiudadanoTherian c;
            switch (especie) {
                case "Lobo":   c = new Lobo(nombre, apellido, id, fecha, estado);   break;
                case "Leon":   c = new Leon(nombre, apellido, id, fecha, estado);   break;
                case "Ciervo": c = new Ciervo(nombre, apellido, id, fecha, estado); break;
                default:       c = new Alce(nombre, apellido, id, fecha, estado);   break;
            }

            c.setIAA(((Number) d.get("iaa")).doubleValue());
            c.setRol((String) d.get("rol"));
            c.setPuntuacionManada(((Number) d.get("puntuacionManada")).intValue());

            // Restaurar afiliaciones
            List<Map<String, Object>> afiliaciones =
                (List<Map<String, Object>>) d.get("afiliaciones");
            if (afiliaciones != null) {
                for (Map<String, Object> a : afiliaciones) {
                    AfiliacionManada af = new AfiliacionManada(
                        (String) a.get("nombreManada"),
                        (String) a.get("fechaIngreso"),
                        (String) a.get("rol"),
                        ((Number) a.get("compromiso")).intValue(),
                        (String) a.get("fechaSalida")
                    );
                    try { c.AgregarManada(af); } catch (TherianException e) { }
                }
            }

            // Asignar a manada según IAA
            Manada manada = obtenerManadaCorrecta(c, c.getIAA());
            if (manada != null && !manada.estaLlena()) {
                try { manada.agregarMiembro(c, c.getIAA()); } catch (TherianException e) { }
            }

            lista.add(c);
        }
        return lista;
    }

    private void recuperarFecha(List<Map<String, Object>> datos) {
        // Busca la afiliacion mas reciente para estimar el mes actual
        String fechaReciente = "2024-01-01";
        for (Map<String, Object> d : datos) {
            List<Map<String, Object>> afiliaciones =
                (List<Map<String, Object>>) d.get("afiliaciones");
            if (afiliaciones != null) {
                for (Map<String, Object> a : afiliaciones) {
                    String f = (String) a.get("fechaIngreso");
                    if (f != null && f.compareTo(fechaReciente) > 0) {
                        fechaReciente = f;
                    }
                }
            }
        }
        String[] partes = fechaReciente.split("-");
        this.anioActual = Integer.parseInt(partes[0]);
        this.mesActual  = Integer.parseInt(partes[1]);
        System.out.println("Continuando desde: " + NOMBRES_MESES[mesActual - 1] + " " + anioActual);
    }
}