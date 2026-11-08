package Codigo;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Vista {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter MES = DateTimeFormatter.ofPattern("MM/yyyy");

    private final Controller controller;
    private final Scanner in = new Scanner(System.in);

    public Vista(Controller controller) {
        this.controller = controller;
    }

    // ===================== MENÚ PRINCIPAL =====================

    public void iniciar() {
        mostrar(controller.cargarConfiguracion());   // CU11: carga la configuración inicial
        int opcion;
        do {
            // cada opción tiene el mismo número que su caso de uso
            System.out.println("\n===== CONTROL DE GASTOS =====");
            System.out.println("1. Registrar consumo");
            System.out.println("2. Modificar consumo");
            System.out.println("3. Eliminar consumo");
            System.out.println("4. Consultar y filtrar consumos");
            System.out.println("5. Definir o modificar límite mensual");
            System.out.println("6. Consultar saldo y estado");
            System.out.println("7. Gestionar categorías");
            System.out.println("8. Gestionar medios de pago");
            System.out.println("9. Reporte estadístico");
            System.out.println("10. Gastos del período");
            System.out.println("0. Salir");
            opcion = leerEntero("Opción: ");

            switch (opcion) {
                case 1: registrarConsumo(); break;
                case 2: modificarConsumo(); break;
                case 3: eliminarConsumo(); break;
                case 4: consultarConsumos(); break;
                case 5: menuLimites(); break;
                case 6: consultarSaldo(); break;
                case 7: menuCategorias(); break;
                case 8: menuMediosPago(); break;
                case 9: reporteEstadistico(); break;
                case 10: menuGastosPeriodo(); break;
                case 0: System.out.println("Hasta luego"); break;
                default: System.out.println("Opción inválida");
            }
        } while (opcion != 0);
    }

    // ===================== CU5: Definir y modificar límite =====================

    private void menuLimites() {
        System.out.println("\n--- Límite mensual ---");
        List<Limite> limites = controller.obtenerLimites();
        System.out.println(limites.isEmpty() ? "No hay límites definidos" : "Límites definidos: " + limites);
        System.out.println("1. Definir límite");
        System.out.println("2. Modificar límite");
        switch (leerEntero("Opción: ")) {
            case 1: definirLimite(); break;
            case 2: modificarLimite(); break;
            default: System.out.println("Opción inválida");
        }
    }

    private void definirLimite() {
        double monto = leerDecimal("Monto del límite: ");
        YearMonth mes = leerMes("Mes del límite (MM/yyyy): ");
        mostrar(controller.definirLimite(monto, mes));
    }

    private void modificarLimite() {
        YearMonth mes = leerMes("Mes del límite a modificar (MM/yyyy): ");
        double monto = leerDecimal("Nuevo monto: ");
        mostrar(controller.modificarLimite(monto, mes));
    }

    // ===================== CU6: Consultar saldo y estado =====================

    private void consultarSaldo() {
        YearMonth mes = leerMes("Mes a consultar (MM/yyyy): ");
        mostrar(controller.consultarSaldo(mes));
    }

    // ===================== CU1: Registrar consumo y notificar =====================

    private void registrarConsumo() {
        double monto = leerDecimal("Monto: ");
        LocalDate fecha = leerFecha("Fecha (dd/MM/yyyy): ", false);
        System.out.print("Descripción: ");
        String descripcion = in.nextLine();
        int cuotas = leerEntero("Cuotas: ");

        System.out.println("Categorías: " + controller.obtenerCategorias());
        Categoria categoria = buscarCategoria(leerEntero("Id de categoría: "));

        System.out.println("Medios de pago: " + controller.obtenerMediosPago());
        MedioPago medio = buscarMedio(leerEntero("Id de medio de pago: "));

        mostrar(controller.ingresarDatosConsumo(monto, fecha, descripcion, cuotas, categoria, medio));
    }

    // ===================== CU7: Gestionar categorías =====================

    private void menuCategorias() {
        System.out.println("\n--- Categorías ---");
        System.out.println("1. Listar");
        System.out.println("2. Agregar");
        System.out.println("3. Modificar");
        System.out.println("4. Eliminar");
        switch (leerEntero("Opción: ")) {
            case 1:
                System.out.println(controller.obtenerCategorias());
                break;
            case 2:
                System.out.print("Nombre: ");
                mostrar(controller.agregarCategoria(in.nextLine()));
                break;
            case 3:
                System.out.println(controller.obtenerCategorias());
                int idMod = leerEntero("Id a modificar: ");
                System.out.print("Nuevo nombre: ");
                mostrar(controller.modificarCategoria(idMod, in.nextLine()));
                break;
            case 4:
                System.out.println(controller.obtenerCategorias());
                mostrar(controller.eliminarCategoria(leerEntero("Id a eliminar: ")));
                break;
            default:
                System.out.println("Opción inválida");
        }
    }

    // ===================== CU8: Gestionar medios de pago =====================

    private void menuMediosPago() {
        System.out.println("\n--- Medios de pago ---");
        System.out.println("1. Listar");
        System.out.println("2. Agregar");
        System.out.println("3. Modificar");
        System.out.println("4. Eliminar");
        switch (leerEntero("Opción: ")) {
            case 1:
                System.out.println(controller.obtenerMediosPago());
                break;
            case 2:
                System.out.print("Nombre: ");
                mostrar(controller.agregarMedioPago(in.nextLine()));
                break;
            case 3:
                System.out.println(controller.obtenerMediosPago());
                int idMod = leerEntero("Id a modificar: ");
                System.out.print("Nuevo nombre: ");
                mostrar(controller.modificarMedioPago(idMod, in.nextLine()));
                break;
            case 4:
                System.out.println(controller.obtenerMediosPago());
                mostrar(controller.eliminarMedioPago(leerEntero("Id a eliminar: ")));
                break;
            default:
                System.out.println("Opción inválida");
        }
    }

    // ===================== CU9: Consultar reporte estadístico =====================

    private void reporteEstadistico() {
        LocalDate desde = leerFecha("Desde (dd/MM/yyyy): ", false);
        LocalDate hasta = leerFecha("Hasta (dd/MM/yyyy): ", false);
        System.out.println();
        System.out.println(controller.generarReporteEstadistico(desde, hasta));
    }

    // ===================== CU10: Consultar gastos del período =====================

    private void menuGastosPeriodo() {
        System.out.println("\n--- Gastos del período ---");
        System.out.println("1. Reporte de gastos por rango de fechas");
        System.out.println("2. Reporte de gastos de un mes");
        switch (leerEntero("Opción: ")) {
            case 1:
                LocalDate desde = leerFecha("Desde (dd/MM/yyyy): ", false);
                LocalDate hasta = leerFecha("Hasta (dd/MM/yyyy): ", false);
                System.out.println();
                System.out.println(controller.generarReporteGastos(desde, hasta));
                break;
            case 2:
                YearMonth mes = leerMes("Mes (MM/yyyy): ");
                System.out.println();
                System.out.println(controller.generarReporteGastosMensual(mes));
                break;
            default:
                System.out.println("Opción inválida");
        }
    }

    // ===================== CU4: Consultar y filtrar consumos =====================

    // todos los filtros son opcionales
    private void consultarConsumos() {
        System.out.println("(Enter vacío en las fechas = sin filtro de fechas)");
        LocalDate desde = leerFecha("Desde (dd/MM/yyyy): ", true);
        LocalDate hasta = leerFecha("Hasta (dd/MM/yyyy): ", true);

        System.out.println("Categorías: " + controller.obtenerCategorias());
        int idCategoria = leerEntero("Id de categoría (0 = todas): ");
        Categoria categoria = idCategoria == 0 ? null : buscarCategoria(idCategoria);
        if (idCategoria != 0 && categoria == null) {
            System.out.println("La categoría no existe");
            return;
        }

        System.out.println("Medios de pago: " + controller.obtenerMediosPago());
        int idMedio = leerEntero("Id de medio de pago (0 = todos): ");
        MedioPago medio = idMedio == 0 ? null : buscarMedio(idMedio);
        if (idMedio != 0 && medio == null) {
            System.out.println("El medio de pago no existe");
            return;
        }

        String palabraClave = leerTexto("Palabra clave o #hashtag (Enter = sin filtro): ", "");
        mostrar(controller.consultarConsumos(desde, hasta, categoria, medio, palabraClave));
    }

    // ===================== AUXILIARES (usados por todos los CU) =====================

    private void mostrar(List<String> mensajes) {
        for (String m : mensajes) System.out.println(m);
    }

    private Categoria buscarCategoria(int id) {
        for (Categoria c : controller.obtenerCategorias()) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    private MedioPago buscarMedio(int id) {
        for (MedioPago m : controller.obtenerMediosPago()) {
            if (m.getId() == id) return m;
        }
        return null;
    }

    private int leerEntero(String texto) {
        while (true) {
            System.out.print(texto);
            try {
                return Integer.parseInt(in.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingresá un número entero");
            }
        }
    }

    private double leerDecimal(String texto) {
        while (true) {
            System.out.print(texto);
            try {
                return Double.parseDouble(in.nextLine().trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Ingresá un número válido");
            }
        }
    }

    // si opcional es true, Enter vacío devuelve null
    private LocalDate leerFecha(String texto, boolean opcional) {
        while (true) {
            System.out.print(texto);
            String s = in.nextLine().trim();
            if (s.isEmpty() && opcional) return null;
            try {
                return LocalDate.parse(s, FECHA);
            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido, usá dd/MM/yyyy");
            }
        }
    }

    private YearMonth leerMes(String texto) {
        while (true) {
            System.out.print(texto);
            try {
                return YearMonth.parse(in.nextLine().trim(), MES);
            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido, usá MM/yyyy");
            }
        }
    }

    // ===================== CU2: Modificar consumo =====================

    private void modificarConsumo() {
        Consumo actual = elegirConsumo();
        if (actual == null) return;

        System.out.println("(Enter vacío = deja el valor actual)");
        double monto = leerDecimal("Monto [" + actual.getMonto() + "]: ", actual.getMonto());
        LocalDate fecha = leerFecha("Fecha [" + actual.getFecha().format(FECHA) + "]: ", actual.getFecha());
        String descripcion = leerTexto("Descripción [" + actual.getDescripcion() + "]: ", actual.getDescripcion());
        int cuotas = leerEntero("Cuotas [" + actual.getCuotas() + "]: ", actual.getCuotas());

        System.out.println("Categorías: " + controller.obtenerCategorias());
        int idCategoria = leerEntero("Id de categoría [" + actual.getCategoria().getId() + "]: ",
                actual.getCategoria().getId());
        Categoria categoria = buscarCategoria(idCategoria);

        System.out.println("Medios de pago: " + controller.obtenerMediosPago());
        int idMedio = leerEntero("Id de medio de pago [" + actual.getMedioDePago().getId() + "]: ",
                actual.getMedioDePago().getId());
        MedioPago medio = buscarMedio(idMedio);

        mostrar(controller.modificarConsumo(actual.getId(), monto, fecha, descripcion, cuotas, categoria, medio));
    }

    // ===================== CU3: Eliminar consumo =====================

    private void eliminarConsumo() {
        Consumo elegido = elegirConsumo();
        if (elegido == null) return;

        String respuesta = leerTexto("¿Eliminar \"" + elegido.getDescripcion() + "\"? (s/n): ", "n");
        if (!respuesta.equalsIgnoreCase("s")) {
            System.out.println("No se eliminó el consumo");
            return;
        }
        mostrar(controller.eliminarConsumo(elegido.getId()));
    }

    // ===================== AUXILIARES de CU2 y CU3 =====================

    // muestra los consumos y devuelve el elegido por id (null si no hay o no existe)
    private Consumo elegirConsumo() {
        List<Consumo> consumos = controller.listarConsumos();
        if (consumos.isEmpty()) {
            System.out.println("No hay consumos registrados");
            return null;
        }
        for (Consumo c : consumos) System.out.println(describir(c));

        int id = leerEntero("Id del consumo: ");
        for (Consumo c : consumos) {
            if (c.getId() == id) return c;
        }
        System.out.println("El consumo no existe");
        return null;
    }

    private String describir(Consumo c) {
        return String.format("%d | %s | %s | %s | %s | %.2f", c.getId(), c.getFecha().format(FECHA),
                c.getDescripcion(), c.getCategoria().getNombre(), c.getMedioDePago().getNombre(), c.getMonto());
    }

    // lectura con valor actual: Enter vacío devuelve el valor actual
    private String leerTexto(String texto, String actual) {
        System.out.print(texto);
        String s = in.nextLine().trim();
        return s.isEmpty() ? actual : s;
    }

    private int leerEntero(String texto, int actual) {
        while (true) {
            System.out.print(texto);
            String s = in.nextLine().trim();
            if (s.isEmpty()) return actual;
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Ingresá un número entero");
            }
        }
    }

    private double leerDecimal(String texto, double actual) {
        while (true) {
            System.out.print(texto);
            String s = in.nextLine().trim();
            if (s.isEmpty()) return actual;
            try {
                return Double.parseDouble(s.replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Ingresá un número válido");
            }
        }
    }

    private LocalDate leerFecha(String texto, LocalDate actual) {
        while (true) {
            System.out.print(texto);
            String s = in.nextLine().trim();
            if (s.isEmpty()) return actual;
            try {
                return LocalDate.parse(s, FECHA);
            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido, usá dd/MM/yyyy");
            }
        }
    }
}
