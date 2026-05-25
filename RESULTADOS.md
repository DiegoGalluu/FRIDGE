# Resultados de FRIDGE

Este documento recoge ejemplos de salidas generadas por la app al trabajar con una despensa de prueba. Los tiempos pueden cambiar segun el movil o emulador usado.

## Datos de ejemplo

Al cargar los datos de ejemplo se crean estos productos:

| producto | categoria | cantidad | caduca en dias |
|---|---|---:|---:|
| Leche | Lacteos | 1 | 2 |
| Tomate | Verdura | 4 | 3 |
| Pollo | Carne | 1 | 1 |
| Arroz | Despensa | 1 | 90 |
| Queso | Lacteos | 1 | 7 |

## Resumen visible

Ejemplo de resumen generado:

```text
total de productos 5
productos proximos a caducar 3
productos caducados 0
producto mas proximo a caducar Pollo
recomendacion de compra consume primero los productos proximos a caducar
```

## Procesamiento de archivos

La app puede exportar la despensa a CSV para procesar un informe interno.

### Lectura de despensa

Archivo generado:

```text
nombre,categoria,cantidad,dias
Leche,Lacteos,1,2
Tomate,Verdura,4,3
Pollo,Carne,1,1
Arroz,Despensa,1,90
Queso,Lacteos,1,7
```

### Conteo de productos

Salida esperada:

```text
6 despensa_exportada.csv
```

Hay 6 lineas porque la primera es la cabecera del CSV.

### Ordenacion de datos

Salida esperada:

```text
Arroz,Despensa,1,90
Leche,Lacteos,1,2
nombre,categoria,cantidad,dias
Pollo,Carne,1,1
Queso,Lacteos,1,7
Tomate,Verdura,4,3
```

El orden puede variar si el sistema cambia el criterio de ordenacion.

## Comunicacion interna

Para algunas tareas, la app genera un archivo intermedio y lo reutiliza en otra operacion.

1. Se produce un archivo ordenado desde la despensa exportada.
2. Se lee ese archivo para comprobar su contenido.
3. Se registra salida, error, codigo de salida y tiempo de cada paso.

## Comparacion de rendimiento

Ejemplo:

| modo | tiempo |
|---|---:|
| secuencial | 35 ms |
| concurrente | 18 ms |

La version concurrente puede ser mas rapida porque varias tareas se lanzan a la vez.

## Errores controlados

Cuando una operacion no puede leer un archivo, la app captura el error y lo transforma en un resultado controlado.

Ejemplo:

```text
stdout sin salida estandar
stderr archivo no encontrado
codigo distinto de 0
correcto false
```

La app no se cierra porque el error se gestiona antes de llegar a la interfaz.

## Tareas en segundo plano

La app usa tareas concurrentes para revisar caducidades y generar sugerencias sin bloquear la interfaz.

### Revision de caducidades

Salida esperada:

```text
productos caducados 0
productos proximos 3
estado finalizado
correcto true
```

### Compra sugerida

Salida esperada:

```text
comprar Leche, Pollo, Arroz, Queso
estado finalizado
correcto true
```

La salida puede cambiar segun los productos guardados.

## Flujo productor consumidor

Al revisar la despensa, una tarea envia productos y otra los clasifica.

Salida esperada:

```text
productor envia Leche
consumidor recibe Leche
consumidor marca Leche como proximo
productor envia Tomate
consumidor recibe Tomate
consumidor marca Tomate como proximo
```

## Concurrencia

Ejemplo:

| modo | tiempo |
|---|---:|
| secuencial | 240 ms |
| concurrente | 85 ms |

La version concurrente suele ser mas rapida porque varias revisiones se ejecutan al mismo tiempo.
