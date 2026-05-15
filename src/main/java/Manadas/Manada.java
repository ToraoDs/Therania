package Manadas;

import Especies.CiudadanoTherian;
import Especies.Ritual;
import Especies.TherianException;

import java.util.ArrayList;
import java.util.List;

public class Manada {

    private String NombreManada;
    private String Especie;
    private String Descripcion;
    private int CupoMaximo;
    private double IAAMinimo;  // rango inferior
    private double IAAMaximo;  // rango superior
    private List<CiudadanoTherian> Miembros;
    private String Lema;
    private String Territorio;
    private List<Ritual> Rituales;

    public Manada(String NombreManada, String Especie ,String Descripcion, int CupoMaximo,
                  double IAAMinimo, double IAAMaximo, String Lema, String Territorio) {
        this.NombreManada = NombreManada;
        this. Especie = Especie;
        this.Descripcion = Descripcion;
        this.CupoMaximo = CupoMaximo;
        this.IAAMinimo = IAAMinimo;
        this.IAAMaximo = IAAMaximo;
        this.Miembros = new ArrayList<>();
        this.Lema = Lema;
        this.Rituales = new ArrayList<>();
        this.Territorio = Territorio;

    }

    // Intenta agregar un ciudadano según su IAA y cupo disponible
    public boolean agregarMiembro(CiudadanoTherian ciudadano, double iaa) {
        if (!aceptaIAA(iaa)) {
            throw new TherianException(TherianException.TipoError.IAA_FUERA_DE_RANGO, "IAA: " + iaa);
        }
        if (estaLlena()) {
            throw new TherianException(TherianException.TipoError.CUPO_LLENO, getNombreManada());

        }
        Miembros.add(ciudadano);
        return true;
    }

    // Verifica si el IAA está dentro del rango de esta manada
    public boolean aceptaIAA(double iaa) {
        return iaa >= IAAMinimo && iaa <= IAAMaximo;
    }

    public boolean estaLlena() {
        return Miembros.size() >= CupoMaximo;
    }

    public int getCupoDisponible() {
        return CupoMaximo - Miembros.size();
    }

    // Getters
    public String getNombreManada() { return NombreManada; }
    public String getEspecie() {return Especie;}
    public String getDescripcion() { return Descripcion; }
    public int getCupoMaximo() { return CupoMaximo; }
    public double getIAAMinimo() { return IAAMinimo; }
    public double getIAAMaximo() { return IAAMaximo; }
    public List<CiudadanoTherian> getMiembros() { return Miembros; }
    public List<Ritual> getRituales(){return Rituales;}
    public String getLema(){return Lema;}
    public String getTerritorio(){return Territorio;}

    public void agregarRitual(Ritual ritual){
        Rituales.add(ritual);
    }

}