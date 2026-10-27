package Codigo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

// Vista gráfica (Swing). Igual que la Vista de consola, solo conoce al Controller:
// lee datos, los manda y muestra los mensajes que devuelve.
public class VistaGrafica {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter MES = DateTimeFormatter.ofPattern("MM/yyyy");
    // Opción "todas" del filtro de categoría (id 0, igual que en la vista de consola)
    private static final Categoria TODAS = new Categoria(0, "Todas");

    private final Controller controller;
    private final JFrame ventana = new JFrame("Control de Gastos");
    private final JTabbedPane pestanas = new JTabbedPane();

    private final JComboBox<Categoria> cmbCategoria = new JComboBox<>();
    private final JComboBox<MedioPago> cmbMedio = new JComboBox<>();
    private final JComboBox<Categoria> cmbFiltroCategoria = new JComboBox<>();
    private final List<Runnable> recargas = new ArrayList<>();

    public VistaGrafica(Controller controller) {
        this.controller = controller;
    }

    // ===================== VENTANA =====================

    public JFrame mostrar() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // si falla, queda el aspecto por defecto
        }
        configurarCombo(cmbCategoria);
        configurarCombo(cmbMedio);
        configurarCombo(cmbFiltroCategoria);

        pestanas.addTab("Registrar consumo", pestanaRegistrar());
        pestanas.addTab("Límite mensual", pestanaLimite());
        pestanas.addTab("Categorías", panelMaestro("Categorías", controller::obtenerCategorias,
                Categoria::getId, Categoria::getNombre, controller::agregarCategoria,
                controller::modificarCategoria, controller::eliminarCategoria));
        pestanas.addTab("Medios de pago", panelMaestro("Medios de pago", controller::obtenerMediosPago,
                MedioPago::getId, MedioPago::getNombre, controller::agregarMedioPago,
                controller::modificarMedioPago, controller::eliminarMedioPago));
        pestanas.addTab("Reporte estadístico", pestanaReporteEstadistico());
        pestanas.addTab("Gastos del período", pestanaGastosPeriodo());

        // al cambiar de pestaña se actualizan listas y combos (por si cambiaron categorías o medios)
        pestanas.addChangeListener(e -> actualizarListas());
        actualizarListas();

        ventana.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ventana.add(pestanas);
        ventana.setSize(820, 560);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
        return ventana;
    }

    // ===================== PESTAÑAS =====================

    // CU1: Registrar consumo y notificar
    private JComponent pestanaRegistrar() {
        JTextField txtMonto = new JTextField(12);
        JTextField txtFecha = new JTextField(LocalDate.now().format(FECHA), 12);
        JTextField txtDescripcion = new JTextField(24);
        JSpinner spCuotas = new JSpinner(new SpinnerNumberModel(1, 1, 120, 1));

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.addActionListener(e -> ejecutar(() -> {
            double monto = leerDecimal(txtMonto, "el monto");
            LocalDate fecha = leerFecha(txtFecha, "la fecha", false);
            int cuotas = (Integer) spCuotas.getValue();
            Categoria categoria = (Categoria) cmbCategoria.getSelectedItem();
            MedioPago medio = (MedioPago) cmbMedio.getSelectedItem();

            List<String> mensajes = controller.ingresarDatosConsumo(
                    monto, fecha, txtDescripcion.getText(), cuotas, categoria, medio);
            mostrar(mensajes);
            if (!mensajes.isEmpty() && mensajes.get(0).contains("correctamente")) {
                txtMonto.setText("");
                txtDescripcion.setText("");
                spCuotas.setValue(1);
            }
        }));

        JPanel form = formulario(
                new String[] {"Monto", "Fecha (dd/MM/yyyy)", "Descripción", "Cuotas", "Categoría", "Medio de pago"},
                new JComponent[] {txtMonto, txtFecha, txtDescripcion, spCuotas, cmbCategoria, cmbMedio});
        return conBoton(form, btnRegistrar);
    }

    // CU5: Definir límite mensual
    private JComponent pestanaLimite() {
        JTextField txtMonto = new JTextField(12);
        JTextField txtMes = new JTextField(YearMonth.now().format(MES), 12);

        JButton btnDefinir = new JButton("Definir límite");
        btnDefinir.addActionListener(e -> ejecutar(() -> {
            double monto = leerDecimal(txtMonto, "el monto del límite");
            YearMonth mes = leerMes(txtMes, "el mes");
            mostrar(controller.definirLimite(monto, mes));
        }));

        JPanel form = formulario(new String[] {"Monto del límite", "Mes (MM/yyyy)"},
                new JComponent[] {txtMonto, txtMes});
        JPanel nota = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nota.add(new JLabel("Hay un solo límite por mes. Se avisa al registrar un consumo si el gasto se acerca o supera el límite."));
        JPanel centro = new JPanel(new BorderLayout());
        centro.add(form, BorderLayout.NORTH);
        centro.add(nota, BorderLayout.CENTER);
        return conBoton(centro, btnDefinir);
    }

    // CU7 y CU8: mismo panel para categorías y para medios de pago
    private <T> JComponent panelMaestro(String titulo, Supplier<List<T>> listar, ToIntFunction<T> idDe,
            Function<T, String> nombreDe, Function<String, List<String>> agregar,
            BiFunction<Integer, String, List<String>> modificar, Function<Integer, List<String>> eliminar) {
        DefaultListModel<T> modelo = new DefaultListModel<>();
        JList<T> lista = new JList<>(modelo);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JTextField txtNombre = new JTextField(20);

        Runnable recargar = () -> {
            modelo.clear();
            for (T elemento : listar.get()) modelo.addElement(elemento);
        };
        recargas.add(recargar);

        // al elegir un elemento se copia su nombre al campo, para poder editarlo
        lista.addListSelectionListener(e -> {
            T elegido = lista.getSelectedValue();
            if (!e.getValueIsAdjusting() && elegido != null) txtNombre.setText(nombreDe.apply(elegido));
        });

        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(e -> ejecutar(() -> {
            mostrar(agregar.apply(txtNombre.getText()));
            txtNombre.setText("");
            actualizarListas();
        }));

        JButton btnModificar = new JButton("Modificar");
        btnModificar.addActionListener(e -> ejecutar(() -> {
            T elegido = lista.getSelectedValue();
            if (elegido == null) {
                aviso("Elegí un elemento de la lista");
                return;
            }
            mostrar(modificar.apply(idDe.applyAsInt(elegido), txtNombre.getText()));
            actualizarListas();
        }));

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> ejecutar(() -> {
            T elegido = lista.getSelectedValue();
            if (elegido == null) {
                aviso("Elegí un elemento de la lista");
                return;
            }
            int respuesta = JOptionPane.showConfirmDialog(ventana,
                    "¿Eliminar \"" + nombreDe.apply(elegido) + "\"?", titulo, JOptionPane.YES_NO_OPTION);
            if (respuesta != JOptionPane.YES_OPTION) return;
            mostrar(eliminar.apply(idDe.applyAsInt(elegido)));
            actualizarListas();
        }));

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulario.add(new JLabel("Nombre"));
        formulario.add(txtNombre);
        formulario.add(btnAgregar);
        formulario.add(btnModificar);
        formulario.add(btnEliminar);

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.add(new JLabel(titulo + " (elegí una para modificarla o eliminarla)"), BorderLayout.NORTH);
        panel.add(new JScrollPane(lista), BorderLayout.CENTER);
        panel.add(formulario, BorderLayout.SOUTH);
        return panel;
    }

    // CU9: Reporte estadístico
    private JComponent pestanaReporteEstadistico() {
        JTextField txtDesde = new JTextField(LocalDate.now().withDayOfMonth(1).format(FECHA), 12);
        JTextField txtHasta = new JTextField(LocalDate.now().format(FECHA), 12);
        JTextArea resultado = areaResultado();

        JButton btnGenerar = new JButton("Generar reporte");
        btnGenerar.addActionListener(e -> ejecutar(() -> {
            LocalDate desde = leerFecha(txtDesde, "desde", false);
            LocalDate hasta = leerFecha(txtHasta, "hasta", false);
            resultado.setText(controller.generarReporteEstadistico(desde, hasta));
            resultado.setCaretPosition(0);
        }));

        JPanel form = formulario(new String[] {"Desde (dd/MM/yyyy)", "Hasta (dd/MM/yyyy)"},
                new JComponent[] {txtDesde, txtHasta});
        return conResultado(form, btnGenerar, resultado);
    }

    // CU10: Gastos del período (consulta con filtros y reportes)
    private JComponent pestanaGastosPeriodo() {
        JTextField txtDesde = new JTextField(12);
        JTextField txtHasta = new JTextField(12);
        JTextField txtMes = new JTextField(YearMonth.now().format(MES), 12);
        JTextArea resultado = areaResultado();

        JButton btnConsultar = new JButton("Consultar consumos");
        btnConsultar.addActionListener(e -> ejecutar(() -> {
            LocalDate desde = leerFecha(txtDesde, "desde", true);
            LocalDate hasta = leerFecha(txtHasta, "hasta", true);
            Categoria elegida = (Categoria) cmbFiltroCategoria.getSelectedItem();
            Categoria categoria = (elegida == null || elegida.getId() == 0) ? null : elegida;
            resultado.setText(String.join("\n", controller.consultarConsumos(desde, hasta, categoria)));
            resultado.setCaretPosition(0);
        }));

        JButton btnRango = new JButton("Reporte por rango");
        btnRango.addActionListener(e -> ejecutar(() -> {
            LocalDate desde = leerFecha(txtDesde, "desde", false);
            LocalDate hasta = leerFecha(txtHasta, "hasta", false);
            resultado.setText(controller.generarReporteGastos(desde, hasta));
            resultado.setCaretPosition(0);
        }));

        JButton btnMes = new JButton("Reporte del mes");
        btnMes.addActionListener(e -> ejecutar(() -> {
            YearMonth mes = leerMes(txtMes, "el mes");
            resultado.setText(controller.generarReporteGastosMensual(mes));
            resultado.setCaretPosition(0);
        }));

        JPanel form = formulario(
                new String[] {"Desde (dd/MM/yyyy, opcional)", "Hasta (dd/MM/yyyy, opcional)", "Categoría", "Mes (MM/yyyy)"},
                new JComponent[] {txtDesde, txtHasta, cmbFiltroCategoria, txtMes});
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnConsultar);
        botones.add(btnRango);
        botones.add(btnMes);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(form, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.add(norte, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultado), BorderLayout.CENTER);
        return panel;
    }

    // ===================== AUXILIARES =====================

    // vuelve a cargar listas y combos con lo que hay en el Controller
    private void actualizarListas() {
        cmbCategoria.removeAllItems();
        cmbFiltroCategoria.removeAllItems();
        cmbFiltroCategoria.addItem(TODAS);
        for (Categoria c : controller.obtenerCategorias()) {
            cmbCategoria.addItem(c);
            cmbFiltroCategoria.addItem(c);
        }
        cmbMedio.removeAllItems();
        for (MedioPago m : controller.obtenerMediosPago()) cmbMedio.addItem(m);
        for (Runnable recarga : recargas) recarga.run();
    }

    // en los combos se muestra solo el nombre
    private void configurarCombo(JComboBox<?> combo) {
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                    boolean seleccionado, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, seleccionado, foco);
                if (valor instanceof Categoria) setText(((Categoria) valor).getNombre());
                if (valor instanceof MedioPago) setText(((MedioPago) valor).getNombre());
                return this;
            }
        });
    }

    // etiquetas a la izquierda y campos a la derecha
    private JPanel formulario(String[] etiquetas, JComponent[] campos) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        for (int i = 0; i < etiquetas.length; i++) {
            g.gridx = 0;
            g.gridy = i;
            g.weightx = 0;
            g.fill = GridBagConstraints.NONE;
            panel.add(new JLabel(etiquetas[i]), g);
            g.gridx = 1;
            g.weightx = 1;
            g.fill = GridBagConstraints.HORIZONTAL;
            panel.add(campos[i], g);
        }
        return panel;
    }

    private JPanel conBoton(JComponent contenido, JButton boton) {
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(boton);
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.add(contenido, BorderLayout.NORTH);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel conResultado(JComponent formulario, JButton boton, JTextArea resultado) {
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(boton);
        JPanel norte = new JPanel(new BorderLayout());
        norte.add(formulario, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.add(norte, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultado), BorderLayout.CENTER);
        return panel;
    }

    private JTextArea areaResultado() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        return area;
    }

    // muestra los mensajes del Controller en un diálogo: alerta, información o error
    private void mostrar(List<String> mensajes) {
        boolean alerta = false;
        for (String m : mensajes) {
            if (m.startsWith("ALERTA") || m.startsWith("ADVERTENCIA")) alerta = true;
        }
        boolean correcto = !mensajes.isEmpty() && mensajes.get(0).contains("correctamente");
        int tipo = alerta ? JOptionPane.WARNING_MESSAGE
                : correcto ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE;
        JOptionPane.showMessageDialog(ventana, String.join("\n", mensajes), "Control de Gastos", tipo);
    }

    private void aviso(String texto) {
        JOptionPane.showMessageDialog(ventana, texto, "Control de Gastos", JOptionPane.INFORMATION_MESSAGE);
    }

    // si algo falla (dato mal escrito o error de archivo) se muestra un mensaje y la ventana sigue
    private void ejecutar(Runnable accion) {
        try {
            accion.run();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(ventana, e.getMessage(), "No se pudo completar",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private double leerDecimal(JTextField campo, String nombre) {
        try {
            return Double.parseDouble(campo.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ingresá un número válido para " + nombre);
        }
    }

    // si opcional es true, un campo vacío devuelve null
    private LocalDate leerFecha(JTextField campo, String nombre, boolean opcional) {
        String texto = campo.getText().trim();
        if (texto.isEmpty() && opcional) return null;
        try {
            return LocalDate.parse(texto, FECHA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Fecha inválida en " + nombre + ", usá dd/MM/yyyy");
        }
    }

    private YearMonth leerMes(JTextField campo, String nombre) {
        try {
            return YearMonth.parse(campo.getText().trim(), MES);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Mes inválido en " + nombre + ", usá MM/yyyy");
        }
    }
}
