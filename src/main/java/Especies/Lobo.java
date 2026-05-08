package Especies;

import Manadas.Manada;

public class Lobo extends CiudadanoTherian{

    public Lobo(String nombre, String apellido, String id,String fechaNacimiento, String estadoCiudadania){

        super(nombre,apellido,id,fechaNacimiento,estadoCiudadania,"Lobo", true);  
    }
    @Override

    public String instinto() {
        return "Los lobos son leales, estratégicos y profundamente sociales. Su instinto los une a su manada con vínculos inquebrantables, guiándose por la confianza y la cooperación.";
    }

    public static final Manada MANADA_SOMBRA = new Manada("Manada Sombra", "Lobos solitarios y estrategas", 20, 0, 40, "En la oscuridad encontramos fuerza","Bosque del Norte Profundo");
    public static final Manada MANADA_LUNA = new Manada("Manada Luna", "Lobos guardianes del territorio", 20, 41, 70, "La luna nos une, el aullido nos guía","Valle de las Sombras Plateadas");
    public static final Manada MANADA_AURORA = new Manada("Manada Aurora", "Lobos líderes y exploradores", 20, 71, 100, "Lideramos donde otros no se atreven","Cima de los Vientos del Norte");

    public static Manada asignarManada(double iaa) {
        if (iaa <= 40){ return MANADA_SOMBRA;}
        if (iaa <= 70){ return MANADA_LUNA;}
        return MANADA_AURORA;
    }



}
