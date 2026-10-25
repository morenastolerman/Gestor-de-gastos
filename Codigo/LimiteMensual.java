package Codigo;

import java.time.LocalDate;
import java.time.YearMonth;

public class LimiteMensual extends Limite {

    public LimiteMensual(double monto, LocalDate fechaInicio, double umbral) {
        super(monto, fechaInicio, umbral);
    }

    @Override
    public boolean aplica(Consumo c) {
        return c.getFecha().getMonth() == this.fechaInicio.getMonth() &&
               c.getFecha().getYear() == this.fechaInicio.getYear();
    }
    
    @Override
    public boolean mismoPeriodo(Limite otro) {
        if (!(otro instanceof LimiteMensual)) return false;
        return YearMonth.from(this.fechaInicio)
                .equals(YearMonth.from(((LimiteMensual) otro).fechaInicio));
    }
    
}
