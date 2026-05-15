package Especies;
import Manadas.Manada;

public class Orca extends CiudadanoTherian {

    private static final String ZONA = "x:110,y:890,r:160";

    public Orca(String nombre, String apellido, int id, String fechaNacimiento, String estadoCiudadania) {
        super(nombre, apellido, id, fechaNacimiento, estadoCiudadania,
            "Orca", true, "Clicks y silbidos complejos", ZONA, "Inteligente, familiar, organizada");
    }

    @Override
    public String describirInstinto() {
        return "Las orcas son inteligentes, emotivas y altamente organizadas. Su instinto las lleva a comunicarse con precisión y a construir lazos de por vida.";
    }

    public static final Manada MANADA_CORRIENTE   = new Manada("Manada Corriente",   "Orca", "Orcas jóvenes del océano",   20,  0, 40,  "Fluimos con la marea",                    ZONA);
    public static final Manada MANADA_PROFUNDIDAD = new Manada("Manada Profundidad", "Orca", "Orcas del mar profundo",     15, 41, 70,  "En las profundidades está la verdad",     ZONA);
    public static final Manada MANADA_ABISMO      = new Manada("Manada Abismo",      "Orca", "Orcas élite del océano",     10, 71, 100, "El abismo nos conoce",                    ZONA);

    public static Manada asignarManada(double iaa) {
        if (iaa <= 40) return MANADA_CORRIENTE;
        if (iaa <= 70) return MANADA_PROFUNDIDAD;
        return MANADA_ABISMO;
    }
}