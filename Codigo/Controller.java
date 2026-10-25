package Codigo;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.ArrayList;

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

    // --------------------------------------------------------
    public List<String> agregarMedioPago(String nombre) {
        List<String> mensajes = new ArrayList<>();
        if (nombre == null || nombre.isBlank()) {
            mensajes.add("Datos incorrectos o incompletos");
        } else if (controlGasto.agregarMedioPago(nombre.trim())) {
            mensajes.add("Medio de pago agregado correctamente");
        } else {
            mensajes.add("El medio de pago ya existe");
        }
        return mensajes;
    }

    public List<MedioPago> obtenerMediosPago() {
        return controlGasto.getMediosPago();
    }

}
