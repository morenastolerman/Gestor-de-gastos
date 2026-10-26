package Codigo;
 
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
 
public class Controller {
    private ControlGasto controlGasto;
 
    public Controller(Repositorio repositorio) {
        this.controlGasto = new ControlGasto(repositorio);
    }
 
    public void agregarLimite(Limite l) {
        controlGasto.agregarLimite(l);
    }
 
    // ===================== CU1 =====================
 
    private boolean validarDatos(double monto, LocalDate fecha, int cuotas, Categoria categoria,
            MedioPago medioDePago) {
        return monto > 0
            && fecha != null && !fecha.isAfter(LocalDate.now())
            && cuotas >= 1
            && categoria != null
            && medioDePago != null;
    }
 
    // Devuelve los mensajes para que la vista los muestre (el Controller no imprime)
    public List<String> ingresarDatosConsumo(double monto, LocalDate fecha, String descripcion, int cuotas,
            Categoria categoria, MedioPago medioDePago) {
        List<String> mensajes = new ArrayList<>();
        if (validarDatos(monto, fecha, cuotas, categoria, medioDePago)) {
            Consumo consumo = new Consumo(monto, fecha, descripcion, cuotas, categoria, medioDePago);
            mensajes.add("Consumo registrado correctamente");
            mensajes.addAll(controlGasto.registrar(consumo));
        } else {
            mensajes.add("Datos incorrectos o incompletos");
        }
        return mensajes;
    }
 
    // ===================== CU7: Gestionar categorías =====================
 
    public List<String> agregarCategoria(String nombre) {
        List<String> mensajes = new ArrayList<>();
        if (nombre == null || nombre.isBlank()) {
            mensajes.add("Datos incorrectos o incompletos");
        } else if (controlGasto.agregarCategoria(nombre.trim())) {
            mensajes.add("Categoría agregada correctamente");
        } else {
            mensajes.add("La categoría ya existe");
        }
        return mensajes;
    }
 
    public List<String> modificarCategoria(int id, String nuevoNombre) {
        List<String> mensajes = new ArrayList<>();
        if (nuevoNombre == null || nuevoNombre.isBlank()) {
            mensajes.add("Datos incorrectos o incompletos");
            return mensajes;
        }
        try {
            controlGasto.modificarCategoria(id, nuevoNombre.trim());
            mensajes.add("Categoría modificada correctamente");
        } catch (IllegalArgumentException | IllegalStateException e) {
            mensajes.add(e.getMessage());
        }
        return mensajes;
    }
 
    public List<String> eliminarCategoria(int id) {
        List<String> mensajes = new ArrayList<>();
        try {
            controlGasto.eliminarCategoria(id);
            mensajes.add("Categoría eliminada correctamente");
        } catch (IllegalArgumentException | IllegalStateException e) {
            mensajes.add(e.getMessage());
        }
        return mensajes;
    }
 
    public List<Categoria> obtenerCategorias() {
        return controlGasto.getCategorias();
    }
    
    // ===================== CU8 =====================
 
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
 
    public List<String> modificarMedioPago(int id, String nuevoNombre) {
        List<String> mensajes = new ArrayList<>();
        if (nuevoNombre == null || nuevoNombre.isBlank()) {
            mensajes.add("Datos incorrectos o incompletos");
            return mensajes;
        }
        try {
            controlGasto.modificarMedioPago(id, nuevoNombre.trim());
            mensajes.add("Medio de pago modificado correctamente");
        } catch (IllegalArgumentException | IllegalStateException e) {
            mensajes.add(e.getMessage());
        }
        return mensajes;
    }
 
    public List<String> eliminarMedioPago(int id) {
        List<String> mensajes = new ArrayList<>();
        try {
            controlGasto.eliminarMedioPago(id);
            mensajes.add("Medio de pago eliminado correctamente");
        } catch (IllegalArgumentException | IllegalStateException e) {
            mensajes.add(e.getMessage());
        }
        return mensajes;
    }
 
    public List<MedioPago> obtenerMediosPago() {
        return controlGasto.getMediosPago();
    }
}
 