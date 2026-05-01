package Especies;

import Manadas.Manada;

public class Halcon extends CiudadanoTherian{
    
    public Halcon(String nombre, String apellido, String id,String fechaNacimiento, String estadoCiudadania){

        super(nombre,apellido,id,fechaNacimiento,estadoCiudadania,"Halcon", true);
            
    }

    @Override

    public String instinto() {
        return "Los halcones son precisos, independientes y de visión aguda. Su instinto los impulsa a observar desde las alturas y actuar con determinación en el momento exacto.";
    }

    public static final Manada MANADA_BRISA    = new Manada("Manada Brisa",    "Halcones observadores y pacientes", 20, 0,  40);
    public static final Manada MANADA_TORMENTA = new Manada("Manada Tormenta", "Halcones precisos e independientes", 20, 41, 70);
    public static final Manada MANADA_ZENITH   = new Manada("Manada Zenith",   "Halcones estrategas y dominantes", 20, 71, 100);

    public static Manada asignarManada(double iaa) {
        if (iaa <= 40) return MANADA_BRISA;
        if (iaa <= 70) return MANADA_TORMENTA;
        return MANADA_ZENITH;
    }
}
