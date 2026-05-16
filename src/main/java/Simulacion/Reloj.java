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
    private Thread hiloSimulacion;

    // ─── Control de ejecución ─────────────────────────────────────────────
    private static volatile boolean pausado           = false;
    private static volatile boolean recargarAlReanudar = false;          
    private boolean modoSilencioso = false;

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

    // ─── Control público ──────────────────────────────────────────────────
    public void setModoSilencioso(boolean valor) { this.modoSilencioso = valor; }
    public void pausar()   { pausado    = true;  }
    public void reanudar() { recargarAlReanudar = true; pausado = false; }
    public boolean isPausado() { return pausado; }

    // ─── Log condicional ──────────────────────────────────────────────────
    private void log(String mensaje) {
        if (!modoSilencioso) System.out.println(mensaje);
    }

    // ─── Arranque ─────────────────────────────────────────────────────────
    public void iniciar(int pausaSegundos) { 

        hiloSimulacion = Thread.currentThread();
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║      RELOJ THERANIA INICIADO     ║");
        System.out.println("╚══════════════════════════════════╝");

        cargarOCrearEstado();

        while (!Thread.currentThread().isInterrupted()) { // ← loop infinito
            avanzarMes();
            try {
                Thread.sleep(pausaSegundos * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void detener() {
    if (hiloSimulacion != null) {
        hiloSimulacion.interrupt(); // ← interrumpe el hilo de simulación
    }
}

    // ─── Cargar o crear estado ────────────────────────────────────────────
    private void cargarOCrearEstado() {
        List<Map<String, Object>> datosCiudadanos = CiudadanoServicio.cargarCiudadanos();

        if (datosCiudadanos.isEmpty()) {
            log("No hay datos previos. Creando simulacion desde cero...\n");
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
            log("Cargando estado previo (" + datosCiudadanos.size() + " ciudadanos)...\n");
            this.manadas    = reconstruirManadas();
            this.ciudadanos = reconstruirCiudadanos(datosCiudadanos);
            recuperarFecha(datosCiudadanos);
        }
    }

    // ─── Avanzar un mes ───────────────────────────────────────────────────
    private void avanzarMes() {

        if (recargarAlReanudar) {
            recargarAlReanudar = false;
            recargarDesdeJSON();
        }

        String encabezado = "[" + anioActual + " - " + NOMBRES_MESES[mesActual - 1] + "]";
        log("\n══════════════════════════════════════");
        log(encabezado);
        log("══════════════════════════════════════");

        List<String> eventos = new ArrayList<>();

        // 1 — Generar rituales del mes
        int ritualesDelMes = 30 + random.nextInt(20);
        generarRitualesMes(ritualesDelMes);
        log("Rituales generados este mes: " + ritualesDelMes);

        // 2 — Actualizar cada ciudadano
        String claveMes = anioActual + "-" + String.format("%02d", mesActual);
        for (CiudadanoTherian c : ciudadanos) {
            String rolAnterior    = c.getRol();
            double iaaAnterior    = c.getIAA();
            String manadaAnterior = obtenerNombreManadaActual(c);

            double nuevoIAA = CalculadoraIAA.calcularIAA(c);
            c.setIAA(nuevoIAA);
            c.registrarIAAMensual(claveMes, nuevoIAA);
            Encuentro.asignarRol(c);

            String nuevaManada = verificarCambioManada(c, nuevoIAA);

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

        // 3 — Imprimir eventos
        if (eventos.isEmpty()) {
            log("Sin cambios significativos este mes.");
        } else {
            log("Cambios del mes:");
            for (String evento : eventos) log(evento);
        }

        // 4 — Alfa Honorario
        try {
            CiudadanoTherian alfa = CalculadoraIAA.obtenerAlfaHonorario(manadas);
            log("\nAlfa Honorario: " + alfa.getNombre() + " " + alfa.getApellido()
                + " (" + alfa.getEspecieActual() + ")"
                + " | IAA: " + String.format("%.1f", alfa.getIAA()));
        } catch (TherianException e) {
            log("Sin Alfa Honorario este mes.");
        }

        // 5 — Reubicar ciudadanos en tránsito
        List<CiudadanoTherian> enTransito = new ArrayList<>(ManadaDePaso.getInstance().getMiembros());
        for (CiudadanoTherian c : enTransito) {
            int    mesesRequeridos  = 1;
            String fechaIngresoPaso = null;
            AfiliacionManada afPasoRef = null;

            for (AfiliacionManada a : c.getManadas()) {
                if (a.estaActivo() && a.getNombreManada().equals("Manada de Paso")) {
                    afPasoRef        = a;
                    mesesRequeridos  = a.getMesesTransicion() > 0 ? a.getMesesTransicion() : 1;
                    fechaIngresoPaso = a.getFechaIngreso();
                    break;
                }
            }

            if (fechaIngresoPaso != null && mesesDesde(fechaIngresoPaso) < mesesRequeridos) continue;

            Manada manadaNueva = obtenerManadaCorrecta(c, c.getIAA());
            if (manadaNueva != null && !manadaNueva.estaLlena()) {
                ManadaDePaso.getInstance().removerMiembro(c);
                if (afPasoRef != null) {
                    afPasoRef.setFechaSalida(anioActual + "-" + String.format("%02d", mesActual) + "-01");
                }
                AfiliacionManada af = new AfiliacionManada(
                    manadaNueva.getNombreManada(),
                    anioActual + "-" + String.format("%02d", mesActual) + "-01",
                    c.getRol(), c.getPuntuacionManada(), null);
                af.setMesesTransicion(0);
                for (AfiliacionManada a : c.getManadas()) {
                    if (a.estaActivo()) {
                        a.setFechaSalida(anioActual + "-" + String.format("%02d", mesActual) + "-01");
                        }
                    }
                try {
                    c.AgregarManada(af);
                    manadaNueva.agregarMiembro(c, c.getIAA());
                    String origen = afPasoRef != null && afPasoRef.getManadaOrigen() != null
                        ? afPasoRef.getManadaOrigen() : "?";
                    log("  ✓ " + c.getNombre() + " " + c.getApellido()
                        + " completó tránsito: " + origen + " → " + manadaNueva.getNombreManada());

                } catch (TherianException e) {
                    log("  [Autocorrección] Error al reubicar " + c.getNombre()
                        + " (" + e.getMessage() + ") — corrigiendo...");
                    try { Thread.sleep(100); } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }

                    if (e.getTipoError() == TherianException.TipoError.MANADA_ACTIVA) {
                        // Cerrar afiliaciones activas y reintentar
                        for (AfiliacionManada a : c.getManadas()) {
                            if (a.estaActivo()) {
                                a.setFechaSalida(anioActual + "-" + String.format("%02d", mesActual) + "-01");
                            }
                        }
                        try {
                            AfiliacionManada afRetry = new AfiliacionManada(
                                manadaNueva.getNombreManada(),
                                anioActual + "-" + String.format("%02d", mesActual) + "-01",
                                c.getRol(), c.getPuntuacionManada(), null);
                            c.AgregarManada(afRetry);
                            manadaNueva.agregarMiembro(c, c.getIAA());
                            log("  [Autocorrección] " + c.getNombre() + " reubicado correctamente.");
                        } catch (TherianException e2) {
                            log("  [Error persistente] " + c.getNombre() + " permanece en ManadaDePaso.");
                            ManadaDePaso.getInstance().agregarMiembro(c, c.getIAA());
                        }

                    } else if (e.getTipoError() == TherianException.TipoError.IAA_FUERA_DE_RANGO) {
                        // El IAA no encaja en esa manada: buscar la manada correcta según IAA actual
                        Manada manadaCorregida = obtenerManadaCorrecta(c, c.getIAA());
                        if (manadaCorregida != null && !manadaCorregida.estaLlena()) {
                            try {
                                AfiliacionManada afCorr = new AfiliacionManada(
                                    manadaCorregida.getNombreManada(),
                                    anioActual + "-" + String.format("%02d", mesActual) + "-01",
                                    c.getRol(), c.getPuntuacionManada(), null);
                                c.AgregarManada(afCorr);
                                manadaCorregida.agregarMiembro(c, c.getIAA());
                                log("  [Autocorrección] " + c.getNombre()
                                    + " redirigido a " + manadaCorregida.getNombreManada()
                                    + " (IAA real: " + String.format("%.2f", c.getIAA()) + ")");
                            } catch (TherianException e2) {
                                log("  [Error persistente] " + c.getNombre() + " permanece en ManadaDePaso.");
                                ManadaDePaso.getInstance().agregarMiembro(c, c.getIAA());
                            }
                        } else {
                            log("  [Sin manada disponible] " + c.getNombre() + " permanece en ManadaDePaso.");
                            ManadaDePaso.getInstance().agregarMiembro(c, c.getIAA());
                        }
                    }
                }
            }
        }

        // 6 — Guardar y avanzar
        guardarEstado();
        log("Estado guardado en JSON.");
        avanzarReloj();
    }

    // ─── Generar rituales del mes ──────────────────────────────────────────
    private void generarRitualesMes(int cantidad) {
        String[] especies = {"Lobo","Leon","Ciervo","Alce","Tigre","Halcon","Orca","Cebra","Foca","Paloma"};

        for (int i = 0; i < cantidad; i++) {
            String especie = especies[random.nextInt(especies.length)];
            Manada manada  = obtenerManadaPorEspecie(especie);

            Ritual ritual = new Ritual(
                "Ritual-" + NOMBRES_MESES[mesActual - 1] + "-" + (i + 1),
                Ritual.tiposPorEspecie(especie)[random.nextInt(2)],
                String.format("%04d-%02d-%02d", anioActual, mesActual, 1 + random.nextInt(28)),
                30 + random.nextInt(151),
                especie,
                manada != null ? manada.getNombreManada() : "Sin manada",
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

            if (manada != null) manada.agregarRitual(ritual);
            rituales.add(ritual);
        }
    }

    // ─── Verificar cambio de manada ───────────────────────────────────────
    private String verificarCambioManada(CiudadanoTherian ciudadano, double nuevoIAA) {

        if (ManadaDePaso.getInstance().getMiembros().contains(ciudadano)) return null;

        Manada manadaCorrecta = obtenerManadaCorrecta(ciudadano, nuevoIAA);

        if (manadaCorrecta == null) return null;

        boolean yaEsta = manadaCorrecta.getMiembros().contains(ciudadano);

        if (!yaEsta && !manadaCorrecta.estaLlena()) {

            // Capturar nombre de origen ANTES de remover
            String nombreOrigen = "Sin manada";
            for (Manada m : manadas) {
                if (m.getMiembros().contains(ciudadano)) {
                    nombreOrigen = m.getNombreManada();
                    m.getMiembros().remove(ciudadano);
                    for (AfiliacionManada a : ciudadano.getManadas()) {
                        if (a.estaActivo()) {
                            a.setFechaSalida(anioActual + "-"+ String.format("%02d", mesActual) + "-01");
                        }
                    }
                    break;
                }
            }

            // Meter en ManadaDePaso
            ManadaDePaso.getInstance().agregarMiembro(ciudadano, ciudadano.getIAA());

            // AfiliacionManada para "Manada de Paso" — NO para el destino todavía
            try {
                AfiliacionManada afTransito = new AfiliacionManada(
                    "Manada de Paso",
                    anioActual + "-" + String.format("%02d", mesActual) + "-01",
                    ciudadano.getRol(), ciudadano.getPuntuacionManada(), null);
                afTransito.setMesesTransicion(1);
                afTransito.setManadaOrigen(nombreOrigen);
                afTransito.setManadaDestino(manadaCorrecta.getNombreManada());
                ciudadano.AgregarManada(afTransito);
                return manadaCorrecta.getNombreManada();

            } catch (TherianException e) {
                // Autocorrección: cerrar todas las afiliaciones activas que quedaron abiertas
                log("  [Autocorrección] Afiliación activa detectada en "
                    + ciudadano.getNombre() + " — corrigiendo...");
                try { Thread.sleep(100); } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                for (AfiliacionManada a : ciudadano.getManadas()) {
                    if (a.estaActivo()) {
                        a.setFechaSalida(anioActual + "-" + String.format("%02d", mesActual) + "-01");
                    }
                }
                // Reintentar
                try {
                    AfiliacionManada afRetry = new AfiliacionManada(
                        "Manada de Paso",
                        anioActual + "-" + String.format("%02d", mesActual) + "-01",
                        ciudadano.getRol(), ciudadano.getPuntuacionManada(), null);
                    afRetry.setMesesTransicion(1);
                    afRetry.setManadaOrigen(nombreOrigen);
                    afRetry.setManadaDestino(manadaCorrecta.getNombreManada());
                    ciudadano.AgregarManada(afRetry);
                    log("  [Autocorrección] " + ciudadano.getNombre() + " corregido correctamente.");
                    return manadaCorrecta.getNombreManada();
                } catch (TherianException e2) {
                    log("  [Error persistente] No se pudo reubicar a " + ciudadano.getNombre()
                        + " — se mantiene en su manada actual.");
                }
            }
            return null;

        }
        return null;
    }

    // ─── Helpers ──────────────────────────────────────────────────────────
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
                default:       return Paloma.MANADA_COSTA;
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
            case "Tigre":  return Tigre.MANADA_CAZADORES;
            case "Halcon": return Halcon.MANADA_RAPACES;
            case "Orca":   return Orca.MANADA_PROFUNDIDAD;
            case "Cebra":  return Cebra.MANADA_RAYAS;
            case "Foca":   return Foca.MANADA_NIEVE;
            default:       return Paloma.MANADA_MENSAJERAS;
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
        if (mesActual > 12) { mesActual = 1; anioActual++; }
    }

    private void guardarEstado() {
        CiudadanoServicio.guardarCiudadanos(ciudadanos);
        ManadaServicio.guardarManadas(manadas);
        RitualServicio.guardarRituales(rituales);
        EspecieServicio.guardarEspecies(ciudadanos);
    }

    private long mesesDesde(String fechaIngreso) {
        try {
            String[] p = fechaIngreso.split("-");
            int anio = Integer.parseInt(p[0]);
            int mes  = Integer.parseInt(p[1]);
            return (long)(anioActual - anio) * 12 + (mesActual - mes);
        } catch (Exception e) { return 99; }
    }

    // ─── Reconstrucción desde JSON ────────────────────────────────────────
    private List<Manada> reconstruirManadas() {
        Manada[] todas = {
            Lobo.MANADA_SOMBRA,    Lobo.MANADA_LUNA,          Lobo.MANADA_AURORA,
            Leon.MANADA_DESIERTO,  Leon.MANADA_SAVANA,         Leon.MANADA_REAL,
            Ciervo.MANADA_ROCIO,   Ciervo.MANADA_ARBOLEDA,     Ciervo.MANADA_ALBA,
            Alce.MANADA_PRADERA,   Alce.MANADA_BOSQUE,         Alce.MANADA_CUMBRE,
            Tigre.MANADA_JUNGLA,   Tigre.MANADA_CAZADORES,     Tigre.MANADA_SIBERIA,
            Halcon.MANADA_VIENTO,  Halcon.MANADA_RAPACES,      Halcon.MANADA_AGUILA,
            Orca.MANADA_CORRIENTE, Orca.MANADA_PROFUNDIDAD,    Orca.MANADA_ABISMO,
            Cebra.MANADA_LLANURA,  Cebra.MANADA_RAYAS,         Cebra.MANADA_SAVANA_CEBRA,
            Foca.MANADA_GLACIAR,   Foca.MANADA_NIEVE,          Foca.MANADA_ARTICO,
            Paloma.MANADA_COSTA,   Paloma.MANADA_MENSAJERAS,   Paloma.MANADA_PALOMAR
        };
        for (Manada m : todas) m.getMiembros().clear();
        ManadaDePaso.getInstance().getMiembros().clear();

        List<Manada> lista = new ArrayList<>();
        for (Manada m : todas) lista.add(m);
        lista.add(ManadaDePaso.getInstance());
        return lista;
    }

    private List<CiudadanoTherian> reconstruirCiudadanos(List<Map<String, Object>> datos) {
        List<CiudadanoTherian> lista = new ArrayList<>();
        for (Map<String, Object> d : datos) {
            String especie  = (String) d.get("especie");
            String nombre   = (String) d.get("nombre");
            String apellido = (String) d.get("apellido");
            int    id       = ((Number) d.get("id")).intValue();
            String fecha    = (String) d.get("fechaNacimiento");
            String estado   = (String) d.get("estadoCiudadania");

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

            Object rc = d.get("ratioCaza");
            Object re = d.get("ratioEscape");
            if (rc != null) c.setRatioCaza(((Number) rc).doubleValue());
            if (re != null) c.setRatioEscape(((Number) re).doubleValue());

            List<Map<String, Object>> afiliaciones = (List<Map<String, Object>>) d.get("afiliaciones");
            if (afiliaciones != null) {
                for (Map<String, Object> a : afiliaciones) {
                    AfiliacionManada af = new AfiliacionManada(
                        (String) a.get("nombreManada"),
                        (String) a.get("fechaIngreso"),
                        (String) a.get("rol"),
                        ((Number) a.get("compromiso")).intValue(),
                        (String) a.get("fechaSalida"));
                    Object mt = a.get("mesesTransicion");
                    Object mo = a.get("manadaOrigen");
                    Object md = a.get("manadaDestino");
                    if (mt != null) af.setMesesTransicion(((Number) mt).intValue());
                    if (mo != null) af.setManadaOrigen((String) mo);
                    if (md != null) af.setManadaDestino((String) md);
                    try { c.AgregarManada(af); } catch (TherianException e) { }
                }
            }

            Manada manada = obtenerManadaCorrecta(c, c.getIAA());
            if (manada != null && !manada.estaLlena()) {
                try { manada.agregarMiembro(c, c.getIAA()); } catch (TherianException e) { }
            }

            List<Map<String, Object>> histIAA = (List<Map<String, Object>>) d.get("historialIAA");
            if (histIAA != null) {
                for (Map<String, Object> punto : histIAA) {
                    c.registrarIAAMensual((String) punto.get("mes"), ((Number) punto.get("iaa")).doubleValue());
                }
            }

            lista.add(c);
        }
        return lista;
    }

    private void recuperarFecha(List<Map<String, Object>> datos) {
        String fechaReciente = "2024-01-01";
        for (Map<String, Object> d : datos) {
            List<Map<String, Object>> afiliaciones = (List<Map<String, Object>>) d.get("afiliaciones");
            if (afiliaciones != null) {
                for (Map<String, Object> a : afiliaciones) {
                    String f = (String) a.get("fechaIngreso");
                    if (f != null && f.compareTo(fechaReciente) > 0) fechaReciente = f;
                }
            }
        }
        String[] partes = fechaReciente.split("-");
        this.anioActual = Integer.parseInt(partes[0]);
        this.mesActual  = Integer.parseInt(partes[1]);
        log("Continuando desde: " + NOMBRES_MESES[mesActual - 1] + " " + anioActual);
    }

    private void recargarDesdeJSON() {
        log("[Reloj] Recargando estado desde JSON...");
        List<Map<String, Object>> datos = CiudadanoServicio.cargarCiudadanos();
        if (datos.isEmpty()) return;
        for (Manada m : manadas) m.getMiembros().clear();
        ManadaDePaso.getInstance().getMiembros().clear();
        this.ciudadanos = reconstruirCiudadanos(datos);
        log("[Reloj] Estado recargado: " + ciudadanos.size() + " ciudadanos.");
    }

    public List<CiudadanoTherian> getCiudadanos() { return ciudadanos; }
    public List<Manada>           getManadas()    { return manadas;    }
}