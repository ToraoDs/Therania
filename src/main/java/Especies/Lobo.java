package Especies;

import Manadas.Manada;

public class Lobo extends CiudadanoTherian{

    private static final String ZONA = "x:520,y:870,r:100";

    public Lobo(String nombre, String apellido, int id,String fechaNacimiento, String estadoCiudadania){

       super(nombre, apellido, id, fechaNacimiento, estadoCiudadania,"Lobo", true,"Aullido profundo y prolongado", ZONA, "Territorial, social, estratégico"); 
    }
    @Override

    public String describirInstinto() {
        return "Los lobos son leales, estratégicos y profundamente sociales. Su instinto los une a su manada con vínculos inquebrantables, guiándose por la confianza y la cooperación.";
    }

    public static final Manada MANADA_SOMBRA = new Manada("Manada Sombra","Lobo", "Lobos solitarios y estrategas", 20, 0, 40, "En la oscuridad encontramos fuerza",ZONA);
    public static final Manada MANADA_LUNA = new Manada("Manada Luna","Lobo", "Lobos guardianes del territorio", 20, 41, 70, "La luna nos une, el aullido nos guía",ZONA);
    public static final Manada MANADA_AURORA = new Manada("Manada Aurora","Lobo", "Lobos líderes y exploradores", 20, 71, 100, "Lideramos donde otros no se atreven",ZONA);

    public static Manada asignarManada(double iaa) {
        if (iaa <= 40){ return MANADA_SOMBRA;}
        if (iaa <= 70){ return MANADA_LUNA;}
        return MANADA_AURORA;
    }



}
