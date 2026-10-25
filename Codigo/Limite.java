package Codigo;
import java.time.LocalDate;

public abstract class Limite {
    protected double monto; 
    protected LocalDate fechaInicio;
    protected double umbral;
    
    // CONSTRUCTOR
    public Limite(double monto,LocalDate fechaInicio, double umbral) {
        if (monto <= 0)
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        if (fechaInicio == null)
            throw new IllegalArgumentException("La fecha de inicio es obligatoria");
        if (umbral <= 0)
            throw new IllegalArgumentException("El umbral debe ser mayor a 0");

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
    public abstract boolean mismoPeriodo(Limite otro);

    

}
