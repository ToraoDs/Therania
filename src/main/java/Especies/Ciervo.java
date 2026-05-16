package Especies;

import Manadas.Manada;

public class Ciervo extends CiudadanoTherian{

    private static final String ZONA = "x:500,y:500,r:100";

    public Ciervo(String nombre, String apellido, int id,String fechaNacimiento, String estadoCiudadania){

        super(nombre, apellido, id, fechaNacimiento, estadoCiudadania,
      "Ciervo", false,
      "Bramido melódico y suave",
      "x:510,y:490,r:80",             // Praderas del centro
      "Sensible, ágil, intuitivo"); 
    }

    @Override

    public String describirInstinto() {
        return "Los ciervos son sensibles e intuitivos ademas, tienen reflejos agudos ante el peligro. Su instinto los guía hacia la elegancia, la cautela y la fuerte conexión con la naturaleza.";    
    }

    public static final Manada MANADA_ROCIO    = new Manada("Manada Rocío",    "Ciervo", "Ciervos sensibles y contemplativos", 20,  0,   40.99, "El silencio nos habla, la naturaleza nos guía",    ZONA);
    public static final Manada MANADA_ARBOLEDA = new Manada("Manada Arboleda", "Ciervo", "Ciervos intuitivos y cautelosos",    20, 41,   70.99, "Cada paso es una decisión, cada decisión es vida", ZONA);
    public static final Manada MANADA_ALBA     = new Manada("Manada Alba",     "Ciervo", "Ciervos elegantes y sabios",         20, 71,  100,   "La elegancia es nuestra armadura",                 ZONA);
    
    public static Manada asignarManada(double iaa) {
        if (iaa <= 40) return MANADA_ROCIO;
        if (iaa <= 70) return MANADA_ARBOLEDA;
        return MANADA_ALBA;
    }
    
}
