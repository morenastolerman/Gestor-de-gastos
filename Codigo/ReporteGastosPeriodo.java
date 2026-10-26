package Codigo;
 
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
 
// CU9: listado de consumos del período con su total
public class ReporteGastosPeriodo implements Reporte {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final Categoria categoria; // opcional: solo para mostrarla en el título
 
    public ReporteGastosPeriodo() {
        this(null);
    }
 
    public ReporteGastosPeriodo(Categoria categoria) {
        this.categoria = categoria;
    }
 
    @Override
    public String generar(List<Consumo> consumos, LocalDate desde, LocalDate hasta) {
        StringBuilder sb = new StringBuilder();
        sb.append("REPORTE DE GASTOS del ").append(desde.format(FORMATO))
          .append(" al ").append(hasta.format(FORMATO)).append("\n");
        if (categoria != null) {
            sb.append("Categoría: ").append(categoria.getNombre()).append("\n");
        }
 
        if (consumos.isEmpty()) {
            sb.append("No hay consumos en el período\n");
            return sb.toString();
        }
 
        List<Consumo> ordenados = new ArrayList<>(consumos);
        ordenados.sort(Comparator.comparing(Consumo::getFecha));
 
        double total = 0;
        for (Consumo c : ordenados) {
            sb.append(String.format("%s | %-20s | %-15s | %-15s | $%.2f%n",
                    c.getFecha().format(FORMATO),
                    c.getDescripcion(),
                    c.getCategoria().getNombre(),
                    c.getMedioDePago().getNombre(),
                    c.getMonto()));
            total += c.getMonto();
        }
        sb.append(String.format("Cantidad de consumos: %d%n", ordenados.size()));
        sb.append(String.format("TOTAL: $%.2f%n", total));
        return sb.toString();
    }
}
 