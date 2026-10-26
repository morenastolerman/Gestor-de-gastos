package Codigo;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Borrá consumos.csv, medios.csv y categorias.csv antes de correr para empezar de cero
        Controller controller = new Controller(new RepositorioArchivo());

        // ---- CU7: categorías ----
        mostrar(controller.agregarCategoria("Alimentos"));
        mostrar(controller.agregarCategoria("alimentos"));   // ya existe
        mostrar(controller.agregarCategoria("   "));         // inválido
        mostrar(controller.agregarCategoria("Alquiler"));
        mostrar(controller.agregarCategoria("Transporte"));
        mostrar(controller.agregarCategoria("Ocio"));

        Categoria alimentos = buscarCategoria(controller, "Alimentos");
        Categoria ocio = buscarCategoria(controller, "Ocio");
        Categoria transporte = buscarCategoria(controller, "Transporte");

        mostrar(controller.modificarCategoria(ocio.getId(), "Entretenimiento"));
        mostrar(controller.modificarCategoria(ocio.getId(), "Alimentos"));  // nombre en uso

        // ---- CU8: medios de pago ----
        mostrar(controller.agregarMedioPago("Efectivo"));
        mostrar(controller.agregarMedioPago("efectivo"));  // ya existe
        mostrar(controller.agregarMedioPago("   "));       // inválido
        mostrar(controller.agregarMedioPago("Tarjeta"));

        MedioPago efectivo = buscarMedio(controller, "Efectivo");
        MedioPago tarjeta = buscarMedio(controller, "Tarjeta");

        mostrar(controller.modificarMedioPago(tarjeta.getId(), "Tarjeta de crédito"));

        // ---- CU1 ----
        try {
            controller.agregarLimite(new LimiteMensual(30000, LocalDate.now()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("No se pudo agregar el límite: " + e.getMessage());
        }

        mostrar(controller.ingresarDatosConsumo(20000, LocalDate.now(), "Super", 1, alimentos, efectivo));    // verde
        mostrar(controller.ingresarDatosConsumo(5000, LocalDate.now(), "Cena", 1, alimentos, efectivo));      // amarillo
        mostrar(controller.ingresarDatosConsumo(4000, LocalDate.now(), "Delivery", 1, alimentos, efectivo));  // rojo
        mostrar(controller.ingresarDatosConsumo(100, LocalDate.now(), "Sin medio", 1, alimentos, null));      // inválido
        mostrar(controller.ingresarDatosConsumo(100, LocalDate.now(), "Sin categoría", 1, null, efectivo));   // inválido

        // ---- Eliminar ----
        mostrar(controller.eliminarCategoria(alimentos.getId()));   // tiene consumos -> no se puede
        mostrar(controller.eliminarCategoria(transporte.getId()));  // sin consumos -> se elimina
        mostrar(controller.eliminarMedioPago(efectivo.getId()));    // tiene consumos -> no se puede
        mostrar(controller.eliminarMedioPago(tarjeta.getId()));     // sin consumos -> se elimina

        System.out.println("Categorías: " + controller.obtenerCategorias());
        System.out.println("Medios de pago: " + controller.obtenerMediosPago());
    }

    private static void mostrar(List<String> mensajes) {
        for (String m : mensajes) System.out.println(m);
    }

    private static Categoria buscarCategoria(Controller controller, String nombre) {
        for (Categoria c : controller.obtenerCategorias()) {
            if (c.getNombre().equalsIgnoreCase(nombre)) return c;
        }
        throw new IllegalStateException("No se encontró la categoría " + nombre);
    }

    private static MedioPago buscarMedio(Controller controller, String nombre) {
        for (MedioPago m : controller.obtenerMediosPago()) {
            if (m.getNombre().equalsIgnoreCase(nombre)) return m;
        }
        throw new IllegalStateException("No se encontró el medio de pago " + nombre);
    }
}