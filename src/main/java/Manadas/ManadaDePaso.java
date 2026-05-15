package Manadas;

import Especies.CiudadanoTherian;


public class ManadaDePaso extends Manada {

    private static final ManadaDePaso INSTANCIA = new ManadaDePaso();

    private ManadaDePaso() {
        super(
            "Manada de Paso",
            "Mixta",
            "Lugar temporal para ciudadanos en transición entre manadas",
            999,     // cupo muy alto, es zona de tránsito
            0, 100,  // acepta cualquier IAA
            "El camino es parte del destino",
            "Tierras Neutrales del Centro"
        );
    }

    // Singleton — solo existe una ManadaDePaso en todo Therania
    public static ManadaDePaso getInstance() {
        return INSTANCIA;
    }

    // Al agregar un ciudadano no valida IAA ni cupo estricto
    @Override
    public boolean agregarMiembro(CiudadanoTherian ciudadano, double iaa) {
        if (!getMiembros().contains(ciudadano)) {
            getMiembros().add(ciudadano);
        }
        return true;
    }

    public void removerMiembro(CiudadanoTherian ciudadano) {
        getMiembros().remove(ciudadano);
    }
}