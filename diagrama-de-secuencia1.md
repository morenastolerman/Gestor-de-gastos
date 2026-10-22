```mermaid
sequenceDiagram
    actor Usuario
    participant Controller
    participant ControlGastos
    participant RepositorioArchivo
    participant Limite

    Usuario->>Controller: ingresarDatosConsumo(monto, fecha, ...)
    activate Controller

    Controller->>Controller: validarDatos(monto, fecha)
    activate Controller
    deactivate Controller

    alt Datos Inválidos
        Controller-->>Usuario: Mostrar error ("Datos incorrectos o incompletos")
    else Datos Válidos
        create participant C as c: Consumo
        Controller-->>C: <<create>> new Consumo(monto, fecha, ...)

        Controller->>ControlGastos: registrar(c)
        activate ControlGastos

        ControlGastos->>RepositorioArchivo: guardar(c)
        activate RepositorioArchivo
        deactivate RepositorioArchivo

        loop Para cada Límite
            ControlGastos->>Limite: estado(gastado)
            activate Limite
            Limite-->>ControlGastos: Semaforo
            deactivate Limite

            alt Semaforo == ROJO
                ControlGastos->>ControlGastos: notificar("Límite excedido")
                activate ControlGastos
                deactivate ControlGastos
                ControlGastos->>Controller: actualizar("Límite excedido (ROJO)")
                activate Controller
                Controller-->>Usuario: Mostrar alerta (Rojo)
                deactivate Controller
            else Semaforo == AMARILLO
                ControlGastos->>ControlGastos: notificar("Advertencia de límite")
                activate ControlGastos
                deactivate ControlGastos
                ControlGastos->>Controller: actualizar("Advertencia (AMARILLO)")
                activate Controller
                Controller-->>Usuario: Mostrar advertencia (Amarillo)
                deactivate Controller
            else Semaforo == VERDE
                ControlGastos->>ControlGastos: notificar("Estado normal")
                activate ControlGastos
                deactivate ControlGastos
                ControlGastos->>Controller: actualizar("Estado OK (VERDE)")
                activate Controller
                deactivate Controller
            end
        end

        ControlGastos-->>Controller: true
        deactivate ControlGastos
        Controller-->>Usuario: Confirmación de registro
    end
    deactivate Controller
'''
