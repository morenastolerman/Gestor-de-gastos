package Codigo;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ControlGasto {
    private Repositorio repositorio;
    private List<Limite> limites = new ArrayList<>();

    public ControlGasto(Repositorio repositorio) {
        this.repositorio = repositorio;
    }

    // ===================== CU5: Definir y modificar límite =====================

    // IllegalStateException si ya hay un límite para ese período
    public void agregarLimite(Limite l) {
        if (l == null)
            throw new IllegalArgumentException("El límite no puede ser null");
        for (Limite existente : limites) {
            if (existente.mismoPeriodo(l))
                throw new IllegalStateException("Ya existe un límite para ese período");
        }
        repositorio.guardarLimite(l);   // CU11: se guarda para recuperarlo al iniciar
        limites.add(l);
    }

    // reemplaza el límite del mismo período por el nuevo
    // IllegalStateException si no hay un límite para ese período
    public void modificarLimite(Limite nuevo) {
        if (nuevo == null)
            throw new IllegalArgumentException("El límite no puede ser null");
        for (int i = 0; i < limites.size(); i++) {
            if (limites.get(i).mismoPeriodo(nuevo)) {
                repositorio.actualizarLimite(nuevo);   // CU11: la modificación también queda guardada
                limites.set(i, nuevo);
                return;
            }
        }
        throw new IllegalStateException("No hay un límite definido para ese período");
    }

    // lista de solo lectura: desde afuera no se pueden agregar ni borrar límites
    public List<Limite> getLimites() {
        return Collections.unmodifiableList(limites);
    }

    // ===================== CU11: Cargar configuración inicial =====================

    // recupera los límites guardados (categorías, medios y consumos se leen del repositorio cuando se piden)
    // RuntimeException si no se puede leer el archivo
    public void cargarConfiguracion() {
        limites.clear();
        limites.addAll(repositorio.listarLimites());
    }

    // ===================== CU6: Consultar saldo y estado =====================

    // Devuelve el límite, lo gastado, el porcentaje, el saldo y el estado (semáforo) del período de la fecha.
    // Si el estado es AMARILLO o ROJO agrega la alerta (CU12).
    // IllegalArgumentException si no hay un límite para ese período
    public List<String> consultarSaldo(LocalDate fecha) {
        Limite l = buscarLimite(fecha);
        double gastado = calcularGastado(l);
        double saldo = l.saldo(gastado);
        Semaforo estado = l.estado(gastado);

        List<String> resultado = new ArrayList<>();
        resultado.add(String.format("Límite: $%.2f", l.getMonto()));
        resultado.add(String.format("Gastado: $%.2f (%.1f %% del límite)", gastado, l.porcentajeConsumido(gastado)));
        if (saldo >= 0) {
            resultado.add(String.format("Saldo disponible: $%.2f", saldo));
        } else {
            resultado.add(String.format("Saldo disponible: $0.00 (te pasaste $%.2f)", -saldo));
        }
        resultado.add("Estado: " + estado);
        String alerta = alertaDe(l, gastado);
        if (alerta != null) resultado.add(alerta);
        return resultado;
    }

    private Limite buscarLimite(LocalDate fecha) {
        for (Limite l : limites) {
            if (l.incluye(fecha)) return l;
        }
        throw new IllegalArgumentException("No hay un límite definido para ese período");
    }

    // ===================== CU1: Registrar consumo y notificar =====================

    public List<String> registrar(Consumo c) {
        repositorio.guardar(c);
        return calcularAlertas(c);
    }
    private List<String> calcularAlertas(Consumo c) { // CU12: lo usan CU1 (registrar) y CU2 (modificar)
        List<String> alertas = new ArrayList<>();
        for (Limite l : limites) {
            if (!l.aplica(c)) continue; // solo los límites que corresponden a este consumo
            String alerta = alertaDe(l, calcularGastado(l));
            if (alerta != null) alertas.add(alerta);
        }
        return alertas;
    }

    // CU12: arma el mensaje de alerta según el semáforo (null si está en VERDE).
    // Lo usan calcularAlertas (CU1 y CU2) y consultarSaldo (CU6).
    private String alertaDe(Limite l, double gastado) {
        Semaforo estado = l.estado(gastado);
        String detalle = String.format(" - gastaste %.2f de %.2f", gastado, l.getMonto());
        if (estado.equals(Semaforo.ROJO)) {
            return "ALERTA: Límite excedido (ROJO)" + detalle;
        } else if (estado.equals(Semaforo.AMARILLO)) {
            return "ADVERTENCIA: Cerca del límite (AMARILLO)" + detalle;
        }
        return null;
    }

    // suma los consumos guardados que caen dentro del límite
    private double calcularGastado(Limite l) {
        double total = 0;
        for (Consumo c : repositorio.listar()) {
            if (l.aplica(c)) total += c.getMonto();
        }
        return total;
    }

    // ===================== CU7: Gestionar categorías =====================

    // true si la agregó; false si ya existía una con ese nombre
    public boolean agregarCategoria(String nombre) {
        if (existeNombreCategoria(nombre, -1)) {
            return false;
        }
        repositorio.guardarCategoria(new Categoria(0, nombre));
        return true;
    }

    public List<Categoria> getCategorias() {
        return repositorio.listarCategorias();
    }

    // IllegalArgumentException si no existe; IllegalStateException si el nombre ya lo usa otra categoría
    public void modificarCategoria(int id, String nuevoNombre) {
        Categoria categoria = buscarCategoria(id);
        if (existeNombreCategoria(nuevoNombre, id)) {
            throw new IllegalStateException("Ya existe una categoría con ese nombre");
        }
        categoria.setNombre(nuevoNombre);
        repositorio.actualizarCategoria(categoria);
    }

    // IllegalArgumentException si no existe; IllegalStateException si tiene consumos asociados
    public void eliminarCategoria(int id) {
        buscarCategoria(id);
        int asociados = 0;
        for (Consumo c : repositorio.listar()) {
            if (c.getCategoria().getId() == id) asociados++;
        }
        if (asociados > 0) {
            throw new IllegalStateException(
                    "No se puede eliminar: la categoría tiene " + asociados + " consumo(s) asociado(s)");
        }
        repositorio.eliminarCategoria(id);
    }

    private Categoria buscarCategoria(int id) {
        for (Categoria c : repositorio.listarCategorias()) {
            if (c.getId() == id) return c;
        }
        throw new IllegalArgumentException("La categoría no existe");
    }

    // ignora la categoría cuyo id es idExcluido (para poder "modificar" sin chocar consigo misma)
    private boolean existeNombreCategoria(String nombre, int idExcluido) {
        for (Categoria c : repositorio.listarCategorias()) {
            if (c.getId() != idExcluido && c.getNombre().equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
    }

    // ===================== CU8: Gestionar medios de pago =====================

    // true si lo agregó; false si ya existía uno con ese nombre
    public boolean agregarMedioPago(String nombre) {
        if (existeNombre(nombre, -1)) {
            return false;
        }
        repositorio.guardarMedioPago(new MedioPago(0, nombre));
        return true;
    }

    public List<MedioPago> getMediosPago() {
        return repositorio.listarMediosPago();
    }

    // IllegalArgumentException si no existe; IllegalStateException si el nombre ya lo usa otro medio
    public void modificarMedioPago(int id, String nuevoNombre) {
        MedioPago medio = buscarMedio(id);
        if (existeNombre(nuevoNombre, id)) {
            throw new IllegalStateException("Ya existe un medio de pago con ese nombre");
        }
        medio.setNombre(nuevoNombre);
        repositorio.actualizarMedioPago(medio);
    }

    // IllegalArgumentException si no existe; IllegalStateException si tiene consumos asociados
    public void eliminarMedioPago(int id) {
        buscarMedio(id);
        int asociados = 0;
        for (Consumo c : repositorio.listar()) {
            if (c.getMedioDePago().getId() == id) asociados++;
        }
        if (asociados > 0) {
            throw new IllegalStateException(
                    "No se puede eliminar: el medio de pago tiene " + asociados + " consumo(s) asociado(s)");
        }
        repositorio.eliminarMedioPago(id);
    }

    private MedioPago buscarMedio(int id) {
        for (MedioPago m : repositorio.listarMediosPago()) {
            if (m.getId() == id) return m;
        }
        throw new IllegalArgumentException("El medio de pago no existe");
    }

    // ignora el medio cuyo id es idExcluido (para poder "modificar" sin chocar consigo mismo)
    private boolean existeNombre(String nombre, int idExcluido) {
        for (MedioPago m : repositorio.listarMediosPago()) {
            if (m.getId() != idExcluido && m.getNombre().equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
    }

    // ===================== COMPARTIDO CU4, CU9 y CU10: consulta y reportes =====================

    // Devuelve los consumos que cumplen TODOS los criterios, ordenados por fecha.
    // Lista de criterios vacía -> devuelve todos los consumos.
    public List<Consumo> consultarConsumos(List<Criterio> criterios) {
        List<Consumo> resultado = new ArrayList<>();
        for (Consumo c : repositorio.listar()) {
            if (cumpleTodos(c, criterios)) resultado.add(c);
        }
        resultado.sort(Comparator.comparing(Consumo::getFecha));
        return resultado;
    }

    private boolean cumpleTodos(Consumo c, List<Criterio> criterios) {
        for (Criterio k : criterios) {
            if (!k.cumple(c)) return false;
        }
        return true;
    }

    // Filtra los consumos por rango de fechas y se los pasa al reporte
    // (ReporteEstadistico en CU9, ReporteGastosPeriodo en CU10).
    // IllegalArgumentException si las fechas son inválidas.
    public String generarReporte(Reporte reporte, LocalDate desde, LocalDate hasta) {
        List<Criterio> criterios = new ArrayList<>();
        criterios.add(new CriterioRangoFechas(desde, hasta));
        return reporte.generar(consultarConsumos(criterios), desde, hasta);
    }

    
    // ===================== CU2: Modificar consumo =====================

    // todos los consumos, ordenados por fecha (para elegir cuál modificar o eliminar)
    public List<Consumo> listarConsumos() {
        List<Consumo> consumos = repositorio.listar();
        consumos.sort(Comparator.comparing(Consumo::getFecha));
        return consumos;
    }

    // reemplaza el consumo guardado por el editado y vuelve a revisar los límites (CU12)
    // IllegalArgumentException si el consumo no existe
    public List<String> modificarConsumo(Consumo editado) {
        buscarConsumo(editado.getId());
        repositorio.actualizar(editado);
        return calcularAlertas(editado);
    }

    // ===================== CU3: Eliminar consumo =====================

    // IllegalArgumentException si el consumo no existe
    public void eliminarConsumo(int id) {
        buscarConsumo(id);
        repositorio.eliminar(id);
    }

    private Consumo buscarConsumo(int id) {
        for (Consumo c : repositorio.listar()) {
            if (c.getId() == id) return c;
        }
        throw new IllegalArgumentException("El consumo no existe");
    }
}
