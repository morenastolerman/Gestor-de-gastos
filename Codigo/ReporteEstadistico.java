package Codigo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// CU9: Consultar reporte estadístico (cuánto se gastó en el período y en qué, desglose por categoría)
public class ReporteEstadistico implements Reporte {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public String generar(List<Consumo> consumos, LocalDate desde, LocalDate hasta) {
        StringBuilder sb = new StringBuilder();
        sb.append("REPORTE ESTADÍSTICO del ").append(desde.format(FORMATO))
          .append(" al ").append(hasta.format(FORMATO)).append("\n");

        if (consumos.isEmpty()) {
            sb.append("No hay consumos en el período\n");
            return sb.toString();
        }

        double total = 0;
        Map<String, Double> porCategoria = new HashMap<>();
        for (Consumo c : consumos) {
            total += c.getMonto();
            porCategoria.merge(c.getCategoria().getNombre(), c.getMonto(), Double::sum);
        }

        sb.append(String.format("Total gastado: $%.2f (%d consumos)%n", total, consumos.size()));
        sb.append("Gasto por categoría:\n");

        List<Map.Entry<String, Double>> entradas = new ArrayList<>(porCategoria.entrySet());
        entradas.sort((a, b) -> Double.compare(b.getValue(), a.getValue())); // de mayor a menor

        for (Map.Entry<String, Double> e : entradas) {
            sb.append(String.format("  %-15s $%.2f (%.1f%%)%n",
                    e.getKey(), e.getValue(), e.getValue() * 100 / total));
        }
        sb.append("Mayor gasto: ").append(entradas.get(0).getKey()).append("\n");
        return sb.toString();
    }
}