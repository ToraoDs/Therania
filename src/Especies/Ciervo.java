package Especies;

import Manadas.Manada;

public class Ciervo extends CiudadanoTherian{

    public Ciervo(String nombre, String apellido, String id,String fechaNacimiento, String estadoCiudadania){

        super(nombre,apellido,id,fechaNacimiento,estadoCiudadania,"Ciervo", false);   
    }

    @Override

    public String instinto() {
        return "Los ciervos son sensibles e intuitivos ademas, tienen reflejos agudos ante el peligro. Su instinto los guía hacia la elegancia, la cautela y la fuerte conexión con la naturaleza.";    
    }

    public static final Manada MANADA_ROCIO = new Manada("Manada Rocío", "Ciervos sensibles y contemplativos", 20, 0, 40, "El silencio nos habla, la naturaleza nos guía","Valle del Rocío Eterno");
    public static final Manada MANADA_ARBOLEDA = new Manada("Manada Arboleda", "Ciervos intuitivos y cautelosos", 20, 41, 70,"Cada paso es una decisión, cada decisión es vida","Arboleda Sagrada del Centro");
    public static final Manada MANADA_ALBA = new Manada("Manada Alba", "Ciervos elegantes y sabios", 20, 71, 100,"La elegancia es nuestra armadura","Pradera del Alba Dorada");
    
    public static Manada asignarManada(double iaa) {
        if (iaa <= 40) return MANADA_ROCIO;
        if (iaa <= 70) return MANADA_ARBOLEDA;
        return MANADA_ALBA;
    }
    
}
