package Especies;

import Manadas.Manada;

public class Tigre extends CiudadanoTherian{

    public Tigre(String nombre, String apellido, String id,String fechaNacimiento, String estadoCiudadania){

        super(nombre,apellido,id,fechaNacimiento,estadoCiudadania,"Tigre", true);
            
    }
    @Override
    
    public String instinto() {
        return "Los tigres son independientes y de gran determinación. Su instinto los hace actuar en solitario con una concentración absoluta hacia sus objetivos.";
    }

public static final Manada MANADA_JUNGLA = new Manada("Manada Jungla", "Tigres instintivos y reservados", 20, 0, 40, "La jungla nos habla, nosotros escuchamos","Jungla Densa del Oeste");
public static final Manada MANADA_SOMBRA = new Manada("Manada Sombra", "Tigres intensos y determinados", 20, 41, 70, "En las sombras somos más letales","Selva de las Sombras Profundas");
public static final Manada MANADA_TORMENTA = new Manada("Manada Tormenta", "Tigres dominantes e independientes", 20, 71, 100, "Somos la tormenta que nadie ve venir","Montañas de la Gran Tormenta");

    public static Manada asignarManada(double iaa) {
        if (iaa <= 40) return MANADA_JUNGLA;
        if (iaa <= 70) return MANADA_SOMBRA;
        return MANADA_TORMENTA;
    }
    
}
