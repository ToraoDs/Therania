package Simulacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import Especies.AfiliacionManada;
import Especies.Alce;
import Especies.Cebra;
import Especies.Ciervo;
import Especies.CiudadanoTherian;
import Especies.Encuentro;
import Especies.Foca;
import Especies.Halcon;
import Especies.Leon;
import Especies.Lobo;
import Especies.Orca;
import Especies.Paloma;
import Especies.Ritual;
import Especies.TherianException;
import Especies.Tigre;
import Manadas.CalculadoraIAA;
import Manadas.Manada;
import Manadas.ManadaDePaso;
import Servicios.CiudadanoServicio;
import Servicios.EspecieServicio;
import Servicios.ManadaServicio;
import Servicios.RitualServicio;

public class Reloj {

    private List<CiudadanoTherian> ciudadanos;
    private List<Manada> manadas;
    private List<Ritual> rituales;
    private int mesActual;
    private int anioActual;
    private static final Random random = new Random();


    // ─── Control de pausa ────────────────────────────────────────────────────
    private static volatile boolean pausado = false;
    private static volatile boolean recargarAlReanudar = false;

    public static void pausar()   { pausado = true;  }
    public static void reanudar() {
    recargarAlReanudar = true;
    pausado = false;
}

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

        while (pausado) {
                try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
            }
        if (recargarAlReanudar) {
            recargarAlReanudar = false;
            recargarDesdeJSON();
        }

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
        String claveMes = anioActual + "-" + String.format("%02d", mesActual);
        for (CiudadanoTherian c : ciudadanos) {
            
            String rolAnterior   = c.getRol();
            double iaaAnterior   = c.getIAA();
            String manadaAnterior = obtenerNombreManadaActual(c);

            // Recalcular IAA y rol
            double nuevoIAA = CalculadoraIAA.calcularIAA(c);
            c.setIAA(nuevoIAA);
            c.registrarIAAMensual(claveMes, nuevoIAA);
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
            // Buscar afiliación activa en ManadaDePaso y sus datos
            int    mesesRequeridos  = 1;
            String fechaIngresoPaso = null;
            AfiliacionManada afPasoRef = null;
            for (AfiliacionManada a : c.getManadas()) {
                if (a.estaActivo() && a.getNombreManada().equals("Manada de Paso")) {
                    afPasoRef       = a;
                    mesesRequeridos = a.getMesesTransicion() > 0 ? a.getMesesTransicion() : 1;
                    fechaIngresoPaso = a.getFechaIngreso();
                    break;
                }
            }

            // Verificar si ya cumplió el tiempo de transición
            if (fechaIngresoPaso != null && mesesDesde(fechaIngresoPaso) < mesesRequeridos) {
                String origen  = afPasoRef != null && afPasoRef.getManadaOrigen()  != null ? afPasoRef.getManadaOrigen()  : "?";
                String destino = afPasoRef != null && afPasoRef.getManadaDestino() != null ? afPasoRef.getManadaDestino() : "?";
                eventos.add( c.getNombre() + " " + c.getApellido()
                    + " en tránsito (" + origen + " → " + destino + ")"
                    + " — " + mesesDesde(fechaIngresoPaso) + "/" + mesesRequeridos + " mes(es)");
                continue;
            }

            Manada manadaNueva = obtenerManadaCorrecta(c, c.getIAA());
            if (manadaNueva != null && !manadaNueva.estaLlena()) {
                ManadaDePaso.getInstance().removerMiembro(c);
                // Cerrar afiliación de ManadaDePaso
                if (afPasoRef != null) {
                    afPasoRef.setFechaSalida(anioActual + "-" + String.format("%02d", mesActual) + "-01");
                }
                AfiliacionManada af = new AfiliacionManada(
                    manadaNueva.getNombreManada(),
                    anioActual + "-" + String.format("%02d", mesActual) + "-01",
                    c.getRol(), c.getPuntuacionManada(), null);
                af.setMesesTransicion(0);
                try {
                    c.AgregarManada(af);
                    manadaNueva.agregarMiembro(c, c.getIAA());
                    String origen = afPasoRef != null && afPasoRef.getManadaOrigen() != null ? afPasoRef.getManadaOrigen() : "?";
                    eventos.add("  ✓ " + c.getNombre() + " " + c.getApellido()
                        + " completó tránsito: " + origen + " → " + manadaNueva.getNombreManada());
                } catch (TherianException e) { }
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
    
        String[] especies = {"Lobo","Leon","Ciervo","Alce","Tigre","Halcon","Orca","Cebra","Foca","Paloma"};

        for (int i = 0; i < cantidad; i++) {
            String especie = especies[random.nextInt(especies.length)];
            Ritual ritual = new Ritual(
                "Ritual-" + NOMBRES_MESES[mesActual - 1] + "-" + (i + 1),
                Ritual.tiposPorEspecie(especie)[random.nextInt(2)],
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
            String nombreOrigen = "Sin manada";
            for (Manada m : manadas) {
                if (m.getMiembros().contains(ciudadano)) { nombreOrigen = m.getNombreManada(); break; }
            }
            try {
                AfiliacionManada nuevaAf = new AfiliacionManada(
                    manadaCorrecta.getNombreManada(),
                    anioActual + "-" + String.format("%02d", mesActual) + "-01",
                    ciudadano.getRol(), ciudadano.getPuntuacionManada(), null);
                nuevaAf.setMesesTransicion(1);
                nuevaAf.setManadaOrigen(nombreOrigen);
                nuevaAf.setManadaDestino(manadaCorrecta.getNombreManada());
                ciudadano.AgregarManada(nuevaAf);
                manadaCorrecta.agregarMiembro(ciudadano, nuevoIAA);
                return manadaCorrecta.getNombreManada();
            } catch (TherianException e) {
                return null;
            }
        }

        return null; // ← esta línea es la que faltaba
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
                case "Tigre":  return Tigre.MANADA_JUNGLA;
                case "Halcon": return Halcon.MANADA_VIENTO;
                case "Orca":   return Orca.MANADA_CORRIENTE;
                case "Cebra":  return Cebra.MANADA_LLANURA;
                case "Foca":   return Foca.MANADA_GLACIAR;
                default:   return Paloma.MANADA_COSTA;
            }

        } else if (iaa <= 70) {

            switch (especie) {
                case "Lobo":   return Lobo.MANADA_LUNA;
                case "Leon":   return Leon.MANADA_SAVANA;
                case "Ciervo": return Ciervo.MANADA_ARBOLEDA;
                case "Alce":   return Alce.MANADA_BOSQUE;
                case "Tigre":  return Tigre.MANADA_CAZADORES;
                case "Halcon": return Halcon.MANADA_RAPACES;
                case "Orca":   return Orca.MANADA_PROFUNDIDAD;
                case "Cebra":  return Cebra.MANADA_RAYAS;
                case "Foca":   return Foca.MANADA_NIEVE;
                default:       return Paloma.MANADA_MENSAJERAS;
            }

        } else {

            switch (especie) {
                case "Lobo":   return Lobo.MANADA_AURORA;
                case "Leon":   return Leon.MANADA_REAL;
                case "Ciervo": return Ciervo.MANADA_ALBA;
                case "Alce":   return Alce.MANADA_CUMBRE;
                case "Tigre":  return Tigre.MANADA_SIBERIA;
                case "Halcon": return Halcon.MANADA_AGUILA;
                case "Orca":   return Orca.MANADA_ABISMO;
                case "Cebra":  return Cebra.MANADA_SAVANA_CEBRA;
                case "Foca":   return Foca.MANADA_ARTICO;
                default:       return Paloma.MANADA_PALOMAR;
            }
        }
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
        Manada[] todas = {
            // Lobo
            Lobo.MANADA_SOMBRA,   Lobo.MANADA_LUNA,          Lobo.MANADA_AURORA,
            // Leon
            Leon.MANADA_DESIERTO, Leon.MANADA_SAVANA,         Leon.MANADA_REAL,
            // Ciervo
            Ciervo.MANADA_ROCIO,  Ciervo.MANADA_ARBOLEDA,     Ciervo.MANADA_ALBA,
            // Alce
            Alce.MANADA_PRADERA,  Alce.MANADA_BOSQUE,         Alce.MANADA_CUMBRE,
            // Tigre
            Tigre.MANADA_JUNGLA,  Tigre.MANADA_CAZADORES,     Tigre.MANADA_SIBERIA,
            // Halcon
            Halcon.MANADA_VIENTO, Halcon.MANADA_RAPACES,       Halcon.MANADA_AGUILA,
            // Orca
            Orca.MANADA_CORRIENTE,Orca.MANADA_PROFUNDIDAD,    Orca.MANADA_ABISMO,
            // Cebra
            Cebra.MANADA_LLANURA, Cebra.MANADA_RAYAS,         Cebra.MANADA_SAVANA_CEBRA,
            // Foca
            Foca.MANADA_GLACIAR,  Foca.MANADA_NIEVE,          Foca.MANADA_ARTICO,
            // Paloma
            Paloma.MANADA_COSTA,  Paloma.MANADA_MENSAJERAS,   Paloma.MANADA_PALOMAR
        };

        for (Manada m : todas) m.getMiembros().clear();

        ManadaDePaso.getInstance().getMiembros().clear();

        List<Manada> lista = new ArrayList<>();
        lista.add(Lobo.MANADA_SOMBRA);
        lista.add(Lobo.MANADA_LUNA);
        lista.add(Lobo.MANADA_AURORA);
        lista.add(Leon.MANADA_DESIERTO);
        lista.add(Leon.MANADA_SAVANA);
        lista.add(Leon.MANADA_REAL);
        lista.add(Ciervo.MANADA_ROCIO);
        lista.add(Ciervo.MANADA_ARBOLEDA);
        lista.add(Ciervo.MANADA_ALBA);
        lista.add(Alce.MANADA_PRADERA);
        lista.add(Alce.MANADA_BOSQUE);
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
            String apellido = (String) d.get("apellido");
            int id = ((Number) d.get("id")).intValue();
            String fecha   = (String) d.get("fechaNacimiento");
            String estado  = (String) d.get("estadoCiudadania");

            CiudadanoTherian c;
            switch (especie) {
                case "Lobo":   c = new Lobo(nombre, apellido, id, fecha, estado);   break;
                case "Leon":   c = new Leon(nombre, apellido, id, fecha, estado);   break;
                case "Ciervo": c = new Ciervo(nombre, apellido, id, fecha, estado); break;
                case "Alce":   c = new Alce(nombre, apellido, id, fecha, estado);   break;
                case "Tigre":  c = new Tigre(nombre, apellido, id, fecha, estado);  break;
                case "Halcon": c = new Halcon(nombre, apellido, id, fecha, estado); break;
                case "Orca":   c = new Orca(nombre, apellido, id, fecha, estado);   break;
                case "Cebra":  c = new Cebra(nombre, apellido, id, fecha, estado);  break;
                case "Foca":   c = new Foca(nombre, apellido, id, fecha, estado);   break;
                default:       c = new Paloma(nombre, apellido, id, fecha, estado); break;
            }

            c.setIAA(((Number) d.get("iaa")).doubleValue());
            c.setRol((String) d.get("rol"));
            c.setPuntuacionManada(((Number) d.get("puntuacionManada")).intValue());

            Object ratioCazaObj   = d.get("ratioCaza");
            Object ratioEscapeObj = d.get("ratioEscape");
            if (ratioCazaObj   != null) c.setRatioCaza(((Number) ratioCazaObj).doubleValue());
            if (ratioEscapeObj != null) c.setRatioEscape(((Number) ratioEscapeObj).doubleValue());

            // Restaurar afiliaciones
            List<Map<String, Object>> afiliaciones = (List<Map<String, Object>>) d.get("afiliaciones");
            if (afiliaciones != null) {
                for (Map<String, Object> a : afiliaciones) {
                    AfiliacionManada af = new AfiliacionManada(
                        (String) a.get("nombreManada"),
                        (String) a.get("fechaIngreso"),
                        (String) a.get("rol"),
                        ((Number) a.get("compromiso")).intValue(),
                        (String) a.get("fechaSalida")
                    );
                    // Leer campos nuevos si existen
                    Object mt = a.get("mesesTransicion");
                    Object mo = a.get("manadaOrigen");
                    Object md = a.get("manadaDestino");
                    if (mt != null) af.setMesesTransicion(((Number) mt).intValue());
                    if (mo != null) af.setManadaOrigen((String) mo);
                    if (md != null) af.setManadaDestino((String) md);
                    try { c.AgregarManada(af); } catch (TherianException e) { }
                }
            }

            // Asignar a manada según IAA
            Manada manada = obtenerManadaCorrecta(c, c.getIAA());
            if (manada != null && !manada.estaLlena()) {
                try { manada.agregarMiembro(c, c.getIAA()); } catch (TherianException e) { }
            }

            // Restaurar historialIAA ← agregar aquí
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> histIAA =
                (List<Map<String, Object>>) d.get("historialIAA");
            if (histIAA != null) {
                for (Map<String, Object> punto : histIAA) {
                    String mes = (String) punto.get("mes");
                    double iaa = ((Number) punto.get("iaa")).doubleValue();
                    c.registrarIAAMensual(mes, iaa);
                }
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
    private void recargarDesdeJSON() {
        System.out.println("[Reloj] Recargando estado desde JSON...");
        List<Map<String, Object>> datos = CiudadanoServicio.cargarCiudadanos();
        if (datos.isEmpty()) return;

        // Limpiar miembros de todas las manadas
        for (Manada m : manadas) m.getMiembros().clear();
        ManadaDePaso.getInstance().getMiembros().clear();

        // Reconstruir lista de ciudadanos con datos actualizados
        this.ciudadanos = reconstruirCiudadanos(datos);
        System.out.println("[Reloj] Estado recargado: " + ciudadanos.size() + " ciudadanos.");
    }

    private long mesesDesde(String fechaIngreso) {
    try {
        String[] p = fechaIngreso.split("-");
        int anio = Integer.parseInt(p[0]);
        int mes  = Integer.parseInt(p[1]);
        return (long)(anioActual - anio) * 12 + (mesActual - mes);
    } catch (Exception e) {
        return 99;
    }
    }

    public List<CiudadanoTherian> getCiudadanos() { return ciudadanos; }
    public List<Manada>           getManadas()    { return manadas;    }

}