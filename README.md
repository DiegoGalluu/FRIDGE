# FRIDGE

FRIDGE es una aplicacion Android para controlar los alimentos que hay en casa, evitar desperdicio y organizar compras y recetas a partir de la despensa real.

## Tecnologias

- Kotlin
- Android Studio
- Jetpack Compose
- Material Design 3
- Navigation Compose
- SharedPreferences
- JSON con `org.json`
- API Spoonacular
- ProcessBuilder
- Thread
- Corrutinas
- Channel de corrutinas

## Cómo ejecutar

1. Abre Android Studio.
2. Pulsa `Open`.
3. Selecciona la carpeta `FRIDGE`.
4. Espera a que Gradle sincronice el proyecto.
5. Ejecuta la app en un emulador o movil Android.

Para generar un APK debug desde la carpeta del proyecto:

```text
.\gradlew.bat :app:assembleDebug
```

El APK se genera en:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Pantallas

La app se organiza en seis pantallas principales:

1. Inicio
2. Mi despensa
3. Añadir producto
4. Lista de la compra
5. Recetas
6. Informe de despensa

## Funcionalidades

- Añadir productos con nombre, cantidad, categoria y dias de caducidad.
- Ver la despensa en tarjetas faciles de revisar.
- Filtrar productos por todos, proximos a caducar o caducados.
- Marcar productos como consumidos.
- Enviar productos a la lista de la compra.
- Crear y completar items de compra.
- Buscar recetas con Spoonacular usando los ingredientes disponibles.
- Priorizar alimentos proximos a caducar en las sugerencias.
- Generar un informe de despensa con resumen, plan de consumo y sugerencias de compra.

## Flujo de demostración

1. Revisa el resumen de inicio.
2. Abre el menu lateral y entra en `Ayuda`.
3. Pulsa `Introducir datos de ejemplo` para cargar una despensa variada.
4. Entra en `Mi despensa` para consultar alimentos guardados.
5. Usa los filtros para localizar alimentos proximos o caducados.
6. Marca alimentos como consumidos o anadelos a la compra.
7. Entra en `Recetas` y busca ideas con los productos disponibles.
8. Toca una receta para ver ingredientes completos y pasos.
9. Entra en `Informe de despensa` y pulsa `Actualizar informe`.
10. Revisa el plan para hoy, los tiempos calculados y la ruta del archivo `resultados_YYYY-MM-DD_HH-mm-ss.md` generado en `Documentos/FRIDGE`.

## Recetas

La pantalla de recetas prepara una lista corta de ingredientes disponibles y prioriza los productos proximos a caducar. Despues consulta Spoonacular con el endpoint de busqueda por ingredientes.

La respuesta se transforma en tarjetas con:

- Ingredientes usados de la despensa.
- Ingredientes que faltan.
- Ingredientes disponibles que no entran en la receta.

La clave de Spoonacular se configura en:

```text
app/src/main/java/com/example/fridge/recetas/RecipeApiConfig.kt
```

## Informe de despensa

El informe convierte la despensa en datos procesables para generar un resumen practico. Internamente combina exportacion CSV, procesos del sistema, hilos y corrutinas para calcular el estado de los productos y proponer acciones utiles.

El usuario ve solo el resultado final: productos prioritarios, productos caducados, recomendaciones de consumo y posibles compras.

## Estructura principal

- `MainActivity`: navegacion y contenedor principal.
- `modelo`: entidades de despensa, compra, recetas e informes.
- `datos`: almacenamiento local y datos de ejemplo.
- `ui`: pantallas y componentes visuales.
- `recetas`: integracion con Spoonacular.
- `procesamiento`: tareas internas para informes, procesos y concurrencia.
- `util`: rutas y utilidades de fechas.
