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

    public static final Manada MANADA_BRISA = new Manada("Manada Brisa","Halcon", "Halcones observadores y pacientes", 20, 0, 40,"Observar es saber, saber es poder","Acantilados del Viento Sur");
    public static final Manada MANADA_TORMENTA = new Manada("Manada Tormenta", "Halcon", "Halcones precisos e independientes", 20, 41, 70,"En la tormenta demostramos quiénes somos","Crestas de la Tormenta");
    public static final Manada MANADA_ZENITH = new Manada("Manada Zenith", "Halcon", "Halcones estrategas y dominantes", 20, 71, 100,"Desde el punto más alto todo es claro","Pico del Zenith Eterno");

    public static Manada asignarManada(double iaa) {
        if (iaa <= 40) return MANADA_BRISA;
        if (iaa <= 70) return MANADA_TORMENTA;
        return MANADA_ZENITH;
    }
}
