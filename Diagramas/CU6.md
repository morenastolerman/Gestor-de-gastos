# CU6 – Consultar saldo y estado

Muestra el límite del mes, lo gastado, el porcentaje consumido, el saldo disponible y el estado con un semáforo de colores (VERDE: menos del 80 %, AMARILLO: desde el 80 %, ROJO: desde el 95 %). Si está en AMARILLO o ROJO agrega la alerta del CU12 («extend»).

```mermaid
sequenceDiagram
    actor Usuario
    participant V as Vista
    participant C as Controller
    participant CG as ControlGasto
    participant R as Repositorio
    participant L as l : Limite

    Usuario->>+V: ingresa el mes y toca "Consultar"
    V->>V: leerMes(mes)
    V->>+C: consultarSaldo(mes)
    C->>+CG: consultarSaldo(mes.atDay(1))

    CG->>CG: buscarLimite(fecha)
    loop por cada límite l
        CG->>+L: incluye(fecha)
        Note over L: Lo resuelve la subclase. LimiteMensual mira mes y año (polimorfismo)
        L-->>-CG: true o false
    end

    alt no hay límite para ese mes
        CG-->>C: IllegalArgumentException("No hay un límite definido para ese período")
        C->>C: mensajes.add("No hay un límite definido para ese período")
    else hay límite
        CG->>CG: calcularGastado(l)
        CG->>+R: listar()
        R-->>-CG: consumos
        loop por cada consumo c
            CG->>+L: aplica(c)
            L-->>-CG: true o false
        end
        Note over CG: gastado = suma de los montos de los consumos que aplican

        CG->>+L: saldo(gastado)
        L-->>-CG: saldo
        CG->>+L: estado(gastado)
        L-->>-CG: Semaforo (VERDE, AMARILLO o ROJO)
        CG->>+L: porcentajeConsumido(gastado)
        L-->>-CG: porcentaje

        CG->>CG: alertaDe(l, gastado)
        opt Semaforo es AMARILLO o ROJO («extend» CU12)
            CG->>CG: agrega la alerta al resultado
        end
        CG-->>-C: límite, gastado y %, saldo, "Estado: ..." y alerta
        C->>C: mensajes.addAll(resultado)
    end

    C-->>-V: mensajes
    V->>V: muestra los mensajes y pintarSemaforo(semaforoDe(mensajes))
    V-->>-Usuario: ve el saldo, el porcentaje y el semáforo de color
```
