package Especies;

public class AfiliacionEfectiva {

    private String IdCiudadano;
    private String NombreRitual;
    private String Fecha;
    private double IntensidadPercibida;
    private boolean Asistio;
    private double AporteIAA;

    public AfiliacionEfectiva(String IdCiudadano, String NombreRitual,
                               String Fecha, double IntensidadPercibida,
                               boolean Asistio) {
        this.IdCiudadano         = IdCiudadano;
        this.NombreRitual        = NombreRitual;
        this.Fecha               = Fecha;
        this.IntensidadPercibida = IntensidadPercibida;
        this.Asistio             = Asistio;
        this.AporteIAA           = Asistio ? (IntensidadPercibida * 0.1) : 0.0;
    }

    // Getters
    public String getIdCiudadano()         { return IdCiudadano; }
    public String getNombreRitual()        { return NombreRitual; }
    public String getFecha()               { return Fecha; }
    public double getIntensidadPercibida() { return IntensidadPercibida; }
    public boolean isAsistio()             { return Asistio; }
    public double getAporteIAA()           { return AporteIAA; }
}