package Especies;

public class Ritual {

    private String NombreRitual;
    private String Fecha;
    private int Duracion;
    private String EspecieAsociada;
    private boolean Asistio;

    public Ritual(String NombreRitual, String Fecha, int DuracionMinutos,
                  String EspecieAsociada, boolean Asistio) {
        this.NombreRitual = NombreRitual;
        this.Fecha = Fecha;
        this.Duracion = DuracionMinutos;
        this.EspecieAsociada = EspecieAsociada;
        this.Asistio = Asistio;
    }

    public String getNombreRitual() { return NombreRitual; }
    public String getFecha() { return Fecha; }
    public int getDuracionMinutos() { return Duracion; }
    public String getEspecieAsociada() { return EspecieAsociada; }
    public boolean isAsistio() { return Asistio; }

    public void setAsistio(boolean Asistio) { this.Asistio = Asistio; }
}