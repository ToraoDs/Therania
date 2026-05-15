package Especies;
import Manadas.Manada;

public class Cebra extends CiudadanoTherian {

    private static final String ZONA = "x:365,y:775,r:100";

    public Cebra(String nombre, String apellido, int id, String fechaNacimiento, String estadoCiudadania) {
        super(nombre, apellido, id, fechaNacimiento, estadoCiudadania,
            "Cebra", false, "Ladrido y relincho corto", ZONA, "Resiliente, alerta, gregaria");
    }

    @Override
    public String describirInstinto() {
        return "Las cebras son animales muy alertas, su instinto las impulsa a mantenerse en grupo para protegerse. Son resilientes y se adaptan a distintos entornos.";
    }

    public static final Manada MANADA_LLANURA     = new Manada("Manada Llanura", "Cebra", "Cebras libres de la pradera",        20,  0, 40,  "En la manada está la fuerza",      ZONA);
    public static final Manada MANADA_RAYAS       = new Manada("Manada Rayas",   "Cebra", "Cebras guardianas del territorio",   15, 41, 70,  "Nuestras rayas nos hacen únicos",  ZONA);
    public static final Manada MANADA_SAVANA_CEBRA = new Manada("Manada Savana", "Cebra", "Cebras élite de la sabana",          10, 71, 100, "La sabana nos pertenece",          ZONA);

    public static Manada asignarManada(double iaa) {
        if (iaa <= 40) return MANADA_LLANURA;
        if (iaa <= 70) return MANADA_RAYAS;
        return MANADA_SAVANA_CEBRA;
    }
}