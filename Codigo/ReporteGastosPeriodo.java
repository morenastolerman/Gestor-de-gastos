package Codigo;
import java.time.LocalDate;
import java.util.List;

public class ReporteGastosPeriodo implements Reporte{

    public String generar(List<Consumo> consumo, LocalDate desde, LocalDate hasta){
        return "hola";
    }
}
