package Especies;

public class Tigre extends CiudadanoTherian{

    public Tigre(String nombre, String apellido, String id,String fechaNacimiento, String estadoCiudadania){

        super(nombre,apellido,id,fechaNacimiento,estadoCiudadania,"Tigre");
            
    }
    @Override
    
    public String instinto() {
        return "Los tigres son independientes y de gran determinación. Su instinto los hace actuar en solitario con una concentración absoluta hacia sus objetivos.";
    }
    
}
