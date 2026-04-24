import java.util.List;
import java.util.ArrayList;



public class CiudadanoTherian {
    private String Nombre;
    private String Apellido;
    private String Id;
    private String FechaNacimiento;
    private String EstadoCiudadania;
    private String EspecieActual;
    private List<AfiliacionManada> Manadas;


    public CiudadanoTherian(String Nombre, String Apellido, String Id, String FechaNacimiento, String EstadoCiudadania, String EspecieActual){
        this.Nombre = Nombre;
        this.Apellido = Apellido;
        this.Id = Id;
        this.FechaNacimiento = FechaNacimiento;
        this.EstadoCiudadania = EstadoCiudadania;
        this.EspecieActual = EspecieActual;
        this. Manadas = new ArrayList<>();
    }

    public void AgregarManada(AfiliacionManada Manada){
        Manadas.add(Manada);
    }

    public List<AfiliacionManada> getManadas(){
        return Manadas;
    }

    public String geTNombre(){
        return Nombre;
    }

    public String getApellido(){
        return Apellido;
    }

    public String getId(){
        return Id;
    }

    public String getFechaNacimiento(){
        return FechaNacimiento;
    }

    public String getEstadoCiudadania(){
        return EstadoCiudadania;
    }

    public String getEspecieActual(){
        return EspecieActual;
    }

    public void setEstadoCiudadania(String EstadoCiudadania){

        this.EstadoCiudadania = EstadoCiudadania;
    }

    public void setEspecieActual(String EspecieActual){
        this.EspecieActual = EspecieActual;
    }

}