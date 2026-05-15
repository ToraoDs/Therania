package Especies;
import Manadas.Manada;

public class Leon extends CiudadanoTherian {

    private static final String ZONA = "x:800,y:600,r:150";

    public Leon(String nombre, String apellido, int id, String fechaNacimiento, String estadoCiudadania) {
        super(nombre, apellido, id, fechaNacimiento, estadoCiudadania,
            "Leon", true, "Rugido grave y dominante", ZONA, "Dominante, protector, noble");
    }

    @Override
    public String describirInstinto() {
        return "Los leones son lideres naturales. Protegen a su manada, son seguros de sí mismos y con una presencia que inspira respeto.";
    }

    public static final Manada MANADA_DESIERTO = new Manada("Manada Desierto", "Leon", "Leones solitarios en formación",       20,  0, 40,  "El desierto nos prueba, nosotros lo superamos", ZONA);
    public static final Manada MANADA_SAVANA   = new Manada("Manada Savana",   "Leon", "Leones protectores y leales",          20, 41, 70,  "Protegemos con garras y con corazón",           ZONA);
    public static final Manada MANADA_REAL     = new Manada("Manada Real",     "Leon", "Leones líderes de gran presencia",     20, 71, 100, "El reino nos pertenece, lo merecimos",          ZONA);

    public static Manada asignarManada(double iaa) {
        if (iaa <= 40) return MANADA_DESIERTO;
        if (iaa <= 70) return MANADA_SAVANA;
        return MANADA_REAL;
    }
}