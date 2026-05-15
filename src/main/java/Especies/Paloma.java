package Especies;
import Manadas.Manada;

public class Paloma extends CiudadanoTherian {

    private static final String ZONA = "x:740,y:800,r:100";

    public Paloma(String nombre, String apellido, int id, String fechaNacimiento, String estadoCiudadania) {
        super(nombre, apellido, id, fechaNacimiento, estadoCiudadania,
            "Paloma", false, "Arrullo suave y constante", ZONA, "Pacífica, empática, leal");
    }

    @Override
    public String describirInstinto() {
        return "Las palomas son pacíficas, empáticas y orientadas al hogar. Su instinto las guía siempre de regreso a sus seres queridos, con una lealtad serena y constante.";
    }

    public static final Manada MANADA_COSTA      = new Manada("Manada Costa",      "Paloma", "Palomas mensajeras de la costa", 20,  0, 40,  "La paz viaja con nosotras",               ZONA);
    public static final Manada MANADA_MENSAJERAS = new Manada("Manada Mensajeras", "Paloma", "Palomas de vuelo largo",         15, 41, 70,  "Llevamos la palabra al mundo",            ZONA);
    public static final Manada MANADA_PALOMAR    = new Manada("Manada Palomar",    "Paloma", "Palomas élite del sur",          10, 71, 100, "En la calma está nuestra grandeza",       ZONA);

    public static Manada asignarManada(double iaa) {
        if (iaa <= 40) return MANADA_COSTA;
        if (iaa <= 70) return MANADA_MENSAJERAS;
        return MANADA_PALOMAR;
    }
}