package Codigo;
import java.time.LocalDate;


public class Consumo {
    private int id;
    private double monto;
    private LocalDate fecha;
    private String descripcion;
    private int cuotas;
    private Categoria categoria;
    private MedioPago medioDePago;


    //+Consumo(id: int, monto: double, fecha: Date, descripcion: String, cuotas: int, categoria: Categoria, medioPago: MedioPago)
    public Consumo(double monto, LocalDate fecha, String descripcion, int cuotas, Categoria categoria,
            MedioPago medioDePago) {
        this.monto = monto;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.cuotas = cuotas;
        this.categoria = categoria;
        this.medioDePago = medioDePago;
    }


    //+montoCuota() : double
    private double montoCuota(){
        return monto/cuotas;

    }

}
