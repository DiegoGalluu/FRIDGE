# FRIDGE

FRIDGE es una aplicacion Android sencilla para controlar los alimentos que hay en casa, evitar tirar comida y saber que productos deben comprarse.

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
3. Anadir producto
4. Lista de compra
5. Informe de despensa

## Funcionalidades principales

- Cargar productos de ejemplo
- Anadir productos con formulario validado
- Ver productos en tarjetas
- Filtrar productos por todos, caducan pronto o caducados
- Marcar productos como consumidos
- Anadir productos a la lista de la compra
- Eliminar productos
- Crear items de compra
- Marcar items como comprados
- Generar informe de despensa
- Ver detalles tecnicos de procesos, hilos y corrutinas

## Flujo de demostracion recomendado

1. Abrir la app
2. Pulsar cargar datos de ejemplo
3. Entrar en mi despensa
4. Ver las tarjetas de productos
5. Probar el filtro caducan pronto
6. Pulsar el boton flotante para anadir producto
7. Guardar un producto nuevo
8. Pulsar un producto y anadirlo a la lista de la compra
9. Entrar en lista de la compra
10. Marcar un item como comprado
11. Abrir el menu lateral
12. Entrar en informe de despensa
13. Pulsar generar informe
14. Ensenar el resumen para usuario
15. Ensenar los detalles tecnicos para PSP

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

La parte visible para el usuario permite anadir alimentos, revisar caducidades y preparar una lista de la compra.

La parte de informe aprovecha esos mismos datos para cumplir PSP. Primero exporta la despensa a CSV, despues ejecuta procesos externos con ProcessBuilder y finalmente ejecuta pruebas con hilos y corrutinas.

Asi el usuario ve un informe util y el profesor puede revisar claramente procesos, hilos, corrutinas, comunicacion, tiempos y errores controlados.
