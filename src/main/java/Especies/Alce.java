package Especies;
import Manadas.Manada;

public class Alce extends CiudadanoTherian {

    private static final String ZONA = "x:350,y:285,r:100";

    public Alce(String nombre, String apellido, int id, String fechaNacimiento, String estadoCiudadania) {
        super(nombre, apellido, id, fechaNacimiento, estadoCiudadania,
            "Alce", false, "Bramido gutural y resonante", ZONA, "Majestuoso, solitario, territorial");
    }

    @Override
    public String describirInstinto() {
        return "Los alces son solitarios y majestuosos, con un fuerte sentido territorial. Su instinto los lleva a la calma pero reaccionan con gran fuerza ante la amenaza.";
    }

    public static final Manada MANADA_PRADERA = new Manada("Manada Pradera", "Alce", "Alces tranquilos y contemplativos", 20,  0,   40.99, "La calma es nuestra mayor fortaleza",     ZONA);
    public static final Manada MANADA_BOSQUE  = new Manada("Manada Bosque",  "Alce", "Alces guardianes del territorio",   20, 41,   70.99, "Protegemos lo que la naturaleza nos dio", ZONA);
    public static final Manada MANADA_CUMBRE  = new Manada("Manada Cumbre",  "Alce", "Alces líderes y majestuosos",       20, 71,  100,   "Desde las alturas guiamos al resto",      ZONA);

    public static Manada asignarManada(double iaa) {
        if (iaa <= 40) return MANADA_PRADERA;
        if (iaa <= 70) return MANADA_BOSQUE;
        return MANADA_CUMBRE;
    }
}