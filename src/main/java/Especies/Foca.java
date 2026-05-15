package Especies;

import Manadas.Manada;

public class Foca extends CiudadanoTherian{

    public Foca(String nombre, String apellido, String id,String fechaNacimiento, String estadoCiudadania){

        super(nombre,apellido,id,fechaNacimiento,estadoCiudadania,"Foca", false, "Gruñidos y ladridos", "x:530,y:165,r:70", "Juguetona, curiosa, social" );    
    }

    @Override

    public String describirInstinto() {
        return "Las focas son juguetonas, curiosa y con una gran inteligencia. Su instinto las lleva a explorar, adaptarse y conectar con otros.";
    }

    public static final Manada MANADA_MAREA = new Manada("Manada Marea", "Foca", "Focas curiosas y exploradoras", 20, 0, 40,"El mar nos llama, lo desconocido nos atrae","Costa de las Mareas Bajas");
    public static final Manada MANADA_CORAL = new Manada("Manada Coral", "Foca","Focas sociales y juguetoras", 20, 41, 70,"La alegría es nuestra forma de resistir","Arrecife del Coral Vivo");
    public static final Manada MANADA_GLACIAR = new Manada("Manada Glaciar", "Foca","Focas líderes y resilientes", 20, 71, 100,"El frío nos forjó, la perseverancia nos define","Glaciar del Fin del Mundo");

    public static Manada asignarManada(double iaa) {
        if (iaa <= 40) return MANADA_MAREA;
        if (iaa <= 70) return MANADA_CORAL;
        return MANADA_GLACIAR;
    }
    
}
