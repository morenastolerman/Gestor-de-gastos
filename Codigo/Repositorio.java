package Codigo;

import java.util.List;

public interface Repositorio {
    
    public void guardar(Consumo c);
    public List<Consumo> listar();
    
}
