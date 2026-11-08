# Diagrama de vista y controlador

```mermaid
flowchart LR
    subgraph VISTAS["Vistas"]
        VG["VistaGrafica<br/>9 pestañas"]
        VC["Vista<br/>menú de consola"]
    end

    subgraph CTRL["Controller"]
        K11["CU11: cargarConfiguracion"]
        K1["CU1: ingresarDatosConsumo<br/>incluye alertas CU12"]
        K2["CU2: listarConsumos<br/>modificarConsumo<br/>incluye alertas CU12"]
        K3["CU3: eliminarConsumo"]
        K4["CU4: consultarConsumos"]
        K5["CU5: definirLimite<br/>modificarLimite<br/>obtenerLimites"]
        K6["CU6: consultarSaldo<br/>incluye alertas CU12"]
        K7["CU7: obtenerCategorias<br/>agregarCategoria<br/>modificarCategoria<br/>eliminarCategoria"]
        K8["CU8: obtenerMediosPago<br/>agregarMedioPago<br/>modificarMedioPago<br/>eliminarMedioPago"]
        K9["CU9: generarReporteEstadistico"]
        K10["CU10: generarReporteGastos<br/>generarReporteGastosMensual"]
    end

    MOD["ControlGasto<br/>modelo"]

    VG -->|"datos ya leídos y convertidos:<br/>double, LocalDate, YearMonth, Categoria, MedioPago, id, texto"| CTRL
    VC -->|"lo mismo, leído por consola"| CTRL
    CTRL -->|"List String mensajes, List Categoria, List MedioPago,<br/>List Consumo, List Limite, String reporte"| VG
    CTRL -->|"los mismos resultados"| VC
    CTRL -->|"delega"| MOD
    MOD -->|"resultado o excepción"| CTRL
```
