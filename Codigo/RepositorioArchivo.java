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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepositorioArchivo implements Repositorio {
    private static final String SEP = ";";
    private final Path archivo;        // consumos
    private final Path archivoMedios;  // medios de pago

    public RepositorioArchivo(String rutaConsumos, String rutaMedios) {
        this.archivo = Paths.get(rutaConsumos);
        this.archivoMedios = Paths.get(rutaMedios);
    }

    public RepositorioArchivo() {
        this("consumos.csv", "medios.csv");
    }

    // ===================== CONSUMOS =====================

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

        // Se cargan los medios una sola vez para mostrar siempre el nombre actualizado
        Map<Integer, MedioPago> medios = mediosPorId();
        try {
            for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
                if (linea.isBlank()) continue;
                consumos.add(deLinea(linea, medios));
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
    private Consumo deLinea(String linea, Map<Integer, MedioPago> medios) {
        String[] p = linea.split(SEP, -1);
        Categoria categoria = new Categoria(Integer.parseInt(p[5]), p[6]);
        int idMedio = Integer.parseInt(p[7]);
        // si el medio existe en medios.csv se usa ese (nombre actual); si no, el guardado en la línea
        MedioPago medio = medios.getOrDefault(idMedio, new MedioPago(idMedio, p[8]));
        return new Consumo(Integer.parseInt(p[0]), Double.parseDouble(p[1]), LocalDate.parse(p[2]),
                p[3], Integer.parseInt(p[4]), categoria, medio);
    }

    // ===================== MEDIOS DE PAGO =====================

    @Override
    public void guardarMedioPago(MedioPago m) {
        m.setId(siguienteIdMedio());
        try (BufferedWriter w = Files.newBufferedWriter(archivoMedios, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            w.write(medioALinea(m));
            w.newLine();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el medio de pago en " + archivoMedios, e);
        }
    }

    @Override
    public List<MedioPago> listarMediosPago() {
        List<MedioPago> medios = new ArrayList<>();
        if (!Files.exists(archivoMedios)) return medios;

        try {
            for (String linea : Files.readAllLines(archivoMedios, StandardCharsets.UTF_8)) {
                if (linea.isBlank()) continue;
                String[] p = linea.split(SEP, -1);
                medios.add(new MedioPago(Integer.parseInt(p[0]), p[1]));
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer " + archivoMedios, e);
        }
        return medios;
    }

    @Override
    public void actualizarMedioPago(MedioPago m) {
        List<MedioPago> medios = listarMediosPago();
        for (int i = 0; i < medios.size(); i++) {
            if (medios.get(i).getId() == m.getId()) {
                medios.set(i, m);
                break;
            }
        }
        reescribirMedios(medios);
    }

    @Override
    public void eliminarMedioPago(int id) {
        List<MedioPago> medios = listarMediosPago();
        medios.removeIf(m -> m.getId() == id);
        reescribirMedios(medios);
    }

    private int siguienteIdMedio() {
        int max = 0;
        for (MedioPago m : listarMediosPago()) {
            if (m.getId() > max) max = m.getId();
        }
        return max + 1;
    }

    private Map<Integer, MedioPago> mediosPorId() {
        Map<Integer, MedioPago> mapa = new HashMap<>();
        for (MedioPago m : listarMediosPago()) mapa.put(m.getId(), m);
        return mapa;
    }

    private String medioALinea(MedioPago m) {
        return String.join(SEP, String.valueOf(m.getId()), limpiar(m.getNombre()));
    }

    private void reescribirMedios(List<MedioPago> medios) {
        List<String> lineas = new ArrayList<>();
        for (MedioPago m : medios) lineas.add(medioALinea(m));
        try {
            Files.write(archivoMedios, lineas, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo escribir " + archivoMedios, e);
        }
    }

    // evita que un ';' o un salto de línea en un texto rompa el formato
    private String limpiar(String s) {
        if (s == null) return "";
        return s.replace(SEP, ",").replace("\r", " ").replace("\n", " ");
    }
}