# Requisitos y evidencias

Este documento concentra la trazabilidad academica de FRIDGE. El resto de la documentacion mantiene un enfoque de aplicacion para usuario final.

## PMDM RA1 - Kotlin

| Requisito | Archivo o pantalla | Evidencia |
|---|---|---|
| Sintaxis basica, variables, condicionales y funciones | `app/src/main/java/com/example/fridge/modelo/Producto.kt` | Calcula caducidad, estados y textos visibles. |
| Clases y objetos | `app/src/main/java/com/example/fridge/datos/AlmacenDatos.kt` | Objeto singleton para guardar y cargar datos locales. |
| Data classes | `app/src/main/java/com/example/fridge/modelo/*.kt` | Modelos `Producto`, `ItemCompra`, `InformeDespensa`, `RecetaSugerida`, `ResultadoProceso` y `ResultadoTarea`. |
| Null safety y control de errores | `Producto.kt`, `ItemCompra.kt`, `InformeDespensa.kt` | Metodos `desdeJson` devuelven null si el JSON no es valido. |
| Colecciones y transformaciones | `PantallaInforme.kt`, `RecipeApiServicio.kt` | Filtros, ordenaciones, `map`, `filter`, `distinctBy`, `sortedWith`. |
| Comentarios explicativos en codigo | Paquetes `modelo`, `datos` y `procesamiento` | Comentarios breves para explicar persistencia, procesos, hilos y exportaciones. |

## PMDM RA2 - Aplicacion Android con Jetpack Compose

| Requisito | Archivo o pantalla | Evidencia |
|---|---|---|
| Proyecto Android en Kotlin | `app/build.gradle.kts` | Configuracion Android, Kotlin y Compose. |
| Multiples pantallas | `MainActivity.kt` y `app/src/main/java/com/example/fridge/ui` | Inicio, despensa, nuevo producto, compra, recetas e informe. |
| Navegacion | `MainActivity.kt` y `util/Rutas.kt` | `NavHost`, rutas, barra inferior y menu lateral. |
| Layouts Compose | `PantallaInicio.kt`, `PantallaDespensa.kt`, `PantallaCompra.kt`, `PantallaInforme.kt` | Uso de `Column`, `Row`, `Box`, tarjetas y scroll. |
| Material Design 3 | `ui/TemaApp.kt`, `ComponentesComunes.kt` | Tema, `ElevatedCard`, botones, dialogos, `Snackbar`. |
| Formularios con validacion | `PantallaNuevoProducto.kt`, `PantallaCompra.kt` | Alta de productos e items de compra con validacion basica. |
| Listas de elementos | `PantallaDespensa.kt`, `PantallaCompra.kt`, `PantallaInforme.kt` | Listado de despensa, compra y productos filtrados. |
| API externa | `recetas/RecipeApiServicio.kt` | Consulta a Spoonacular por ingredientes y detalle de receta por id. |
| Permisos y manifest | `app/src/main/AndroidManifest.xml` | Internet y escritura legacy para exportacion en documentos cuando aplica. |

## PSP RA1 - Procesos

| Requisito | Archivo o pantalla | Evidencia |
|---|---|---|
| Lanzamiento de procesos externos | `procesamiento/GestorProcesos.kt` | Usa `ProcessBuilder` para ejecutar comandos del sistema. |
| Captura de salida estandar y error | `GestorProcesos.lanzarProceso` | Guarda `stdout`, `stderr`, codigo de salida y tiempo en `ResultadoProceso`. |
| Comunicacion entre procesos | `GestorProcesos.comunicarProcesosConArchivo` | Un proceso genera `despensa_ordenada.csv` y otro lo consume. |
| Comparacion secuencial/concurrente | `GestorProcesos.compararSecuencialConcurrente` | Ejecuta comandos en modo secuencial y concurrente con hilos. |
| Error controlado | `GestorProcesos.generarErrorControladoProceso` | Intenta leer un archivo inexistente y captura el fallo. |
| Resultados documentados | `RESULTADOS.md` y `procesamiento/ExportadorResultados.kt` | La app genera un markdown fechado en `Documentos/FRIDGE`. |
| Pantalla de acceso | `ui/PantallaInforme.kt` | Boton `Actualizar informe` lanza el procesamiento y muestra resumen. |

## PSP RA2 - Hilos, corrutinas y sincronizacion

| Requisito | Archivo o pantalla | Evidencia |
|---|---|---|
| Uso de hilos | `procesamiento/GestorHilos.kt` | `revisarCaducidadesConThread` usa `Thread`. |
| Uso de corrutinas | `GestorHilos.calcularCompraConCorrutina` | Usa `async`, `await`, `withContext` y `Dispatchers`. |
| Productor-consumidor | `GestorHilos.ejecutarProductorConsumidor` | Usa `Channel<Producto>` para enviar y recibir productos. |
| Comparacion secuencial/concurrente | `GestorHilos.compararSecuencialConcurrenteHilos` | Compara tareas con `delay`, ejecucion secuencial y `async`. |
| Error controlado | `GestorHilos.generarErrorControladoHilo` | Simula una lista vacia y devuelve un `ResultadoTarea` con estado de error. |
| Captura de estado y tiempos | `modelo/ResultadoTarea.kt` | Guarda salida, estado, tiempo y resultado correcto/error. |
| Integracion en la app | `MainActivity.kt` y `PantallaInforme.kt` | El informe ejecuta procesos, hilos y corrutinas desde una accion de usuario. |

## Evidencia de ejecucion

- `RESULTADOS.md` contiene ejemplos de salidas, tiempos y conclusiones.
- Al pulsar `Actualizar informe`, la app genera un archivo fechado en `Documentos/FRIDGE/resultados_YYYY-MM-DD_HH-mm-ss.md`.
- Ese archivo incluye salidas reales de procesos, hilos, corrutinas, comunicacion interna, comparacion de tiempos, errores controlados y conclusiones.
