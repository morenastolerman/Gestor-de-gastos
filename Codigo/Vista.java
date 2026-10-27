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
        int opcion;
        do {
            System.out.println("\n===== CONTROL DE GASTOS =====");
            System.out.println("1. Registrar consumo");
            System.out.println("2. Definir límite mensual");
            System.out.println("3. Gestionar categorías");
            System.out.println("4. Gestionar medios de pago");
            System.out.println("5. Reporte estadístico");
            System.out.println("6. Gastos del período");
            System.out.println("0. Salir");
            opcion = leerEntero("Opción: ");

            switch (opcion) {
                case 1: registrarConsumo(); break;
                case 2: definirLimite(); break;
                case 3: menuCategorias(); break;
                case 4: menuMediosPago(); break;
                case 5: reporteEstadistico(); break;
                case 6: menuGastosPeriodo(); break;
                case 0: System.out.println("Hasta luego"); break;
                default: System.out.println("Opción inválida");
            }
        } while (opcion != 0);
    }

    // ===================== CU1: Registrar consumo y notificar =====================

    private void definirLimite() {
        double monto = leerDecimal("Monto del límite: ");
        YearMonth mes = leerMes("Mes del límite (MM/yyyy): ");
        try {
            controller.agregarLimite(new LimiteMensual(monto, mes.atDay(1)));
            System.out.println("Límite agregado correctamente");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("No se pudo agregar el límite: " + e.getMessage());
        }
    }

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
        System.out.println("1. Consultar consumos (filtros opcionales)");
        System.out.println("2. Reporte de gastos por rango de fechas");
        System.out.println("3. Reporte de gastos de un mes");
        switch (leerEntero("Opción: ")) {
            case 1:
                consultarConsumos();
                break;
            case 2:
                LocalDate desde = leerFecha("Desde (dd/MM/yyyy): ", false);
                LocalDate hasta = leerFecha("Hasta (dd/MM/yyyy): ", false);
                System.out.println();
                System.out.println(controller.generarReporteGastos(desde, hasta));
                break;
            case 3:
                YearMonth mes = leerMes("Mes (MM/yyyy): ");
                System.out.println();
                System.out.println(controller.generarReporteGastosMensual(mes));
                break;
            default:
                System.out.println("Opción inválida");
        }
    }

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
        mostrar(controller.consultarConsumos(desde, hasta, categoria));
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
}