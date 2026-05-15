package Especies;
public class AfiliacionManada {

    private String nombreManada;
    private String FechaIngreso;
    private String Rol;
    private int Compromiso;
    private String FechaSalida;

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

    public void setComprimiso(int Compromiso){
        this.Compromiso = Compromiso;
    }

    public void setFechaSalida(String FechaSalida){
        this.FechaSalida = FechaSalida;
    }


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

    
}
