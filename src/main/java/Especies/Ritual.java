package Especies;

import java.util.ArrayList;
import java.util.List;

public class Ritual {

    public enum TipoRitual {
        AULLIDO_LUNAR, CAZA_EN_MANADA,           // Lobo
        RUGIDO_REAL, PATRULLA_TERRITORIAL,        // Leon
        DANZA_DEL_BOSQUE, CONTEMPLACION_NATURAL,  // Ciervo
        BRAMIDO_MONTANIA, MARCAJE_TERRITORIAL,    // Alce
        ACECHO_NOCTURNO, CEREMONIA_SOLITARIA,     // Tigre
        VUELO_SUPREMO, OBSERVACION_ALTA,          // Halcon
        CANTO_MARINO, CACERIA_GRUPAL,             // Orca
        GALOPE_LIBRE, VIGILANCIA_GRUPAL,          // Cebra
        ZAMBULLIDA_POLAR, JUEGO_GLACIAR,          // Foca
        VUELO_MENSAJERO, CIRCULO_DE_PAZ 
    }

    public static TipoRitual[] tiposPorEspecie(String especie) {
    switch (especie) {
        case "Lobo":   return new TipoRitual[]{TipoRitual.AULLIDO_LUNAR,    TipoRitual.CAZA_EN_MANADA};
        case "Leon":   return new TipoRitual[]{TipoRitual.RUGIDO_REAL,      TipoRitual.PATRULLA_TERRITORIAL};
        case "Ciervo": return new TipoRitual[]{TipoRitual.DANZA_DEL_BOSQUE, TipoRitual.CONTEMPLACION_NATURAL};
        case "Alce":   return new TipoRitual[]{TipoRitual.BRAMIDO_MONTANIA, TipoRitual.MARCAJE_TERRITORIAL};
        case "Tigre":  return new TipoRitual[]{TipoRitual.ACECHO_NOCTURNO,  TipoRitual.CEREMONIA_SOLITARIA};
        case "Halcon": return new TipoRitual[]{TipoRitual.VUELO_SUPREMO,    TipoRitual.OBSERVACION_ALTA};
        case "Orca":   return new TipoRitual[]{TipoRitual.CANTO_MARINO,     TipoRitual.CACERIA_GRUPAL};
        case "Cebra":  return new TipoRitual[]{TipoRitual.GALOPE_LIBRE,     TipoRitual.VIGILANCIA_GRUPAL};
        case "Foca":   return new TipoRitual[]{TipoRitual.ZAMBULLIDA_POLAR, TipoRitual.JUEGO_GLACIAR};
        default:       return new TipoRitual[]{TipoRitual.VUELO_MENSAJERO,  TipoRitual.CIRCULO_DE_PAZ};
        }
    }

    private String NombreRitual;
    private TipoRitual Tipo;
    private String Fecha;
    private int DuracionMinutos;
    private String EspecieAsociada;
    private String ManadaResponsable;  
    private List<CiudadanoTherian> Participantes;
    private double IntensidadPercibida; // escala 0.0 a 10.0
    private boolean Asistio;

    public Ritual(String NombreRitual, TipoRitual Tipo, String Fecha,
                  int DuracionMinutos, String EspecieAsociada,String ManadaResponsable,
                  double IntensidadPercibida) {
        this.NombreRitual = NombreRitual;
        this.Tipo = Tipo;
        this.Fecha = Fecha;
        this.DuracionMinutos = DuracionMinutos;
        this.EspecieAsociada = EspecieAsociada;
        this.ManadaResponsable   = ManadaResponsable; 
        this.IntensidadPercibida = IntensidadPercibida;
        this.Participantes = new ArrayList<>();
        this.Asistio = false;
    }

    // Agrega participante y registra el ritual en el ciudadano
    public void agregarParticipante(CiudadanoTherian ciudadano) {
        if (!Participantes.contains(ciudadano)) {
            Participantes.add(ciudadano);
            ciudadano.agregarRitual(this); // afiliación efectiva

            AfiliacionEfectiva ae = new AfiliacionEfectiva(
                ciudadano.getId(),
                this.NombreRitual,
                this.Fecha,
                this.IntensidadPercibida,
                true
            );
            ciudadano.agregarAfiliacionEfectiva(ae);
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
    public String getManadaResponsable() { return ManadaResponsable; }

    // Setters
    public void setAsistio(boolean Asistio) { this.Asistio = Asistio; }
    public void setIntensidadPercibida(double v) { this.IntensidadPercibida = v; }
    public void setManadaResponsable(String m) { this.ManadaResponsable = m; }
}