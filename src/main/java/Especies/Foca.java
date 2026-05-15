package Especies;
import Manadas.Manada;

public class Foca extends CiudadanoTherian {

    private static final String ZONA = "x:530,y:190,r:150";

    public Foca(String nombre, String apellido, int id, String fechaNacimiento, String estadoCiudadania) {
        super(nombre, apellido, id, fechaNacimiento, estadoCiudadania,
            "Foca", false, "Gruñidos y ladridos", ZONA, "Juguetona, curiosa, social");
    }

    @Override
    public String describirInstinto() {
        return "Las focas son juguetonas, curiosas y con una gran inteligencia. Su instinto las lleva a explorar, adaptarse y conectar con otros.";
    }

    public static final Manada MANADA_GLACIAR = new Manada("Manada Glaciar", "Foca", "Focas jóvenes del hielo",         20,  0, 40,  "El frío es nuestro hogar",          ZONA);
    public static final Manada MANADA_NIEVE   = new Manada("Manada Nieve",   "Foca", "Focas exploradoras de la tundra", 15, 41, 70,  "Bajo la nieve late la vida",        ZONA);
    public static final Manada MANADA_ARTICO  = new Manada("Manada Ártico",  "Foca", "Focas élite del ártico",          10, 71, 100, "Somos guardianes del hielo",        ZONA);

    public static Manada asignarManada(double iaa) {
        if (iaa <= 40) return MANADA_GLACIAR;
        if (iaa <= 70) return MANADA_NIEVE;
        return MANADA_ARTICO;
    }
}