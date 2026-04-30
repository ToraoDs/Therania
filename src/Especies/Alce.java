package Especies;

public class Alce extends CiudadanoTherian {

    public Alce(String nombre, String apellido, String id,String fechaNacimiento, String estadoCiudadania){

        super(nombre,apellido,id,fechaNacimiento,estadoCiudadania,"Alce");

    }

    @Override

    public String instinto() {
        return "Los alces son solitarios y majestuosos, con un fuerte sentido territorial. Su instinto los lleva a la calma pero reaccionan con gran fuerza ante la amenaza.";
    }

    
}
