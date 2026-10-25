package Codigo;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller(new RepositorioArchivo());

        Categoria comida = new Categoria(1, "Comida");
        for (String m : controller.agregarMedioPago("Efectivo")) System.out.println(m);
        for (String m : controller.agregarMedioPago("efectivo")) System.out.println(m); // ya existe
        for (String m : controller.agregarMedioPago("   "))      System.out.println(m); // inválido
        for (String m : controller.agregarMedioPago("Tarjeta"))  System.out.println(m);

        MedioPago efectivo = controller.obtenerMediosPago().get(0);
        controller.ingresarDatosConsumo(20000, LocalDate.now(), "Super", 1, comida, efectivo);


        try {
            controller.agregarLimite(new LimiteMensual(100000, LocalDate.now()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("No se pudo agregar el límite: " + e.getMessage());
        }

        controller.ingresarDatosConsumo(20000, LocalDate.now(), "Super", 1, comida, efectivo);
        controller.ingresarDatosConsumo(5000, LocalDate.now(), "Cena", 1, comida, efectivo); // 25000/30000 → amarillo
        controller.ingresarDatosConsumo(4000, LocalDate.now(), "Delivery", 1, comida, efectivo); // 29000/30000 → rojo
    }
}
