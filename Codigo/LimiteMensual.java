package Codigo;

import java.time.LocalDate;
import java.time.YearMonth;

public class LimiteMensual extends Limite {

    public LimiteMensual(double monto, LocalDate fechaReferencia) {
        super(monto, fechaReferencia);
    }

    @Override
    public boolean aplica(Consumo c) {
        return YearMonth.from(c.getFecha()).equals(YearMonth.from(this.fechaReferencia));
    }

    @Override
    public boolean mismoPeriodo(Limite otro) {
        if (!(otro instanceof LimiteMensual)) return false;
        return YearMonth.from(this.fechaReferencia)
                .equals(YearMonth.from(((LimiteMensual) otro).fechaReferencia));
    }
}