package Especies;

import Manadas.Manada;

public class Foca extends CiudadanoTherian{

    public Foca(String nombre, String apellido, String id,String fechaNacimiento, String estadoCiudadania){

        super(nombre,apellido,id,fechaNacimiento,estadoCiudadania,"Foca", false);    
    }

    @Override

    public String instinto() {
        return "Las focas son juguetonas, curiosa y con una gran inteligencia. Su instinto las lleva a explorar, adaptarse y conectar con otros.";
    }

    public static final Manada MANADA_MAREA    = new Manada("Manada Marea",    "Focas curiosas y exploradoras", 20, 0,  40);
    public static final Manada MANADA_CORAL    = new Manada("Manada Coral",    "Focas sociales y juguetones", 20, 41, 70);
    public static final Manada MANADA_GLACIAR  = new Manada("Manada Glaciar",  "Focas líderes y resilientes", 20, 71, 100);

    public static Manada asignarManada(double iaa) {
        if (iaa <= 40) return MANADA_MAREA;
        if (iaa <= 70) return MANADA_CORAL;
        return MANADA_GLACIAR;
    }
    
}
