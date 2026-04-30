package Especies;

public class Paloma extends CiudadanoTherian{

    public Paloma(String nombre, String apellido, String id,String fechaNacimiento, String estadoCiudadania){

        super(nombre,apellido,id,fechaNacimiento,estadoCiudadania,"Paloma");
            
    }

    @Override
    
    public String instinto() {
        return "Las palomas son pacíficas, empáticas y orientadas al hogar. Su instinto las guía siempre de regreso a sus seres queridos, con una lealtad serena y constante.";
    }
    
}