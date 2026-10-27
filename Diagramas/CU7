```mermaid
sequenceDiagram
    actor Usuario
    participant Vista
    participant Controller
    participant ControlGasto
    participant Repositorio

    Usuario->>+Vista: elige una operación sobre categorías

    alt listar
        Vista->>+Controller: obtenerCategorias()
        Controller->>+ControlGasto: getCategorias()
        ControlGasto->>+Repositorio: listarCategorias()
        Repositorio-->>-ControlGasto: categorias
        ControlGasto-->>-Controller: categorias
        Controller-->>-Vista: categorias
    else agregar
        Vista->>+Controller: agregarCategoria(nombre)
        Controller->>+ControlGasto: agregarCategoria(nombre)
        ControlGasto->>+Repositorio: listarCategorias()
        Repositorio-->>-ControlGasto: categorias
        alt el nombre ya existe
            ControlGasto-->>Controller: false
        else el nombre es nuevo
            ControlGasto->>+Repositorio: guardarCategoria(categoria)
            deactivate Repositorio
            ControlGasto-->>Controller: true
        end
        deactivate ControlGasto
        Controller-->>Vista: mensaje
        deactivate Controller
    else modificar
        Vista->>+Controller: modificarCategoria(id, nombre)
        Controller->>+ControlGasto: modificarCategoria(id, nombre)
        ControlGasto->>+Repositorio: actualizarCategoria(categoria)
        deactivate Repositorio
        ControlGasto-->>Controller: ok o error (no existe, nombre repetido)
        deactivate ControlGasto
        Controller-->>Vista: mensaje
        deactivate Controller
    else eliminar
        Vista->>+Controller: eliminarCategoria(id)
        Controller->>+ControlGasto: eliminarCategoria(id)
        ControlGasto->>+Repositorio: listar()
        Repositorio-->>-ControlGasto: consumos
        alt tiene consumos asociados
            ControlGasto-->>Controller: error
        else sin consumos asociados
            ControlGasto->>+Repositorio: eliminarCategoria(id)
            deactivate Repositorio
            ControlGasto-->>Controller: ok
        end
        deactivate ControlGasto
        Controller-->>Vista: mensaje
        deactivate Controller
    end

    Vista-->>-Usuario: muestra el resultado
```
