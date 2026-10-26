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
}
 