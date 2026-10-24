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
        this(0, monto, fecha, descripcion, cuotas, categoria, medioDePago);
    }
    
    public Consumo(int id, double monto, LocalDate fecha, String descripcion, int cuotas, Categoria categoria,
        MedioPago medioDePago) {
    this.id = id;
    this.monto = monto;
    this.fecha = fecha;
    this.descripcion = descripcion;
    this.cuotas = cuotas;
    this.categoria = categoria;
    this.medioDePago = medioDePago;
}


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public double getMonto() {
        return monto;
    }


    public LocalDate getFecha() {
        return fecha;
    }


    public String getDescripcion() {
        return descripcion;
    }

    public MedioPago getMedioDePago() {
        return medioDePago;
    }

    public int getCuotas() {
        return cuotas;
    }


    public Categoria getCategoria() {
        return categoria;
    }


    //+montoCuota() : double
    private double montoCuota(){
        return monto/cuotas;

    }

}
