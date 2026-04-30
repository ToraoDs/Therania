package Especies;

public class Cebra extends CiudadanoTherian{

    public Cebra(String nombre, String apellido, String id,String fechaNacimiento, String estadoCiudadania){

        super(nombre,apellido,id,fechaNacimiento,estadoCiudadania,"Cebra");
            
    }

    @Override

    public String instinto() {
        return "Las cebras son animales muy alertas, su instinto las impulsa a mantenerse en grupo para protegerse. Son resilientes y se adaptan a distintos entornos.";
    
    }
}
