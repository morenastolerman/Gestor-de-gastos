``` mermaid
sequenceDiagram
    actor Usuario
    participant V as Vista
    participant C as Controller
    participant CG as ControlGasto
    participant R as Repositorio
    participant LM as LimiteMensual

    Usuario->>+V: ConsultarSaldo(mes, anio)
    V->>+C: consultarSaldoYEstado(mes, anio)

    C->>C: calcular fechaInicio (primer dia del mes)
    C->>+CG: consultarEstadoLimite(fechaInicio)

    CG->>CG: buscarLimite(fechaInicio)

    alt no existe limite para ese mes
        CG->>CG: resultado = sin limite definido
    else existe limite para ese mes
        CG->>CG: calcularGastado(limite)
        CG->>+R: listar()
        R-->>-CG: lista de Consumo

        loop por cada Consumo c
            CG->>+LM: aplica(c)
            LM-->>-CG: boolean
            opt aplica es true
                CG->>CG: sumar monto de c al gastado
            end
        end

        CG->>+LM: saldo(gastado)
        LM-->>-CG: saldo disponible
        CG->>+LM: estado(gastado)
        LM-->>-CG: Semaforo (VERDE, AMARILLO o ROJO)
        CG->>CG: armar resultado (gastado, saldo, estado)
    end

    CG-->>-C: resultado
    C-->>-V: resultado


    V->>V: mostrarLimites(resultado)
    V-->>-Usuario: muestra saldo y estado
```
