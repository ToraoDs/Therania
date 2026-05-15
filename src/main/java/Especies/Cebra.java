package Especies;

import Manadas.Manada;

public class Cebra extends CiudadanoTherian{

    public Cebra(String nombre, String apellido, String id,String fechaNacimiento, String estadoCiudadania){

        super(nombre, apellido, id, fechaNacimiento, estadoCiudadania,
      "Cebra", false,
      "Ladrido y relincho corto",
      "x:295,y:470,r:75",             // Pradera centro-izquierda
      "Resiliente, alerta, gregaria");
    }

    @Override

    public String describirInstinto() {
        return "Las cebras son animales muy alertas, su instinto las impulsa a mantenerse en grupo para protegerse. Son resilientes y se adaptan a distintos entornos.";
    }

    public static final Manada MANADA_SABANA = new Manada("Manada Sabana", "Cebra","Cebras adaptables y resilientes", 20, 0, 40, "En la diferencia encontramos unidad", "Sabana del Sur");
    public static final Manada MANADA_VIENTO = new Manada("Manada Viento", "Cebra", "Cebras ágiles y alertas", 20, 41, 70, "Corremos juntas, sobrevivimos juntas", "Planicies del Viento Eterno");
    public static final Manada MANADA_AURORA = new Manada("Manada Aurora","Cebra", "Cebras protectoras y unidas", 20, 71, 100, "Nuestra fuerza está en el grupo", "Campos del Alba");

    public static Manada asignarManada(double iaa) {
        if (iaa <= 40) {return MANADA_SABANA;}
        if (iaa <= 70) {return MANADA_VIENTO;}
        return MANADA_AURORA;
    }
}
