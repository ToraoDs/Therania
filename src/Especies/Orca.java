package Especies;

public class Orca extends CiudadanoTherian {
    
    public Orca(String nombre, String apellido, String id,String fechaNacimiento, String estadoCiudadania){

        super(nombre,apellido,id,fechaNacimiento,estadoCiudadania,"Orca");
            
    }    

    @Override
    
    public String instinto() {
        return "Las orcas son inteligentes, emotivas y altamente organizadas. Su instinto las lleva a comunicarse con precisión y a construir lazos de por vida.";
    }
}
