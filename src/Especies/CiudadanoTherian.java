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
    private List<Ritual> Rituales;
    private double ratioCaza;    
    private double ratioEscape; 
    private String Rol;    
    private double IAA;  
    private int PuntuacionManada;   
    private String InicioCargoAlfa;
    private int DuracionCargoMeses;


    public CiudadanoTherian(String Nombre, String Apellido, String Id, String FechaNacimiento, String EstadoCiudadania, String EspecieActual, boolean esPredador){
        
        super(EspecieActual, esPredador);
        if (esPredador){
            this.ratioCaza= 25.0;
            this.ratioEscape= 0.0;
        } else {
            this.ratioCaza = 0.0;
            this.ratioEscape = 25.0;
        }
        this.Rol = "Observador";
        this.Nombre = Nombre;
        this.Apellido = Apellido;
        this.Id = Id;
        this.FechaNacimiento = FechaNacimiento;
        this.EstadoCiudadania = EstadoCiudadania;
        this.HistorialEspecie = new ArrayList<>();
        this.Manadas = new ArrayList<>();
        this.Rituales = new ArrayList<>();
        this.IAA = 0;
        this.PuntuacionManada = 0;
        this.InicioCargoAlfa = null;
        this.DuracionCargoMeses = 0;

    }

    public boolean AgregarManada(AfiliacionManada nuevaManada) {
        for (AfiliacionManada m : Manadas) {
            if (m.estaActivo()) {
                throw new TherianException(TherianException.TipoError.MANADA_ACTIVA, getNombre());
            }
        }
        Manadas.add(nuevaManada);
        return true;
    }

    public void AgregarEspecie(String EspecieActual){
        HistorialEspecie.add(EspecieActual);
    }

    public void agregarRitual(Ritual ritual) {
        Rituales.add(ritual);
    }

    public List<AfiliacionManada> getManadas(){
        return Manadas;
    }


    // getters

    public String getNombre(){
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
    public List<Ritual> getRituales() {
        return Rituales;
    }

    public double getRatioCaza(){
        return ratioCaza;
    }

    public double getRatioEscape(){
        return ratioEscape;
    }

    public String getRol(){
        return Rol;
    }

    public double getIAA(){
        return IAA;
    }

    public int getPuntuacionManada(){
        return PuntuacionManada;
    }
    public String getInicioCargoAlfa() { 
        return InicioCargoAlfa; 
    }

    public int getDuracionCargoMeses() { 
        return DuracionCargoMeses; 
    }

    // setters

    public void setEstadoCiudadania(String EstadoCiudadania){
        this.EstadoCiudadania = EstadoCiudadania;
    }

    public void setRol(String Rol){
        this.Rol = Rol;
    }

    public void setRatioCaza(Double ratioCaza ){
        this.ratioCaza = ratioCaza;
    }

    public void setRatioEscape(Double ratioEscape){
        this.ratioEscape = ratioEscape;
    }

    public void setIAA(double IAA){
        this.IAA = IAA;
    }

    public void setPuntuacionManada(int PuntuacionManada){
        this.PuntuacionManada = PuntuacionManada;
    }
    
    public void setInicioCargoAlfa(String fecha) { 
        this.InicioCargoAlfa = fecha; 
    }
    
    public void setDuracionCargoMeses(int meses) { 
        this.DuracionCargoMeses = meses; 
    }

    
    // Contador de rituales

    public int contarRitualesAsistidos(){
        int contador = 0;
        for (Ritual r : Rituales) {
        if (r.isAsistio()) contador++;
        }
    return contador;
    }

    
}