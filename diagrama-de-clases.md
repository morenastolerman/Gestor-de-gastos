classDiagram
    direction TB
    class Observador {
        <<interface>>
        +actualizar(msg: String)
    }
    class GUI {
        +iniciar()
        +ingresarDatosConsumo(monto: double, fecha: Date, descripcion: String, cuotas: int, categoria: Categoria, medioPago: MedioPago)
        -validarDatos(monto: double, fecha: Date) boolean
        +actualizar(msg: String)
    }
    class Criterio {
        <<interface>>
        +cumple(c: Consumo) boolean
    }
    class CriterioCategoria {
        -categoria: Categoria
        +cumple(c: Consumo) boolean
    }
    class CriterioRangoFechas {
        -desde: Date
        -hasta: Date
        +cumple(c: Consumo) boolean
    }
    class Reporte {
        <<interface>>
        +generar(consumos: List~Consumo~, desde: Date, hasta: Date) String
    }
    class ReporteEstadistico {
        +generar(consumos: List~Consumo~, desde: Date, hasta: Date) String
    }
    class ReporteGastosPeriodo {
        +generar(consumos: List~Consumo~, desde: Date, hasta: Date) String
    }
    class Repositorio {
        <<interface>>
        +guardar(c: Consumo)
        +cargar() List~Consumo~
        +importar(archivo: String) List~Consumo~
    }
    class RepositorioArchivo {
        -ruta: String
        +guardar(c: Consumo)
        +cargar() List~Consumo~
        +importar(archivo: String) List~Consumo~
    }
    class Semaforo {
        <<enumeration>>
        VERDE
        AMARILLO
        ROJO
    }
    class Periodo {
        <<enumeration>>
        SEMANAL
        MENSUAL
    }
    class ControlGastos {
        -instancia: ControlGastos$
        -consumos: List~Consumo~
        -limites: List~Limite~
        -categorias: List~Categoria~
        -medios: List~MedioPago~
        -obs: List~Observador~
        -repositorio: Repositorio
        +getInstancia()$ ControlGastos
        +registrar(c: Consumo) boolean
        +modificar(c: Consumo) boolean
        +eliminar(id: int) boolean
        +filtrar(c: Criterio) List~Consumo~
        +definirLimite(l: Limite)
        +consultarEstado(l: Limite) Semaforo
        +suscribir(o: Observador)
        +desuscribir(o: Observador)
        +notificar(msg: String)
        +reporte(r: Reporte, desde: Date, hasta: Date) String
        +getCategorias() List~Categoria~
        +getMedios() List~MedioPago~
        -calcularGastado(l: Limite) double
    }
    class Consumo {
        -id: int
        -monto: double
        -fecha: Date
        -descripcion: String
        -cuotas: int
        -categoria: Categoria
        -medioPago: MedioPago
        +montoCuota() double
        +Consumo(id: int, monto: double, fecha: Date, descripcion: String, cuotas: int, categoria: Categoria, medioPago: MedioPago)
        +montoCuota() double
    }
    class Limite {
        -monto: double
        -periodo: Periodo
        -fechaInicio: Date
        -umbral: double
        -categoria: Categoria
        +saldo(gastado: double) double
        +estado(gastado: double) Semaforo
    }
    class Categoria {
        -id: int
        -nombre: String
    }
    class MedioPago {
        -id: int
        -nombre: String
    }
    GUI ..|> Observador
    GUI ..> ControlGastos : usa
    CriterioCategoria ..|> Criterio
    CriterioRangoFechas ..|> Criterio
    ReporteEstadistico ..|> Reporte
    ReporteGastosPeriodo ..|> Reporte
    RepositorioArchivo ..|> Repositorio
    ControlGastos "1" o-- "*" Consumo
    ControlGastos "1" o-- "*" Limite
    ControlGastos "1" o-- "*" Categoria
    ControlGastos "1" o-- "*" MedioPago
    ControlGastos "1" --> "*" Observador : notifica
    ControlGastos --> Repositorio : utiliza
    ControlGastos ..> Criterio : aplica
    ControlGastos ..> Reporte : ejecuta
    ControlGastos ..> Semaforo
    Consumo "*" --> "1" Categoria
    Consumo "*" --> "1" MedioPago
    Limite "*" --> "0..1" Categoria
    Limite ..> Semaforo
    Limite -- Periodo
    GUI ..> Consumo : crea
    GUI ..> Categoria
    GUI ..> MedioPago
