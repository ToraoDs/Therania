package Especies;
public class AfiliacionManada {

    private String FechaIngreso;
    private String Rol;
    private int Compromiso;
    private String FechaSalida;

    public AfiliacionManada(String FechaIngreso, String Rol, int Compromiso, String FechaSalida){
        this.FechaIngreso = FechaIngreso;
        this.Rol = Rol;
        this.Compromiso = Compromiso;
        this.FechaSalida = FechaSalida;
    }

    // Setters
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
