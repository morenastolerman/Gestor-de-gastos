package Codigo;
import java.time.LocalDate;
import java.util.List;

public interface Reporte {

    //+generar(consumos: List<Consumo>, desde: Date, hasta: Date) : String
    public String generar(List<Consumo> consumo, LocalDate desde, LocalDate hasta);

    
}