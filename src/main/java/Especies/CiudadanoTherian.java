package Especies;
import java.util.List;

import Manadas.Manada;
import Manadas.ManadaDePaso;

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
    private List<AfiliacionEfectiva> AfiliacionesEfectivas;


    public CiudadanoTherian(String Nombre, String Apellido, String Id, String FechaNacimiento, String EstadoCiudadania, String EspecieActual, boolean esPredador, String SonidoPredominante, String HabitatSimbolico, String Caracteristicas){
        
        super(EspecieActual, esPredador,SonidoPredominante, HabitatSimbolico, Caracteristicas);
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
        this.HistorialEspecie = new ArrayList<>();
        AgregarEspecie(EspecieActual);
        this.AfiliacionesEfectivas = new ArrayList<>();

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

    public void agregarAfiliacionEfectiva(AfiliacionEfectiva ae) {
        AfiliacionesEfectivas.add(ae);
    }


    public List<AfiliacionEfectiva> getAfiliacionesEfectivas() {
        return AfiliacionesEfectivas;
    }

    public void cambiarEspecie(String nuevaEspecie, boolean esPredador,
                            String sonido, String habitat, String caracteristicas,
                            List<Manada> todasLasManadas) {
    // Guardar especie anterior
    AgregarEspecie(getEspecieActual());

    // Actualizar atributos
    setEspecieActual(nuevaEspecie);
    setEsPredador(esPredador);
    setSonidoPredominante(sonido);
    setHabitatSimbolico(habitat);
    setCaracteristicas(caracteristicas);

    // Salir de manada actual → ManadaDePaso
    for (Manada m : todasLasManadas) {
        if (m.getMiembros().contains(this)) {
            m.getMiembros().remove(this);
            for (AfiliacionManada a : getManadas()) {
                if (a.estaActivo()) {
                    a.setFechaSalida(java.time.LocalDate.now().toString());
                    }
                }
            break;
            }
        }
    ManadaDePaso.getInstance().agregarMiembro(this, getIAA());

    // Buscar manada para nueva especie
    Manada manadaNueva = buscarManadaPorEspecieEIAA(nuevaEspecie, getIAA(), todasLasManadas);
    if (manadaNueva != null && !manadaNueva.estaLlena()) {
        ManadaDePaso.getInstance().removerMiembro(this);
        AfiliacionManada af = new AfiliacionManada(
            manadaNueva.getNombreManada(),
            java.time.LocalDate.now().toString(),
            getRol(), getPuntuacionManada(), null
        );
        try {
            AgregarManada(af);
            manadaNueva.agregarMiembro(this, getIAA());
            } catch (TherianException e) { }
        }
    }

    private Manada buscarManadaPorEspecieEIAA(String especie, double iaa,
                                            List<Manada> todasLasManadas) {
        for (Manada m : todasLasManadas) {
            if (m.getEspecie().equalsIgnoreCase(especie) 
                && m.aceptaIAA(iaa) 
                && !m.estaLlena()) {
                return m;
            }
        }
        return null;
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

    public List<String> getHistorialEspecie() {
    return HistorialEspecie;
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