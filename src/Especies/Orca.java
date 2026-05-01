package Especies;

import Manadas.Manada;

public class Orca extends CiudadanoTherian {
    
    public Orca(String nombre, String apellido, String id,String fechaNacimiento, String estadoCiudadania){

        super(nombre,apellido,id,fechaNacimiento,estadoCiudadania,"Orca", true);
            
    }    

    @Override

    public String instinto() {
        return "Las orcas son inteligentes, emotivas y altamente organizadas. Su instinto las lleva a comunicarse con precisión y a construir lazos de por vida.";
    }

    public static final Manada MANADA_PROFUNDA = new Manada("Manada Profunda",  "Orcas reservadas y observadoras", 20, 0,  40);
    public static final Manada MANADA_CORRIENTE= new Manada("Manada Corriente", "Orcas comunicativas y familiares", 20, 41, 70);
    public static final Manada MANADA_ABISMO   = new Manada("Manada Abismo",   "Orcas dominantes e inteligentes", 20, 71, 100);

    public static Manada asignarManada(double iaa) {
        if (iaa <= 40) return MANADA_PROFUNDA;
        if (iaa <= 70) return MANADA_CORRIENTE;
        return MANADA_ABISMO;
    }
}
