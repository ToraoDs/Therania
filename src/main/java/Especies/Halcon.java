package Especies;

import Manadas.Manada;

public class Halcon extends CiudadanoTherian{
    
    public Halcon(String nombre, String apellido, String id,String fechaNacimiento, String estadoCiudadania){

        super(nombre, apellido, id, fechaNacimiento, estadoCiudadania,
      "Halcon", true,
      "Chillido agudo y penetrante",
      "x:855,y:215,r:70",             // Picos rocosos derecha
      "Preciso, independiente, observador");
            
    }

    @Override

    public String describirInstinto() {
        return "Los halcones son precisos, independientes y de visión aguda. Su instinto los impulsa a observar desde las alturas y actuar con determinación en el momento exacto.";
    }

public static final Manada MANADA_VIENTO = new Manada(
    "Manada Viento", "Halcon", "Halcones exploradores de altura",
    20, 0, 40, "El horizonte nos pertenece", "x:855,y:215,r:70");
public static final Manada MANADA_RAPACES = new Manada(
    "Manada Rapaces", "Halcon", "Halcones de vista aguda",
    15, 41, 70, "Vemos lo que otros no ven", "x:855,y:215,r:70");
public static final Manada MANADA_AGUILA = new Manada(
    "Manada Aguila", "Halcon", "Halcones supremos del cielo",
    10, 71, 100, "Desde las cimas todo es claro", "x:855,y:215,r:70");
    public static Manada asignarManada(double iaa) {

        if (iaa <= 40) return MANADA_VIENTO;
        if (iaa <= 70) return MANADA_RAPACES;
        return MANADA_AGUILA;
    }
}
