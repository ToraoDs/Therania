package Especies;

import Manadas.Manada;

public class Alce extends CiudadanoTherian {

    public Alce(String nombre, String apellido, String id,String fechaNacimiento, String estadoCiudadania){

        super(nombre,apellido,id,fechaNacimiento,estadoCiudadania,"Alce",false);

    }

    @Override

    public String instinto() {
        return "Los alces son solitarios y majestuosos, con un fuerte sentido territorial. Su instinto los lleva a la calma pero reaccionan con gran fuerza ante la amenaza.";
    }

    public static final Manada MANADA_PRADERA  = new Manada("Manada Pradera",  "Alces tranquilos y contemplativos", 20, 0,  40, "La calma es nuestra mayor fortaleza", "Llanuras del Amanecer");
    public static final Manada MANADA_BOSQUE   = new Manada("Manada Bosque",   "Alces guardianes del territorio", 20, 41, 70, "Protegemos lo que la naturaleza nos dio", "Bosque Antiguo del Este");
    public static final Manada MANADA_CUMBRE   = new Manada("Manada Cumbre",   "Alces líderes y majestuosos", 20, 71, 100, "Desde las alturas guiamos al resto", "Cima de la Gran Cordillera");

    public static Manada asignarManada(double iaa) {
        if (iaa <= 40) {return MANADA_PRADERA;}
        if (iaa <= 70) {return MANADA_BOSQUE;}
        return MANADA_CUMBRE;
}

    
}
