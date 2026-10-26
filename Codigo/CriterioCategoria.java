package Codigo;
 
public class CriterioCategoria implements Criterio {
    private final Categoria categoria;
 
    public CriterioCategoria(Categoria categoria) {
        if (categoria == null)
            throw new IllegalArgumentException("La categoría es obligatoria");
        this.categoria = categoria;
    }
 
    @Override
    public boolean cumple(Consumo c) {
        return c.getCategoria().getId() == categoria.getId();
    }
}
 