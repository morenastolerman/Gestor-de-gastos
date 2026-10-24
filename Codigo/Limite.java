package Codigo;
import java.time.LocalDate;

public abstract class Limite {
    protected double monto; 
    protected LocalDate fechaInicio;
    protected double umbral;
    
    // CONSTRUCTOR
    public Limite(double monto,LocalDate fechaInicio, double umbral) {
        this.monto = monto;
        this.fechaInicio = fechaInicio;
        this.umbral = umbral;
        
    }

    // +saldo(gastado: double) : double
    public double saldo(double gastado){
        return umbral - gastado;
    }

    // +estado(gastado: double) : Semaforo
    public Semaforo estado(double gastado){
        if(gastado >= umbral*0.95){
            return Semaforo.ROJO;
        }else if(gastado >= umbral*0.80 && gastado < umbral*0.95){
            return Semaforo.AMARILLO;
        }
        return Semaforo.VERDE;
    }

    // Para ver si el consumo aplica a ese limite
    public abstract boolean aplica(Consumo c);

    

}
