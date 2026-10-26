package Codigo;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        // Borrá consumos.csv y medios.csv antes de correr para empezar de cero
        Controller controller = new Controller(new RepositorioArchivo());

        Categoria comida = new Categoria(1, "Comida");

        // ---- CU8 ----
        mostrar(controller.agregarMedioPago("Efectivo"));
        mostrar(controller.agregarMedioPago("efectivo"));  // ya existe
        mostrar(controller.agregarMedioPago("   "));       // inválido
        mostrar(controller.agregarMedioPago("Tarjeta"));

        MedioPago efectivo = buscar(controller, "Efectivo");
        MedioPago tarjeta = buscar(controller, "Tarjeta");

        mostrar(controller.modificarMedioPago(tarjeta.getId(), "Tarjeta de crédito"));

        // ---- CU1 ----
        try {
            controller.agregarLimite(new LimiteMensual(30000, LocalDate.now()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("No se pudo agregar el límite: " + e.getMessage());
        }

        mostrar(controller.ingresarDatosConsumo(20000, LocalDate.now(), "Super", 1, comida, efectivo));    // 20000/30000 verde
        mostrar(controller.ingresarDatosConsumo(5000, LocalDate.now(), "Cena", 1, comida, efectivo));      // 25000/30000 amarillo
        mostrar(controller.ingresarDatosConsumo(4000, LocalDate.now(), "Delivery", 1, comida, efectivo));  // 29000/30000 rojo
        mostrar(controller.ingresarDatosConsumo(100, LocalDate.now(), "Sin medio", 1, comida, null));      // inválido

        // ---- CU8: eliminar ----
        mostrar(controller.eliminarMedioPago(efectivo.getId()));  // tiene consumos -> no se puede
        mostrar(controller.eliminarMedioPago(tarjeta.getId()));   // sin consumos -> se elimina
    }

    private static void mostrar(java.util.List<String> mensajes) {
        for (String m : mensajes) System.out.println(m);
    }

    private static MedioPago buscar(Controller controller, String nombre) {
        for (MedioPago m : controller.obtenerMediosPago()) {
            if (m.getNombre().equalsIgnoreCase(nombre)) return m;
        }
        throw new IllegalStateException("No se encontró el medio de pago " + nombre);
    }
}