package Codigo;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller(new RepositorioArchivo());

        Categoria comida = new Categoria(1, "Comida");
        MedioPago efectivo = new MedioPago(1, "Efectivo");

        controller.definirLimite(new LimiteMensual(100000, LocalDate.now(), 100000));
        controller.ingresarDatosConsumo(20000, LocalDate.now(), "Super", 1, comida, efectivo);
        controller.ingresarDatosConsumo(5000, LocalDate.now(), "Cena", 1, comida, efectivo); // 25000/30000 → amarillo
        controller.ingresarDatosConsumo(4000, LocalDate.now(), "Delivery", 1, comida, efectivo); // 29000/30000 → rojo
    }
}
