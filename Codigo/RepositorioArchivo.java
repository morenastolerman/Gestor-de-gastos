package Codigo;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class RepositorioArchivo implements Repositorio {
    private static final String SEP = ";";
    private final Path archivo;


    public RepositorioArchivo(String ruta) {
        this.archivo = Paths.get(ruta);
    }
    
    public RepositorioArchivo() {
        this("consumos.csv");
    }

    @Override
    public void guardar(Consumo c) {
        c.setId(siguienteId());
        try (BufferedWriter w = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            w.write(aLinea(c));
            w.newLine();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el consumo en " + archivo, e);
        }
    }

    @Override
    public List<Consumo> listar() {
        List<Consumo> consumos = new ArrayList<>();
        if (!Files.exists(archivo)) return consumos;

        try {
            for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
                if (linea.isBlank()) continue;
                consumos.add(deLinea(linea));
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer " + archivo, e);
        }
        return consumos;
    }

    private int siguienteId() {
        int max = 0;
        for (Consumo c : listar()) {
            if (c.getId() > max) max = c.getId();
        }
        return max + 1;
    }

    // Consumo -> línea del CSV
    private String aLinea(Consumo c) {
        return String.join(SEP,
                String.valueOf(c.getId()),
                String.valueOf(c.getMonto()),
                c.getFecha().toString(),
                limpiar(c.getDescripcion()),
                String.valueOf(c.getCuotas()),
                String.valueOf(c.getCategoria().getId()),
                limpiar(c.getCategoria().getNombre()),
                String.valueOf(c.getMedioDePago().getId()),
                limpiar(c.getMedioDePago().getNombre()));
    }

    // línea del CSV -> Consumo
    private Consumo deLinea(String linea) {
        String[] p = linea.split(SEP, -1);
        Categoria categoria = new Categoria(Integer.parseInt(p[5]), p[6]);
        MedioPago medio = new MedioPago(Integer.parseInt(p[7]), p[8]);
        return new Consumo(Integer.parseInt(p[0]), Double.parseDouble(p[1]), LocalDate.parse(p[2]),
                p[3], Integer.parseInt(p[4]), categoria, medio);
    }

    // evita que un ';' o un salto de línea en un texto rompa el formato
    private String limpiar(String s) {
        if (s == null) return "";
        return s.replace(SEP, ",").replace("\r", " ").replace("\n", " ");
    }
    
    
    //-----------------------------------------------

}
