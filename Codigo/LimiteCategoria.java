package Codigo;

import java.time.LocalDate;

public class LimiteCategoria extends Limite {
    private Categoria categoria;

    public LimiteCategoria(double monto,  LocalDate fechaInicio, double umbral, Categoria categoria) {
        super(monto, fechaInicio, umbral);
        this.categoria = categoria;
    }
    
    @Override
    public boolean aplica(Consumo c) {
        boolean mismoMes = c.getFecha().getMonth() == this.fechaInicio.getMonth() &&
                           c.getFecha().getYear() == this.fechaInicio.getYear();
                           
        return mismoMes && c.getCategoria().getId() == this.categoria.getId();
    }
}
