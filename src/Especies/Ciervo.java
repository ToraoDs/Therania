package Especies;

public class Ciervo extends CiudadanoTherian{

    public Ciervo(String nombre, String apellido, String id,String fechaNacimiento, String estadoCiudadania){

        super(nombre,apellido,id,fechaNacimiento,estadoCiudadania,"Ciervo");   
    }

    @Override

    public String instinto() {
        return "Los ciervos son sensibles e intuitivos ademas, tienen reflejos agudos ante el peligro. Su instinto los guía hacia la elegancia, la cautela y la fuerte conexión con la naturaleza.";    
    }
    
}
