``` mermaid
sequenceDiagram
    actor Usuario
    participant V as Vista
    participant C as Controller
    participant CG as ControlGasto
    participant LM as LimiteMensual

    Usuario->>+V: clicDefinirLimite(monto, mes, anio, umbral)
    V->>+C: agregarLimite(monto, mes, anio, umbral)

    C->>C: validarDatos(monto, mes, anio, umbral)

    alt datos invalidos
        C->>C: armar lista de errores
    else datos validos
        C->>C: calcular fechaInicio (primer dia del mes)
        C->>+CG: definirOModificarLimite(monto, fechaInicio, umbral)

        CG->>CG: buscarLimite(fechaInicio)

        alt ya existe limite mensual para ese mes (modificar)
            CG->>+LM: actualizar(monto, umbral)
            LM-->>-CG: limite actualizado
        else no existe limite para ese mes (definir)
            CG->>CG: new LimiteMensual(monto, fechaInicio, umbral)
            CG->>CG: agregar limite a la lista de limites
        end

        CG-->>-C: listo
    end
```
    C-->>-V: lista de errores (vacia si salio bien)
    V->>V: mostrarMensaje(mensajes)
    V-->>-Usuario: muestra el resultado
