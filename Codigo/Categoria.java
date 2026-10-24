package Codigo;
public class Categoria {
    private int id;
    private String nombre;
    public int getId() {
        return id;
    }
    public Categoria(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    public String getNombre() {
        return nombre;
    }
}
