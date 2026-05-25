# Cumplimiento de requisitos

Este documento sirve como guia rapida para revisar la entrega de FRIDGE frente a los requisitos de PMDM y PSP.

## PMDM RA1 - Kotlin

| Requisito | Donde se cumple |
|---|---|
| Proyecto en Kotlin | Todo el codigo principal esta en `app/src/main/java/com/example/fridge/**/*.kt` |
| Sintaxis basica de Kotlin | Variables, funciones, condicionales y listas en pantallas, datos y utilidades |
| Clases y objetos | `Producto`, `ItemCompra`, `InformeDespensa`, `AlmacenDatos`, `DatosEjemplo`, `UtilFechas` |
| Data classes | Modelos de `Producto`, `ItemCompra`, `InformeDespensa`, `ResultadoProceso`, `ResultadoTarea` |
| Null safety | Uso de tipos nullable y comprobaciones en formularios, JSON e informe |
| Programa ejecutable | App Android ejecutable desde Android Studio |

## PMDM RA2 - Android con Jetpack Compose

| Requisito | Donde se cumple |
|---|---|
| Kotlin y Jetpack Compose | `MainActivity.kt` y pantallas en paquete `ui` |
| Layouts Column, Row y Box | `PantallaInicio.kt`, `PantallaDespensa.kt`, `PantallaCompra.kt`, `PantallaNuevoProducto.kt` |
| Material Design 3 | Dependencia `androidx.compose.material3:material3` y tema `FridgeTheme` |
| Bottom navigation | `NavigationBar` en `MainActivity.kt` |
| Navigation drawer | `ModalNavigationDrawer` y `NavigationDrawerItem` en `MainActivity.kt` |
| Card | `Card` en `ComponentesComunes.kt` |
| Tarjetas Material | `ElevatedCard` en inicio, despensa, compra e informe |
| Text y OutlinedTextField | Formularios de producto y compra |
| Button, OutlinedButton y TextButton | Navegacion, filtros, formularios y dialogos |
| FloatingActionButton | Botón de añadir producto en la pantalla de despensa |
| Snackbar | `SnackbarHostState` y mensajes tras guardar, eliminar o generar informe |
| Multiples pantallas | Inicio, despensa, nuevo producto, compra e informe |
| Formulario con validacion | `PantallaNuevoProducto.kt` y `PantallaCompra.kt` |
| Lista de elementos | `LazyColumn` en despensa y lista de la compra |
| API externa | Pantalla `Recetas` con llamada HTTPS a Spoonacular usando productos de la despensa |

## PSP RA1 - Procesos y concurrencia

| Requisito | Donde se cumple |
|---|---|
| Lanzar procesos externos | `GestorProcesos.lanzarProceso()` con `ProcessBuilder` |
| Dos procesos o mas | Mensaje simple, lectura CSV, conteo de lineas y ordenacion |
| Captura stdout y stderr | `ResultadoProceso.salidaEstandar` y `ResultadoProceso.salidaError` |
| Codigo de salida | `ResultadoProceso.codigoSalida` |
| Tiempo de ejecucion | `ResultadoProceso.tiempoMs` |
| Comunicacion entre procesos | Productor `sort` genera archivo puente y consumidor `cat` lo lee |
| Secuencial vs concurrente | `compararSecuencialConcurrente()` |
| Error controlado | `demoErrorProceso()` con archivo inexistente |
| Resultados documentados | `RESULTADOS.md`; la app usa el analisis para generar un informe util sin mostrar comandos crudos al usuario |

## PSP RA2 - Hilos, corrutinas y sincronizacion

| Requisito | Donde se cumple |
|---|---|
| Hilo real | `GestorHilos.revisarCaducidadesConThread()` |
| Corrutina con async | `GestorHilos.calcularCompraConCorrutina()` |
| Captura de salida y estado | `ResultadoTarea.salida`, `estado`, `correcto` |
| Tiempo de ejecucion | `ResultadoTarea.tiempoMs` |
| Productor-consumidor | `Channel<Producto>` en `demoProductorConsumidor()` |
| Secuencial vs concurrente | `compararSecuencialConcurrenteHilos()` |
| Error controlado | `demoErrorHilo()` con `IllegalStateException` |
| Resultados documentados | `RESULTADOS.md`; la pantalla de informe muestra acciones de despensa y compra |

## Pendientes administrativos

- Abrir el proyecto en Android Studio para que descargue/sincronice Gradle y Android SDK si aun no estan instalados.
- Generar el APK desde Android Studio o con `gradlew :app:assembleDebug` cuando exista el wrapper.
- Si el profesor exige literalmente los repositorios separados de PSP, crear los repositorios publicos indicados en `PSP.md` o explicar que esta entrega integra RA1 y RA2 de PSP dentro de la app FRIDGE.
- Crear repositorio GitHub publico y hacer commits progresivos antes de entregar el enlace.
