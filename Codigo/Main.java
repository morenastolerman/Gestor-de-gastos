package Codigo;

import javax.swing.SwingUtilities;

public class Main {
    // Por defecto abre la interfaz gráfica; con el argumento "--consola" abre la vista de consola
    public static void main(String[] args) {
        Controller controller = new Controller(new RepositorioArchivo());
        if (args.length > 0 && args[0].equals("--consola")) {
            new Vista(controller).iniciar();
        } else {
            SwingUtilities.invokeLater(() -> new VistaGrafica(controller).mostrar());
        }
    }
}
