package Especies;
public abstract class EspecieAutoPercibida {
    
    private String EspecieActual;
    private boolean esPredador;

    public EspecieAutoPercibida(String EspecieActual){
        this.EspecieActual = EspecieActual;
    }

    public abstract String instinto();

    public String getEspecieActual(){
        return EspecieActual;
    }

    public EspecieAutoPercibida(String EspecieActual, boolean esPredador) {
        this.EspecieActual = EspecieActual;
        this.esPredador = esPredador;
    }

    public boolean esPredador() { 
        return esPredador; 
    }
        
    

}
