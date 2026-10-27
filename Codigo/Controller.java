package Codigo;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private ControlGasto controlGasto;

    public Controller(Repositorio repositorio) {
        this.controlGasto = new ControlGasto(repositorio);
    }

    // ===================== CU1: Registrar consumo y notificar =====================

    public void agregarLimite(Limite l) {
        controlGasto.agregarLimite(l);
    }

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

    // ===================== CU8: Gestionar medios de pago =====================

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

   // ---- CU9: reporte estadístico ----
    // Devuelve el reporte listo para mostrar, o el mensaje de error si el rango es inválido
    public String generarReporteEstadistico(LocalDate desde, LocalDate hasta) {
        try {
            return controlGasto.generarReporte(new ReporteEstadistico(), desde, hasta);
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    } 
   // ===================== CU10: Consultar gastos del período =====================

    // desde/hasta: ambos o ninguno. categoria: null = todas.
    public List<String> consultarConsumos(LocalDate desde, LocalDate hasta, Categoria categoria) {
        List<String> mensajes = new ArrayList<>();
        if ((desde == null) != (hasta == null)) {
            mensajes.add("Datos incorrectos o incompletos");
            return mensajes;
        }

        List<Criterio> criterios = new ArrayList<>();
        try {
            if (desde != null) criterios.add(new CriterioRangoFechas(desde, hasta));
        } catch (IllegalArgumentException e) {
            mensajes.add(e.getMessage());
            return mensajes;
        }
        if (categoria != null) criterios.add(new CriterioCategoria(categoria));

        List<Consumo> consumos = controlGasto.consultarConsumos(criterios);
        if (consumos.isEmpty()) {
            mensajes.add("No se encontraron consumos");
            return mensajes;
        }

        double total = 0;
        for (Consumo c : consumos) {
            total += c.getMonto();
            String cuotas = c.getCuotas() > 1 ? " (" + c.getCuotas() + " cuotas)" : "";
            mensajes.add(String.format("%s | %s | %s | %s | %.2f%s",
                    c.getFecha(), c.getDescripcion(), c.getCategoria().getNombre(),
                    c.getMedioDePago().getNombre(), c.getMonto(), cuotas));
        }
        mensajes.add(String.format("Total: %.2f (%d consumo(s))", total, consumos.size()));
        return mensajes;
    }

    // Devuelve el reporte listo para mostrar, o el mensaje de error si el rango es inválido
    public String generarReporteGastos(LocalDate desde, LocalDate hasta) {
        try {
            return controlGasto.generarReporte(new ReporteGastosPeriodo(), desde, hasta);
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    // Atajo para "en el mes de agosto": el período es el mes completo
    public String generarReporteGastosMensual(YearMonth mes) {
        if (mes == null) return "Datos incorrectos o incompletos";
        return generarReporteGastos(mes.atDay(1), mes.atEndOfMonth());
    }
}