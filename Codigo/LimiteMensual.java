package Codigo;

import java.time.LocalDate;

public class LimiteMensual extends Limite {

    public LimiteMensual(double monto, LocalDate fechaInicio, double umbral) {
        super(monto, fechaInicio, umbral);
    }

    @Override
    public boolean aplica(Consumo c) {
        return c.getFecha().getMonth() == this.fechaInicio.getMonth() &&
               c.getFecha().getYear() == this.fechaInicio.getYear();
    }
    
    
}
