package Especies;

public class Leon extends CiudadanoTherian {
    
    public Leon(String nombre, String apellido, String id,String fechaNacimiento, String estadoCiudadania){

        super(nombre,apellido,id,fechaNacimiento,estadoCiudadania,"Leon");
            
    }
    @Override

    public String instinto() {
        return "Los leones son lideres naturales. Protegen a su manada, son seguros de sí mismos y con una presencia que inspira respeto.";
    }
}
