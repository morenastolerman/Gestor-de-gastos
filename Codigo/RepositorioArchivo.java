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
    private final Path archivo;            // consumos
    private final Path archivoMedios;      // medios de pago
    private final Path archivoCategorias;  // categorías

    public RepositorioArchivo(String rutaConsumos, String rutaMedios, String rutaCategorias) {
        this.archivo = Paths.get(rutaConsumos);
        this.archivoMedios = Paths.get(rutaMedios);
        this.archivoCategorias = Paths.get(rutaCategorias);
    }

    public RepositorioArchivo() {
        this("consumos.csv", "medios.csv", "categorias.csv");
    }

    // ===================== CU1: Registrar consumo y notificar =====================
    // (listar() también lo usan CU7, CU8, CU9 y CU10)

    @Override
    public void guardar(Consumo c) {
        c.setId(siguienteId());
        agregarLinea(archivo, aLinea(c), "el consumo");
    }

    @Override
    public List<Consumo> listar() {
        List<Consumo> consumos = new ArrayList<>();
        if (!Files.exists(archivo)) return consumos;

        // Se cargan una sola vez para mostrar siempre los nombres actualizados
        Map<Integer, MedioPago> medios = mediosPorId();
        Map<Integer, Categoria> categorias = categoriasPorId();
        try {
            for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
                if (linea.isBlank()) continue;
                consumos.add(deLinea(linea, medios, categorias));
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
    private Consumo deLinea(String linea, Map<Integer, MedioPago> medios, Map<Integer, Categoria> categorias) {
        String[] p = linea.split(SEP, -1);
        int idCategoria = Integer.parseInt(p[5]);
        int idMedio = Integer.parseInt(p[7]);
        // si existe en su archivo se usa ese (nombre actual); si no, el guardado en la línea
        Categoria categoria = categorias.getOrDefault(idCategoria, new Categoria(idCategoria, p[6]));
        MedioPago medio = medios.getOrDefault(idMedio, new MedioPago(idMedio, p[8]));
        return new Consumo(Integer.parseInt(p[0]), Double.parseDouble(p[1]), LocalDate.parse(p[2]),
                p[3], Integer.parseInt(p[4]), categoria, medio);
    }

    // ===================== CU7: Gestionar categorías =====================

    @Override
    public void guardarCategoria(Categoria c) {
        c.setId(siguienteIdCategoria());
        agregarLinea(archivoCategorias, categoriaALinea(c), "la categoría");
    }

    @Override
    public List<Categoria> listarCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        for (String linea : leerLineas(archivoCategorias)) {
            String[] p = linea.split(SEP, -1);
            categorias.add(new Categoria(Integer.parseInt(p[0]), p[1]));
        }
        return categorias;
    }

    @Override
    public void actualizarCategoria(Categoria c) {
        List<Categoria> categorias = listarCategorias();
        for (int i = 0; i < categorias.size(); i++) {
            if (categorias.get(i).getId() == c.getId()) {
                categorias.set(i, c);
                break;
            }
        }
        reescribirCategorias(categorias);
    }

    @Override
    public void eliminarCategoria(int id) {
        List<Categoria> categorias = listarCategorias();
        categorias.removeIf(c -> c.getId() == id);
        reescribirCategorias(categorias);
    }

    private int siguienteIdCategoria() {
        int max = 0;
        for (Categoria c : listarCategorias()) {
            if (c.getId() > max) max = c.getId();
        }
        return max + 1;
    }

    private Map<Integer, Categoria> categoriasPorId() {
        Map<Integer, Categoria> mapa = new HashMap<>();
        for (Categoria c : listarCategorias()) mapa.put(c.getId(), c);
        return mapa;
    }

    private String categoriaALinea(Categoria c) {
        return String.join(SEP, String.valueOf(c.getId()), limpiar(c.getNombre()));
    }

    private void reescribirCategorias(List<Categoria> categorias) {
        List<String> lineas = new ArrayList<>();
        for (Categoria c : categorias) lineas.add(categoriaALinea(c));
        escribirLineas(archivoCategorias, lineas);
    }

    // ===================== CU8: Gestionar medios de pago =====================

    @Override
    public void guardarMedioPago(MedioPago m) {
        m.setId(siguienteIdMedio());
        agregarLinea(archivoMedios, medioALinea(m), "el medio de pago");
    }

    @Override
    public List<MedioPago> listarMediosPago() {
        List<MedioPago> medios = new ArrayList<>();
        for (String linea : leerLineas(archivoMedios)) {
            String[] p = linea.split(SEP, -1);
            medios.add(new MedioPago(Integer.parseInt(p[0]), p[1]));
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
        escribirLineas(archivoMedios, lineas);
    }

    // ===================== AUXILIARES (usados por todos los CU) =====================

    private List<String> leerLineas(Path ruta) {
        List<String> lineas = new ArrayList<>();
        if (!Files.exists(ruta)) return lineas;
        try {
            for (String linea : Files.readAllLines(ruta, StandardCharsets.UTF_8)) {
                if (!linea.isBlank()) lineas.add(linea);
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer " + ruta, e);
        }
        return lineas;
    }

    private void agregarLinea(Path ruta, String linea, String que) {
        try (BufferedWriter w = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            w.write(linea);
            w.newLine();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar " + que + " en " + ruta, e);
        }
    }

    private void escribirLineas(Path ruta, List<String> lineas) {
        try {
            Files.write(ruta, lineas, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo escribir " + ruta, e);
        }
    }

    // evita que un ';' o un salto de línea en un texto rompa el formato
    private String limpiar(String s) {
        if (s == null) return "";
        return s.replace(SEP, ",").replace("\r", " ").replace("\n", " ");
    }
}