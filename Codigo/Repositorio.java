package Codigo;
 
import java.util.List;
 
public interface Repositorio {
 
    // Consumos
    void guardar(Consumo c);
    List<Consumo> listar();
 
    // Medios de pago
    void guardarMedioPago(MedioPago m);          // asigna el id al medio
    List<MedioPago> listarMediosPago();
    void actualizarMedioPago(MedioPago m);
    void eliminarMedioPago(int id);

    // Categorías
    void guardarCategoria(Categoria c);          // asigna el id a la categoría
    List<Categoria> listarCategorias();
    void actualizarCategoria(Categoria c);
    void eliminarCategoria(int id);
}
 