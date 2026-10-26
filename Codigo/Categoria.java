package Codigo;

import java.util.Objects;

public class Categoria {
    private int id;
    private String nombre;

    public Categoria(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Categoria(String nombre) {
        this(0, nombre);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Dos categorías son iguales si tienen el mismo id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Categoria)) return false;
        return id == ((Categoria) o).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + " - " + nombre;
    }
}