package Codigo;
import java.time.LocalDate;

public class Limite {
    private int monto; 
    private Periodo periodo;
    private LocalDate fechaInicio;
    private double umbral;
    private Categoria categoria;

    // +saldo(gastado: double) : double
    public double saldo(double gastado){
        return 0;
    }

    // +estado(gastado: double) : Semaforo
    public Semaforo estado(double gastado){
        return Semaforo.ROJO;
    }

}
