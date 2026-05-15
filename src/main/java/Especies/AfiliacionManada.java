package Especies;
public class AfiliacionManada {

    private String nombreManada;
    private String FechaIngreso;
    private String Rol;
    private int Compromiso;
    private String FechaSalida;
    private int    mesesTransicion; // 1 = misma especie, 2 = especie diferente
    private String manadaOrigen;
    private String manadaDestino;

    public AfiliacionManada(String nombreManada, String FechaIngreso, String Rol, int Compromiso, String FechaSalida){
        this.nombreManada = nombreManada;
        this.FechaIngreso = FechaIngreso;
        this.Rol = Rol;
        this.Compromiso = Compromiso;
        this.FechaSalida = FechaSalida;

    }

    // Setters
    public void setNombreManada(String nombreManada){
        this.nombreManada = nombreManada;
    }

    public void setFechaIngreso(String FechaIngreso){
        this.FechaIngreso = FechaIngreso;
    }
    
    public void setRol(String Rol){
        this.Rol = Rol;
    }

    public void setCompromiso(int Compromiso){
        this.Compromiso = Compromiso;
    }

    public void setFechaSalida(String FechaSalida){
        this.FechaSalida = FechaSalida;
    }
    public void setMesesTransicion(int meses)        { this.mesesTransicion = meses; }
    public void setManadaOrigen(String manadaOrigen)   { this.manadaOrigen  = manadaOrigen; }
    public void setManadaDestino(String manadaDestino) { this.manadaDestino = manadaDestino; }


    // Getters

    public String getNombreManada(){
        return nombreManada;
    }
    public String getFechaIngreso(){
        return FechaIngreso;
    }

    public String getRol(){
        return Rol;
    } 

    public int getCompromiso(){
        return Compromiso;
    }

    public String getFechaSalida(){
        return FechaSalida;
    }

    public boolean estaActivo(){
        return FechaSalida == null;
    }
    public int    getMesesTransicion() { return mesesTransicion; }
    public String getManadaOrigen()    { return manadaOrigen; }
    public String getManadaDestino()   { return manadaDestino; }

    
}
