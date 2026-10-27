```mermaid
flowchart TD
    INICIO([Inicio]) --> MENU[Pantalla principal]

    MENU -->|Registrar consumo| REG[Registrar consumo]
    MENU -->|Consultar consumos| CONS[Consultar y filtrar consumos]
    MENU -->|Limite mensual| LIM[Definir o modificar limite mensual]
    MENU -->|Saldo y estado| SALDO[Consultar saldo y estado]
    MENU -->|Categorias| CAT[Categorias]
    MENU -->|Medios de pago| MED[Medios de pago]
    MENU -->|Reportes| REP[Reportes]
    MENU -->|Salir| FIN([Fin])

    REG -->|Registrar| RES_REG[Resultado del registro]
    RES_REG -->|Registrar otro| REG
    RES_REG -->|Volver| MENU
    REG -->|Volver| MENU

    CONS -->|Filtrar| LISTA[Listado de consumos]
    LISTA -->|Nuevo filtro| CONS
    LISTA -->|Volver| MENU
    CONS -->|Volver| MENU

    LIM -->|Guardar| RES_LIM[Resultado del limite]
    RES_LIM -->|Ver saldo y estado| SALDO
    RES_LIM -->|Volver| MENU
    LIM -->|Volver| MENU

    SALDO -->|Definir o modificar limite| LIM
    SALDO -->|Volver| MENU

    CAT -->|Agregar| CAT_ALTA[Agregar categoria]
    CAT -->|Modificar| CAT_MOD[Modificar categoria]
    CAT -->|Eliminar| CAT_BAJA[Eliminar categoria]
    CAT_ALTA -->|Aceptar| CAT
    CAT_MOD -->|Aceptar| CAT
    CAT_BAJA -->|Aceptar| CAT
    CAT -->|Volver| MENU

    MED -->|Agregar| MED_ALTA[Agregar medio de pago]
    MED -->|Modificar| MED_MOD[Modificar medio de pago]
    MED -->|Eliminar| MED_BAJA[Eliminar medio de pago]
    MED_ALTA -->|Aceptar| MED
    MED_MOD -->|Aceptar| MED
    MED_BAJA -->|Aceptar| MED
    MED -->|Volver| MENU

    REP -->|Estadistico| REP_EST[Reporte estadistico]
    REP -->|Gastos por periodo| REP_PER[Reporte de gastos por periodo]
    REP_EST -->|Volver| REP
    REP_PER -->|Volver| REP
    REP -->|Volver| MENU
    ```