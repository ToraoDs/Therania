package Especies;

import Manadas.Manada;

public class Leon extends CiudadanoTherian {
    
    public Leon(String nombre, String apellido, String id,String fechaNacimiento, String estadoCiudadania){

        super(nombre,apellido,id,fechaNacimiento,estadoCiudadania,"Leon", true);
            
    }
    @Override

    public String instinto() {
        return "Los leones son lideres naturales. Protegen a su manada, son seguros de sí mismos y con una presencia que inspira respeto.";
    }
    
    public static final Manada MANADA_DESIERTO = new Manada("Manada Desierto", "Leones solitarios en formación", 20, 0,  40);
    public static final Manada MANADA_SAVANA   = new Manada("Manada Savana",   "Leones protectores y leales", 20, 41, 70);
    public static final Manada MANADA_REAL     = new Manada("Manada Real",     "Leones líderes de gran presencia", 20, 71, 100);

    public static Manada asignarManada(double iaa) {
        if (iaa <= 40) return MANADA_DESIERTO;
        if (iaa <= 70) return MANADA_SAVANA;
        return MANADA_REAL;
    }
}