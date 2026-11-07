package Codigo;

import java.time.LocalDate;
import java.time.YearMonth;

public class LimiteMensual extends Limite {

    public LimiteMensual(double monto, LocalDate fechaReferencia) {
        super(monto, fechaReferencia);
    }

    @Override
    public boolean aplica(Consumo c) {
        return incluye(c.getFecha());
    }

    // la fecha es del mismo mes y año que el límite
    @Override
    public boolean incluye(LocalDate fecha) {
        return YearMonth.from(fecha).equals(YearMonth.from(this.fechaReferencia));
    }

    @Override
    public boolean mismoPeriodo(Limite otro) {
        if (!(otro instanceof LimiteMensual)) return false;
        return YearMonth.from(this.fechaReferencia)
                .equals(YearMonth.from(((LimiteMensual) otro).fechaReferencia));
    }

    // ej.: "10/2026 - $10000.00"
    @Override
    public String toString() {
        return String.format("%02d/%d - $%.2f", fechaReferencia.getMonthValue(), fechaReferencia.getYear(), monto);
    }
}
