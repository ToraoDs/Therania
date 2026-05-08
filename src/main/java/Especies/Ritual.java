package Especies;

import java.util.ArrayList;
import java.util.List;

public class Ritual {

    public enum TipoRitual {
        AULLIDO_COLECTIVO,
        CAMINATA_NOCTURNA,
        ASAMBLEA_DE_RUGIDOS,
        VUELO_CEREMONIAL,
        DANZA_DEL_AGUA,
        CEREMONIA_DE_MANADA
    }

    private String NombreRitual;
    private TipoRitual Tipo;
    private String Fecha;
    private int DuracionMinutos;
    private String EspecieAsociada;
    private List<CiudadanoTherian> Participantes;
    private double IntensidadPercibida; // escala 0.0 a 10.0
    private boolean Asistio;

    public Ritual(String NombreRitual, TipoRitual Tipo, String Fecha,
                  int DuracionMinutos, String EspecieAsociada,
                  double IntensidadPercibida) {
        this.NombreRitual = NombreRitual;
        this.Tipo = Tipo;
        this.Fecha = Fecha;
        this.DuracionMinutos = DuracionMinutos;
        this.EspecieAsociada = EspecieAsociada;
        this.IntensidadPercibida = IntensidadPercibida;
        this.Participantes = new ArrayList<>();
        this.Asistio = false;
    }

    // Agrega participante y registra el ritual en el ciudadano
    public void agregarParticipante(CiudadanoTherian ciudadano) {
        if (!Participantes.contains(ciudadano)) {
            Participantes.add(ciudadano);
            ciudadano.agregarRitual(this); // afiliación efectiva
        }
    }

    // Getters
    public String getNombreRitual() { return NombreRitual; }
    public TipoRitual getTipo() { return Tipo; }
    public String getFecha() { return Fecha; }
    public int getDuracionMinutos() { return DuracionMinutos; }
    public String getEspecieAsociada() { return EspecieAsociada; }
    public List<CiudadanoTherian> getParticipantes() { return Participantes; }
    public double getIntensidadPercibida() { return IntensidadPercibida; }
    public boolean isAsistio() { return Asistio; }

    // Setters
    public void setAsistio(boolean Asistio) { this.Asistio = Asistio; }
    public void setIntensidadPercibida(double v) { this.IntensidadPercibida = v; }
}