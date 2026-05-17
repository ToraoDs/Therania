package Especies;

public class EspeciePersonalizada extends CiudadanoTherian {

    public EspeciePersonalizada(String nombre, String apellido, int id,
                                String fechaNacimiento, String estadoCiudadania,
                                String especie, boolean esPredador,
                                String sonido, String habitat, String caracteristicas) {
        super(nombre, apellido, id, fechaNacimiento, estadoCiudadania,
              especie, esPredador, sonido, habitat, caracteristicas);
    }

    @Override
    public String describirInstinto() {
        return "Especie personalizada: " + getEspecieActual()
             + ". Características: " + getCaracteristicas();
    }
}