package Codigo;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller(new RepositorioArchivo());
        new Vista(controller).iniciar();
    }
}