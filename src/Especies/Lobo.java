package Especies;

public class Lobo extends CiudadanoTherian{
    
    public Lobo(String nombre, String apellido, String id,String fechaNacimiento, String estadoCiudadania){

        super(nombre,apellido,id,fechaNacimiento,estadoCiudadania,"Lobo");  
    }
    @Override

    public String instinto() {
        return "Los lobos son leales, estratégicos y profundamente sociales. Su instinto los une a su manada con vínculos inquebrantables, guiándose por la confianza y la cooperación.";
    }
}
