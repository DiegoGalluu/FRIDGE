# RESULTADOS DE FRIDGE

Este documento resume los resultados esperados al ejecutar la app FRIDGE y generar un informe de despensa.

Los tiempos pueden cambiar segun el movil o emulador usado.

## Datos de ejemplo

Al pulsar cargar datos de ejemplo se crean estos productos:

| producto | categoria | cantidad | caduca en dias |
|---|---|---:|---:|
| leche | lacteos | 1 | 2 |
| tomate | verdura | 4 | 3 |
| pollo | carne | 1 | 1 |
| arroz | despensa | 1 | 90 |
| queso | lacteos | 1 | 7 |

## Resultado visible para el usuario

Ejemplo de resumen generado:

```text
total de productos 5
productos que caducan pronto 3
productos caducados 0
producto mas urgente pollo
recomendacion de compra consume primero los productos que caducan pronto
```

## PSP RA1 procesos

La clase `GestorProcesos` ejecuta procesos externos usando `ProcessBuilder`.

### Proceso 1 mensaje simple

Comando:

```text
sh -c echo informe de despensa
```

Salida esperada:

```text
stdout informe de despensa
stderr sin salida de error
codigo 0
tiempo variable
correcto true
```

### Proceso 2 lectura de archivo csv

Comando:

```text
sh -c cat despensa_exportada.csv
```

Salida esperada:

```text
nombre,categoria,cantidad,dias
leche,lacteos,1,2
tomate,verdura,4,3
pollo,carne,1,1
arroz,despensa,1,90
queso,lacteos,1,7
```

### Proceso 3 contar lineas

Comando:

```text
sh -c wc -l despensa_exportada.csv
```

Salida esperada:

```text
6 despensa_exportada.csv
```

Hay 6 lineas porque la primera es la cabecera del CSV.

### Proceso 4 ordenar archivo

Comando:

```text
sh -c sort despensa_exportada.csv
```

Salida esperada:

```text
arroz,despensa,1,90
leche,lacteos,1,2
nombre,categoria,cantidad,dias
pollo,carne,1,1
queso,lacteos,1,7
tomate,verdura,4,3
```

El orden puede variar si el sistema cambia el criterio de ordenacion.

## PSP RA1 comunicacion entre procesos

La comunicacion se hace mediante un archivo temporal.

Proceso productor:

```text
sort despensa_exportada.csv > despensa_ordenada.csv
```

Proceso consumidor:

```text
cat despensa_ordenada.csv
```

Explicacion:

1. El primer proceso produce un archivo ordenado
2. El segundo proceso consume ese archivo y lo lee
3. La app muestra stdout, stderr, codigo de salida y tiempo de ambos

## PSP RA1 comparacion secuencial y concurrente

Ejemplo:

| modo | tiempo |
|---|---:|
| secuencial | 35 ms |
| concurrente | 18 ms |

El tiempo concurrente puede ser menor porque varios procesos se lanzan a la vez en hilos distintos.

En un emulador rapido la diferencia puede ser pequena porque los comandos son muy simples.

## PSP RA1 error controlado

Comando:

```text
sh -c cat archivo_que_no_existe.csv
```

Resultado esperado:

```text
stdout sin salida estandar
stderr cat archivo_que_no_existe.csv no such file or directory
codigo distinto de 0
correcto false
```

La app no se cierra porque el error se captura y se muestra como error controlado.

## PSP RA2 hilos y corrutinas

La clase `GestorHilos` ejecuta tareas con hilos y corrutinas.

### Hilo real

Se usa `Thread` para revisar caducidades.

Salida esperada:

```text
productos caducados 0
productos urgentes 3
estado finalizado
correcto true
```

### Corrutina

Se usa `async` para calcular compra sugerida.

Salida esperada:

```text
comprar leche, pollo, arroz, queso
estado finalizado
correcto true
```

La salida puede cambiar segun los productos guardados.

## PSP RA2 productor consumidor

Se usa `Channel<Producto>`.

Salida esperada:

```text
productor envia leche
consumidor recibe leche
consumidor marca leche como urgente
productor envia tomate
consumidor recibe tomate
consumidor marca tomate como urgente
```

Explicacion:

1. El productor envia productos al canal
2. El consumidor recibe productos del canal
3. El consumidor clasifica cada producto como correcto, urgente o caducado

## PSP RA2 comparacion secuencial y concurrente

Ejemplo:

| modo | tiempo |
|---|---:|
| secuencial | 240 ms |
| concurrente | 85 ms |

La version concurrente suele ser mas rapida porque las tres tareas se ejecutan al mismo tiempo con corrutinas.

## PSP RA2 error controlado

Se simula una lista vacia y se lanza una excepcion controlada.

Salida esperada:

```text
nombre error de hilo
salida lista vacia para probar error
estado error controlado
correcto false
```

La app no se cierra porque el error se captura y se devuelve como `ResultadoTarea`.
