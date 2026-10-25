package Codigo;
public class Categoria {
    private int id;
    private String nombre;
    

    public Categoria(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    public String getNombre() {
        return nombre;
    }
    
    public int getId() {
        return id;
    }

    //------------------------------------------------------------------

    public Categoria(String nombre) {
        this(0, nombre);
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    

}
