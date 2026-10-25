# Gestor-de-gastos
 Documentación de Análisis
P1. Objetivo y alcance
El objetivo del Sistema de Control de Gastos es permitir que el usuario registre y administre sus gastos personales, controle sus presupuestos y analice sus hábitos de consumo, facilitando la organización de sus finanzas.
El sistema abarca la gestión de consumos, incluyendo su registro, modificación, eliminación, consulta y filtrado; la definición de límites de gastos semanales y mensuales; la administración de categorías y medios de pago; y la generación de reportes estadísticos y consultas de gastos por período.
Además, contempla la persistencia de la información mediante archivos, así como la importación de datos y la carga de una configuración inicial. También permite verificar el cumplimiento de los límites establecidos y notificar al usuario cuando se detecten excesos o situaciones que requieran atención.
El sistema está destinado a un usuario que interactúa con sus funcionalidades para mantener un registro organizado y actualizado de sus gastos.

P2. Descripción de alto nivel del sistema
El Sistema de Control de Gastos es una aplicación que centraliza la información sobre los consumos del usuario y permite realizar un seguimiento de sus gastos y presupuestos.
El sistema se organiza en cinco módulos principales:

Módulo de Consumos: permite registrar, modificar, eliminar, consultar y filtrar los gastos según diferentes criterios.
Módulo de Presupuesto y Límites: permite definir límites de gastos, consultar el saldo disponible y conocer el estado de cada presupuesto.
Módulo de Entidades MaestCras: permite gestionar las categorías de gastos y los medios de pago disponibles.
Módulo de Análisis y Reportes: permite consultar estadísticas y obtener información sobre los gastos realizados durante un período.
Módulo de Persistencia y Archivos: permite guardar y recuperar la información, importar datos y cargar la configuración inicial.

P3. Requerimientos funcionales
Gestión de consumos ( CU1 , CU2 , CU3 ): El sistema debe permitir al usuario registrar (CU1), modificar (CU2) y eliminar (CU3) consumos, capturando información de monto, fecha, concepto, categoría y medio de pago (admitiendo cantidad de cuotas cuando aplique).
Consulta y filtrado de consumos ( CU4 ): El sistema debe permitir consultar los consumos registrados y filtrarlos por período, categoría, medio de pago y palabra clave.
Gestión de límites ( CU5 ): El sistema debe permitir definir y modificar el límite de gasto monetario asignado a un período determinado.
Consulta de saldo y estado ( CU6 ): El sistema debe calcular el saldo disponible del presupuesto y determinar su estado mediante un indicador de ejecución.
Validación y notificación de alertas ( CU7 ): El sistema debe verificar condicionalmente los límites establecidos ante operaciones que modifiquen o consulten los gastos («extend» de CU1, CU2 y CU6) y notificar al usuario mediante una advertencia visual en caso de producirse un sobregiro.
Gestión de entidades maestras ( CU8 , CU9 ): El sistema debe permitir administrar las categorías de gastos (CU8) y la jerarquía de medios de pago (CU9), contemplando las modalidades de efectivo, tarjeta de débito, tarjeta de crédito y transferencia.
Generación de reportes y análisis ( CU10 , CU11 ): El sistema debe generar reportes estadísticos de distribución del gasto (CU10) y permitir consultar los gastos consolidados correspondientes a un período determinado (CU11).
Persistencia y carga inicial ( CU12 ): El sistema debe cargar automáticamente en memoria la configuración inicial y el historial de datos desde el archivo local al momento del arranque.

P4. Requerimientos no funcionales
RNF01: Rendimiento y tiempo de procesamiento (Performance)
El sistema debe procesar y guardar un consumo en un tiempo máximo de 3 segundos. Asimismo, las consultas de saldos, el filtrado de consumos y la generación de reportes deben ejecutarse en tiempos adecuados para garantizar una interacción fluida.

RNF02: Usabilidad y consistencia visual (Usability)
La interfaz debe permitir registrar un gasto en efectivo en un máximo de [VER] toques desde la pantalla de inicio y diferenciar claramente los métodos de pago mediante indicadores visuales consistentes en todas las pantallas.


1. Módulo 1: Consumos 
Valores por defecto inteligentes: Al abrir la pantalla de registro, completar automáticamente la fecha de hoy y seleccionar la última categoría o medio de pago utilizado para agilizar la carga.
Nota/Concepto con hashtags de búsqueda: Permitir incluir etiquetas en el concepto (ej. "Almuerzo #trabajo" o "Nafta #viaje") para facilitar el filtrado posterior en CU04.
Validación en tiempo real: Deshabilitar el botón de guardado si el monto es 0 o negativo, mostrando una sugerencia en rojo antes de enviar los datos.

2. Módulo 2: Presupuesto y Alertas 
Sistema de Alerta por Semáforo:
🟢 Verde (< 70%): Estado óptimo / Presupuesto saludable.
🟡 Amarillo (70% - 99%): Advertencia preventiva ("Atención: Has consumido el 85% del límite").
🔴 Rojo (≥ 100%): Alerta de sobregiro (CU07) disparada mediante un diálogo flotante al registrar el gasto.
Reinicio Automático de Período: Que el presupuesto venza y se reinicie automáticamente el primer día del mes (ej. 1 de cada mes) sin borrar el historial anterior.

3. Módulo 3: Entidades Maestras 
Categorías Predefinidas (Semilla): Incluir un conjunto básico de categorías de fábrica (Comida, Transporte, Servicios, Salud, Entretenimiento) para que el sistema sea útil desde el segundo cero.
Aprovechamiento del Polimorfismo en Medios de Pago:
Tarjeta de Crédito: Permitir ingresar recargos por cuotas.
Efectivo / Débito: Descuento directo sin comisiones.
Transferencia: Guardar alias/CBU asociado.

4. Módulo 4: Análisis e Informes 
Resumen de Distribución Porcentual: Mostrar el porcentaje del presupuesto consumido por cada categoría (ej. 40% Supermercado, 25% Alquiler, 15% Servicios).
Comparativa Mensual Básica: Mostrar la diferencia de gasto total entre el mes actual y el mes anterior ("Gastaste un 12% más que el mes pasado").

5. Módulo 5: Persistencia y Configuración 
Configuración Inicial Silenciosa (CU14 e CU13): Si al arrancar la aplicación no se detecta el archivo de datos local (gastos.csv o datos.json), el sistema genera automáticamente el archivo base con las categorías e instancias maestras por defecto sin preguntarle al usuario.
Formato Estructurado CSV / TXT (CU12): Guardar los registros en formato separado por comas (fecha,monto,concepto,categoria,medioPago). Esto facilita la lectura con BufferedReader en Java y permite abrir el archivo en Excel.
Auto-guardado al Salir (Auto-save): Ejecutar la exportación a disco automáticamente al cerrar la ventana principal de la aplicación, previniendo pérdidas por cierres inesperados.


RNF03: Persistencia local y exportación de reportes (Storage)
Toda la información del sistema, incluidos consumos, presupuestos, categorías y medios de pago, debe almacenarse en archivos locales en formato CSV. Al iniciar la aplicación, los datos deben recuperarse automáticamente. Asimismo, el sistema debe permitir exportar reportes de gastos en formato CSV.


