# CU4 – Consultar y filtrar consumos

Todos los filtros son opcionales. El `Controller` crea un `Criterio` por cada filtro elegido y `ControlGasto` los aplica sin saber cuáles son (patrón Strategy).

```mermaid
sequenceDiagram
    actor Usuario
    participant V as Vista
    participant C as Controller
    participant CG as ControlGasto
    participant R as Repositorio
    participant K as k : Criterio

    Usuario->>+V: completa los filtros que quiere y toca "Consultar"
    Note over V,C: todos los filtros son opcionales: desde y hasta, categoría, medio de pago y palabra clave o hashtag
    V->>+C: consultarConsumos(desde, hasta, categoria, medioDePago, palabraClave)

    alt pone una sola fecha
        C->>C: mensajes.add("Datos incorrectos o incompletos")
    else filtros válidos
        opt hay fechas
            create participant F as CriterioRangoFechas
            C->>F: new CriterioRangoFechas(desde, hasta)
        end
        opt eligió una categoría
            create participant CA as CriterioCategoria
            C->>CA: new CriterioCategoria(categoria)
        end
        opt eligió un medio de pago
            create participant M as CriterioMedioPago
            C->>M: new CriterioMedioPago(medioDePago)
        end
        opt escribió una palabra clave
            create participant P as CriterioPalabraClave
            C->>P: new CriterioPalabraClave(palabraClave)
        end

        C->>+CG: consultarConsumos(criterios)
        CG->>+R: listar()
        R-->>-CG: consumos
        loop por cada consumo c
            CG->>CG: cumpleTodos(c, criterios)
            loop por cada criterio k de la lista
                CG->>+K: cumple(c)
                Note over K: Strategy: cada criterio concreto responde a su manera (fechas, categoría, medio o palabra)
                K-->>-CG: true o false
            end
            opt cumple todos los criterios
                CG->>CG: resultado.add(c)
            end
        end
        CG->>CG: ordenar por fecha
        CG-->>-C: consumos filtrados

        alt no hay consumos
            C->>C: mensajes.add("No se encontraron consumos")
        else hay consumos
            C->>C: una línea por consumo y el total
        end
    end

    C-->>-V: mensajes
    V->>V: muestra los mensajes en el área de resultado
    V-->>-Usuario: ve los consumos filtrados y el total
```
