package Especies;
import java.util.List;
import java.util.ArrayList;

public abstract class CiudadanoTherian extends EspecieAutoPercibida{
    private String Nombre;
    private String Apellido;
    private String Id;
    private String FechaNacimiento;
    private String EstadoCiudadania;
    private List<String> HistorialEspecie;
    private List<AfiliacionManada> Manadas;


    public CiudadanoTherian(String Nombre, String Apellido, String Id, String FechaNacimiento, String EstadoCiudadania, String EspecieActual){
        
        super(EspecieActual);
        this.Nombre = Nombre;
        this.Apellido = Apellido;
        this.Id = Id;
        this.FechaNacimiento = FechaNacimiento;
        this.EstadoCiudadania = EstadoCiudadania;
        this.HistorialEspecie = new ArrayList<>();
        this.Manadas = new ArrayList<>();
    }

    public void AgregarManada(AfiliacionManada Manada){
        Manadas.add(Manada);
    }

    public void AgregarEspecie(String EspecieActual){
        HistorialEspecie.add(EspecieActual);
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

    public void setEstadoCiudadania(String EstadoCiudadania){
        this.EstadoCiudadania = EstadoCiudadania;
    }

}