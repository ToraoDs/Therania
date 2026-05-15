package Especies;

import Manadas.Manada;

public class Orca extends CiudadanoTherian {
    
    public Orca(String nombre, String apellido, String id,String fechaNacimiento, String estadoCiudadania){

        super(nombre, apellido, id, fechaNacimiento, estadoCiudadania,
      "Orca", true,
      "Clicks y silbidos complejos",
      "x:110,y:890,r:60",             // Océano sur
      "Inteligente, familiar, organizada");
            
    }    

    @Override

    public String describirInstinto() {
        return "Las orcas son inteligentes, emotivas y altamente organizadas. Su instinto las lleva a comunicarse con precisión y a construir lazos de por vida.";
    }

    public static final Manada MANADA_PROFUNDA = new Manada("Manada Profunda","Orca", "Orcas reservadas y observadoras", 20, 0, 40, "Las profundidades guardan nuestros secretos","Fosa del Océano Oscuro");
    public static final Manada MANADA_CORRIENTE = new Manada("Manada Corriente","Orca", "Orcas comunicativas y familiares", 20, 41, 70,"Nuestra voz viaja donde el ojo no llega","Corrientes del Mar Interior");
    public static final Manada MANADA_ABISMO = new Manada("Manada Abismo", "Orca", "Orcas dominantes e inteligentes", 20, 71, 100, "Dominamos el abismo porque lo conocemos","Abismo de las Aguas Eternas");

    public static Manada asignarManada(double iaa) {
        if (iaa <= 40) return MANADA_PROFUNDA;
        if (iaa <= 70) return MANADA_CORRIENTE;
        return MANADA_ABISMO;
    }
}
