package Especies;

public class TherianException extends RuntimeException {

    public enum TipoError {
        MANADA_ACTIVA,
        CUPO_LLENO,
        IAA_FUERA_DE_RANGO,
        CIUDADANO_NO_ENCONTRADO,
        MANADA_NO_ENCONTRADA,
        RITUAL_DUPLICADO
    }

    private TipoError tipoError;

    private static String construirMensaje(TipoError tipo, String detalle) {
        String base;
        switch (tipo) {
            case MANADA_ACTIVA:            base = "El ciudadano ya pertenece a una manada activa"; break;
            case CUPO_LLENO:              base = "La manada ha alcanzado su cupo maximo"; break;
            case IAA_FUERA_DE_RANGO:      base = "El IAA no corresponde al rango de esta manada"; break;
            case CIUDADANO_NO_ENCONTRADO: base = "No se encontro el ciudadano"; break;
            case MANADA_NO_ENCONTRADA:    base = "No se encontro la manada"; break;
            case RITUAL_DUPLICADO:        base = "El ritual ya fue registrado"; break;
            default:                      base = "Error desconocido"; break;
        }
        return base + " - " + detalle;
    }

    public TherianException(TipoError tipoError, String detalle) { 
        super(construirMensaje(tipoError, detalle)); 
        this.tipoError = tipoError;
    }

    public TipoError getTipoError() {
        return tipoError;
    }
}