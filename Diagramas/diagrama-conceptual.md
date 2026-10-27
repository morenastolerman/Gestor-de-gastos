```mermaid
classDiagram
    direction LR

    class Consumo {
        monto
        fecha
        descripcion
        cuotas
    }

    class Categoria {
        nombre
    }

    class MedioPago {
        nombre
    }

    class Limite {
        <<abstract>>
        monto
        fechaReferencia
    }

    class LimiteMensual

    class Semaforo {
        <<enumeration>>
        ROJO
        AMARILLO
        VERDE
    }

    class Reporte {
        <<abstract>>
    }

    class ReporteEstadistico
    class ReporteGastosPeriodo

    Consumo "*" -- "1" Categoria : pertenece a ▶
    Consumo "*" -- "1" MedioPago : se paga con ▶
    Limite "1" -- "*" Consumo : limita el gasto de ▶
    Limite "1" -- "1" Semaforo : indica estado de ejecución ▶
    Reporte "*" -- "*" Consumo : resume ▶

    Limite <|-- LimiteMensual
    Reporte <|-- ReporteEstadistico
    Reporte <|-- ReporteGastosPeriodo
    ```