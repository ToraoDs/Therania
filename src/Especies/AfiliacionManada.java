package Especies;
public class AfiliacionManada {

    private String FechaIngreso;
    private String Rol;
    private int Compromiso;

    public AfiliacionManada(String FechaIngreso, String Rol, int Compromiso){
        this.FechaIngreso = FechaIngreso;
        this.Rol = Rol;
        this.Compromiso = Compromiso;
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

    public String getFechaIngreso(){
        return FechaIngreso;
    }

    public String getRol(){
        return Rol;
    } 

    public int getCompromiso(){
        return Compromiso;
    }
}
