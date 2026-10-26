package Codigo;

import java.time.LocalDate;

public class CriterioRangoFechas implements Criterio {
    private final LocalDate desde;
    private final LocalDate hasta;

    public CriterioRangoFechas(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null)
            throw new IllegalArgumentException("Las fechas desde y hasta son obligatorias");
        if (desde.isAfter(hasta))
            throw new IllegalArgumentException("La fecha desde no puede ser posterior a la fecha hasta");
        this.desde = desde;
        this.hasta = hasta;
    }

    // incluye ambos extremos
    @Override
    public boolean cumple(Consumo c) {
        return !c.getFecha().isBefore(desde) && !c.getFecha().isAfter(hasta);
    }
}