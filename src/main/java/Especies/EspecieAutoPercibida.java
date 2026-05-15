package Especies;
public abstract class EspecieAutoPercibida {
    
    private String EspecieActual;
    private boolean esPredador;
    private String SonidoPredominante;
    private String HabitatSimbolico;
    private String Caracteristicas;

    
    public abstract String describirInstinto();

    public EspecieAutoPercibida(String EspecieActual, boolean esPredador, String SonidoPredominante, String HabitatSimbolico,String Caracteristicas) {
        this.EspecieActual = EspecieActual;
        this.esPredador = esPredador;
        this.SonidoPredominante  = SonidoPredominante;
        this.HabitatSimbolico    = HabitatSimbolico;
        this.Caracteristicas     = Caracteristicas;
    }

    public boolean esPredador() { 
        return esPredador; 
    }

    // getters
    public String getEspecieActual(){
        return EspecieActual;
    }
    public String getSonidoPredominante(){ 
        return SonidoPredominante;
    }
    public String getHabitatSimbolico(){ 
        return HabitatSimbolico; 
    }
    public String getCaracteristicas(){ 
        return Caracteristicas; 
    }


    // setters
    public void setEspecieActual(String especie){ 
        this.EspecieActual = especie; 
    }
    public void setEsPredador(boolean esPredador){ 
        this.esPredador = esPredador; 
    }
    public void setSonidoPredominante(String sonido){ 
        this.SonidoPredominante = sonido; 
    }
    public void setHabitatSimbolico(String habitat){ 
        this.HabitatSimbolico = habitat; 
    }
    public void setCaracteristicas(String caracteristicas){ 
        this.Caracteristicas = caracteristicas; 
    }

}
