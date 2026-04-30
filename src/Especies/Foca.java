package Especies;

public class Foca extends CiudadanoTherian{

    public Foca(String nombre, String apellido, String id,String fechaNacimiento, String estadoCiudadania){

        super(nombre,apellido,id,fechaNacimiento,estadoCiudadania,"Foca");    
    }

    @Override

    public String instinto() {
        return "Las focas son juguetonas, curiosa y con una gran inteligencia. Su instinto las lleva a explorar, adaptarse y conectar con otros.";
    }
    
}
