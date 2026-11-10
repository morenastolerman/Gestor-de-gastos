# CU5 – Definir y modificar límite

Hay un solo límite por mes. El diagrama tiene las dos operaciones del caso de uso: definir un límite nuevo y modificar el de un mes que ya tiene límite. En los dos casos el límite queda guardado en `limites.csv` para recuperarlo al iniciar (CU11). `Limite` es abstracta: `ControlGasto` trabaja con ella y cada subclase (hoy `LimiteMensual`) resuelve `mismoPeriodo` a su manera.

```mermaid
sequenceDiagram
    actor Usuario
    participant V as Vista
    participant C as Controller
    participant CG as ControlGasto
    participant E as existente : Limite
    participant R as Repositorio

    Usuario->>+V: ingresa monto y mes y toca "Definir límite" o "Modificar límite"
    V->>V: leerDecimal(monto) y leerMes(mes)

    alt Definir límite
        V->>+C: definirLimite(monto, mes)
        create participant L as l : LimiteMensual
        C->>L: new LimiteMensual(monto, mes.atDay(1))
        alt monto <= 0
            L-->>C: IllegalArgumentException("El monto debe ser mayor a 0")
        else monto válido
            C->>+CG: agregarLimite(l)
            loop para cada límite existente
                CG->>+E: mismoPeriodo(l)
                E-->>-CG: true o false
            end
            alt ya existe un límite para ese mes
                CG-->>C: IllegalStateException("Ya existe un límite para ese período")
            else no existe
                CG->>+R: guardarLimite(l)
                Note over R: lo agrega a limites.csv (así se recupera al iniciar, CU11)
                R-->>-CG: listo
                CG->>CG: limites.add(l)
                CG-->>C: listo
            end
            deactivate CG
        end
        C->>C: mensajes.add("Límite agregado correctamente") o el error
        C-->>-V: mensajes
    else Modificar límite
        V->>+C: modificarLimite(monto, mes)
        C->>L: new LimiteMensual(monto, mes.atDay(1))
        alt monto <= 0
            L-->>C: IllegalArgumentException("El monto debe ser mayor a 0")
        else monto válido
            C->>+CG: modificarLimite(l)
            loop para cada límite existente (posición i)
                CG->>+E: mismoPeriodo(l)
                E-->>-CG: true o false
                break es del mismo mes
                    CG->>+R: actualizarLimite(l)
                    Note over R: reescribe limites.csv con el monto nuevo
                    R-->>-CG: listo
                    CG->>CG: limites.set(i, l)
                    CG-->>C: listo
                end
            end
            opt ningún límite era de ese mes
                CG-->>C: IllegalStateException("No hay un límite definido para ese período")
            end
            deactivate CG
        end
        C->>C: mensajes.add("Límite modificado correctamente") o el error
        C-->>-V: mensajes
    end

    Note over CG,E: mismoPeriodo lo resuelve la subclase: LimiteMensual compara mes y año (polimorfismo)
    V->>V: mostrar(mensajes)
    V-->>-Usuario: ve el resultado y la lista de límites actualizada
```
