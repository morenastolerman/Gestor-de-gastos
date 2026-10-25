package Codigo;

import java.util.List;

public interface Repositorio {
    
    public void guardar(Consumo c);
    public List<Consumo> listar();
//----------------------------------------------------
   /* public void guardarCategoria(Categoria c);
    public List<Categoria> listarCategorias();
    public void actualizarCategoria(Categoria c);
    public void eliminarCategoria(int id);*/
    
}
