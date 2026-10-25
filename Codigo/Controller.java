package Codigo;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.List;

public class Controller {
    private ControlGasto controlGasto;

    public Controller(Repositorio repositorio){
        this.controlGasto = new ControlGasto(repositorio);
    }

    public void agregarLimite(Limite l) {
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
            List<String> alertas = controlGasto.registrar(consumo);  
            System.out.println("Consumo registrado correctamente");
            for (String alerta : alertas) {
                System.out.println(alerta);
            }
        }else{
            System.out.println("Datos incorrectos o incompletos");
        }
    }

}
