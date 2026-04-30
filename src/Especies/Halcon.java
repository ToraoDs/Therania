package Especies;

public class Halcon extends CiudadanoTherian{
    
    public Halcon(String nombre, String apellido, String id,String fechaNacimiento, String estadoCiudadania){

        super(nombre,apellido,id,fechaNacimiento,estadoCiudadania,"Halcon");
            
    }

    @Override

    public String instinto() {
        return "Los halcones son precisos, independientes y de visión aguda. Su instinto los impulsa a observar desde las alturas y actuar con determinación en el momento exacto.";
    }
}
