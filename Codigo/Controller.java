package Codigo;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;

public class Controller {
    private ControlGasto controlGasto;

    public Controller(Repositorio repositorio){
        this.controlGasto = new ControlGasto(repositorio, this);
    }

    //ingresarDatosConsumo()
    // ValidarDatos(monto, fecha)
    public void agregarLimite(Limite l){
        controlGasto.agregarLimite(l);
    }

    private boolean validarDatos(Double monto, LocalDate fecha, int cuotas, Categoria categoria){
        return monto > 0
            && fecha != null && !fecha.isAfter(LocalDate.now())
            && cuotas >= 1
            && categoria != null;
    }

    public void ingresarDatosConsumo(double monto, LocalDate fecha, String descripcion, int cuotas, Categoria categoria,
            MedioPago medioDePago){
        if(validarDatos(monto, fecha, cuotas, categoria)){
            Consumo consumo = new  Consumo(monto, fecha, descripcion, cuotas, categoria, medioDePago);
            controlGasto.registrar(consumo);
            System.out.println("Consumo registrado correctamente");
        }else{
            System.out.println("Datos incorrectos o incompletos");
        }
    }

    public void actualizar(Semaforo estado, String mensaje){
        if (estado == Semaforo.ROJO) {
            System.out.println("ALERTA: " + mensaje);
        } else if (estado == Semaforo.AMARILLO) {
            System.out.println("ADVERTENCIA: " + mensaje);
        }
    }
}
