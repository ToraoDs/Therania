package Especies;

import Manadas.Manada;

public class Paloma extends CiudadanoTherian{

    public Paloma(String nombre, String apellido, String id,String fechaNacimiento, String estadoCiudadania){

        super(nombre, apellido, id, fechaNacimiento, estadoCiudadania,
      "Paloma", false,
      "Arrullo suave y constante",
      "x:760,y:790,r:70",             // Costa sur derecha
      "Pacífica, empática, leal");
            
    }

    @Override
    
    public String describirInstinto() {
        return "Las palomas son pacíficas, empáticas y orientadas al hogar. Su instinto las guía siempre de regreso a sus seres queridos, con una lealtad serena y constante.";
    }

    public static final Manada MANADA_NIDO = new Manada("Manada Nido","Paloma", "Palomas hogareñas y pacíficas", 20, 0, 40, "El hogar es donde el corazón descansa","Jardines del Valle Sereno");
    public static final Manada MANADA_VUELO = new Manada("Manada Vuelo","Paloma", "Palomas empáticas y mensajeras", 20, 41, 70, "Llevamos la paz allá donde vamos","Rutas del Viento Suave");
    public static final Manada MANADA_CIELO = new Manada("Manada Cielo","Paloma", "Palomas guías y sabias", 20, 71, 100, "El cielo no es el límite, es nuestro hogar","Cúpula del Cielo Infinito");

    public static Manada asignarManada(double iaa) {
        if (iaa <= 40) return MANADA_NIDO;
        if (iaa <= 70) return MANADA_VUELO;
        return MANADA_CIELO;
    }
    
}