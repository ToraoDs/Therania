package Especies;
public abstract class EspecieAutoPercibida {
    
    private String EspecieActual;

    public EspecieAutoPercibida(String EspecieActual){
        this.EspecieActual = EspecieActual;
    }

    public abstract String instinto();

    public String getEspecieActual(){
        return EspecieActual;
    }
    

}
