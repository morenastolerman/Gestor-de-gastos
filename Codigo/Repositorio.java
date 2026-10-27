package Codigo;

import java.util.List;

public interface Repositorio {

    // ===================== CU1: Registrar consumo y notificar =====================
    // (listar() también lo usan CU7, CU8, CU9 y CU10)
    void guardar(Consumo c);
    List<Consumo> listar();

    // ===================== CU7: Gestionar categorías =====================
    void guardarCategoria(Categoria c);          // asigna el id a la categoría
    List<Categoria> listarCategorias();
    void actualizarCategoria(Categoria c);
    void eliminarCategoria(int id);

    // ===================== CU8: Gestionar medios de pago =====================
    void guardarMedioPago(MedioPago m);          // asigna el id al medio
    List<MedioPago> listarMediosPago();
    void actualizarMedioPago(MedioPago m);
    void eliminarMedioPago(int id);
}