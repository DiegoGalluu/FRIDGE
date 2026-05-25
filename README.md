# FRIDGE

FRIDGE es una aplicación Android sencilla para controlar los alimentos que hay en casa, evitar tirar comida y saber qué productos deben comprarse.

El proyecto esta pensado para recuperar los resultados RA1 y RA2 de PMDM y RA1 y RA2 de PSP en una sola app util para el usuario final.

No incluye RA5, Auth0, OAuth, login, registro, Firebase ni autenticacion.

## Tecnologias usadas

- Kotlin
- Android Studio
- Jetpack Compose
- Material Design 3
- Navigation Compose
- SharedPreferences
- JSON con `org.json`
- ProcessBuilder
- Thread
- Corrutinas
- Channel de corrutinas

## Como abrir el proyecto

1. Descomprime el ZIP
2. Abre Android Studio
3. Pulsa Open
4. Selecciona la carpeta `FRIDGE`
5. Espera a que Gradle sincronice
6. Ejecuta la app en un emulador o movil Android

## Como compilar

Desde la carpeta del proyecto:

```text
.\gradlew.bat :app:assembleDebug
```

El APK debug se genera en:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Tambien se puede revisar el proyecto con:

```text
.\gradlew.bat :app:lintDebug
```

La tabla detallada de requisitos esta en `CUMPLIMIENTO_REQUISITOS.md`.

## Pantallas de la app

La app tiene cinco pantallas principales:

1. Inicio
2. Mi despensa
3. Añadir producto
4. Lista de la compra
5. Informe de despensa

## Funcionalidades principales

- Añadir productos con formulario validado
- Ver productos en tarjetas
- Filtrar productos por todos, próximos a caducar o caducados
- Marcar productos como consumidos
- Añadir productos a la lista de la compra
- Eliminar productos
- Crear ítems de compra
- Marcar ítems como comprados
- Generar informe de despensa
- Ver un plan de consumo y compra con productos caducados, próximos y sugeridos

## Flujo de demostracion recomendado

1. Abrir la app
2. Pulsar la tarjeta Productos para entrar en Mi despensa
3. Pulsar el botón flotante para añadir producto
4. Ver las tarjetas de productos
5. Probar el filtro Próximos
6. Guardar un producto nuevo
7. Pulsar un producto y añadirlo a la lista de la compra
8. Entrar en Lista de la compra
9. Marcar un ítem como comprado
10. Abrir el menú lateral
11. Entrar en Informe de despensa
12. Pulsar Actualizar informe
13. Enseñar el plan para hoy
14. Añadir un producto sugerido a la lista de la compra

## Cumplimiento PMDM RA1

PMDM RA1 pide trabajar en Kotlin y demostrar conceptos basicos del lenguaje.

En este proyecto se usa:

- Variables
- Funciones
- Condicionales
- Listas
- Clases
- Objetos
- Data classes
- Null safety
- Funciones auxiliares
- Codigo comentado

Clases principales relacionadas:

- `Producto`
- `ItemCompra`
- `InformeDespensa`
- `ResultadoProceso`
- `ResultadoTarea`
- `AlmacenDatos`
- `DatosEjemplo`
- `UtilFechas`

## Cumplimiento PMDM RA2

PMDM RA2 pide una aplicacion Android con Kotlin y Jetpack Compose.

En este proyecto se usa:

- `Column`
- `Row`
- `Box`
- `Scaffold`
- `NavigationBar`
- `ModalNavigationDrawer`
- `FloatingActionButton`
- `Snackbar`
- `ElevatedCard`
- `Text`
- `OutlinedTextField`
- `Button`
- `OutlinedButton`
- `TextButton`
- `AlertDialog`
- `LazyColumn`
- Formularios con validacion
- Navegacion entre multiples pantallas
- Material Design 3

## Cumplimiento PSP RA1

PSP RA1 pide procesos externos, captura de salidas, comunicacion entre procesos, comparacion secuencial y concurrente y gestion de errores.

La clase principal es:

- `GestorProcesos`

Se implementa:

- `ProcessBuilder`
- Lanzamiento de procesos externos
- Captura de `stdout`
- Captura de `stderr`
- Captura del codigo de salida
- Medicion de tiempos
- Comunicacion entre procesos usando archivo temporal
- Comparacion secuencial y concurrente
- Error controlado con archivo inexistente

Tambien se usa:

- `ExportadorDespensa`

Esta clase crea el archivo interno `despensa_exportada.csv` para que los procesos tengan datos reales de la despensa.

## Cumplimiento PSP RA2

PSP RA2 pide hilos, corrutinas, comunicacion, comparacion y errores controlados.

La clase principal es:

- `GestorHilos`

Se implementa:

- Hilo real con `Thread`
- Corrutina con `async`
- Productor consumidor con `Channel<Producto>`
- Medicion de tiempos
- Estado de finalizacion
- Comparacion secuencial y concurrente
- Error controlado con `IllegalStateException`

## Clases principales

- `MainActivity`
- `Producto`
- `ItemCompra`
- `InformeDespensa`
- `ResultadoProceso`
- `ResultadoTarea`
- `AlmacenDatos`
- `DatosEjemplo`
- `ExportadorDespensa`
- `GestorProcesos`
- `GestorHilos`
- `PantallaInicio`
- `PantallaDespensa`
- `PantallaNuevoProducto`
- `PantallaCompra`
- `PantallaInforme`
- `ComponentesComunes`
- `TemaApp`
- `Rutas`
- `UtilFechas`

## Explicacion para defensa oral

FRIDGE no es una demo tecnica aislada. Es una app sencilla para controlar alimentos en casa.

La parte visible para el usuario permite añadir alimentos, revisar caducidades y preparar una lista de la compra.

La parte de informe aprovecha esos mismos datos para cumplir PSP. Primero exporta la despensa a CSV, despues ejecuta procesos externos con ProcessBuilder y finalmente ejecuta pruebas con hilos y corrutinas.

La pantalla muestra al usuario un plan útil de consumo y compra. Los detalles técnicos quedan ejecutados y documentados en el código y en `RESULTADOS.md`, pero no se muestran como texto crudo dentro de la app.
