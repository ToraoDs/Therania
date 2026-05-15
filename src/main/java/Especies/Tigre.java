package Especies;

import Manadas.Manada;

public class Tigre extends CiudadanoTherian{

    public Tigre(String nombre, String apellido, String id,String fechaNacimiento, String estadoCiudadania){

        super(nombre, apellido, id, fechaNacimiento, estadoCiudadania,
      "Tigre", true,
      "Gruñido grave y sibilante",
      "x:245,y:690,r:80",             // Jungla densa izquierda
      "Solitario, determinado, sigiloso");
            
    }
    @Override
    
    public String describirInstinto() {
        return "Los tigres son independientes y de gran determinación. Su instinto los hace actuar en solitario con una concentración absoluta hacia sus objetivos.";
    }

public static final Manada MANADA_JUNGLA = new Manada("Manada Jungla", "Tigre", "Tigres del territorio selvático",20, 0, 40, "En la sombra acecha el poder", "x:245,y:690,r:80");
public static final Manada MANADA_CAZADORES = new Manada("Manada Cazadores", "Tigre", "Tigres de alto rango",15, 41, 70, "La paciencia es nuestra arma", "x:245,y:690,r:80");
public static final Manada MANADA_SIBERIA = new Manada("Manada Siberia", "Tigre", "Tigres élite de la cima",10, 71, 100, "El frío nos fortalece", "x:245,y:690,r:80");

    public static Manada asignarManada(double iaa) {
        if (iaa <= 40) return MANADA_JUNGLA;
        if (iaa <= 70) return MANADA_CAZADORES;
        return MANADA_SIBERIA;
    }
    
}
