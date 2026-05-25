package com.example.fridge.procesamiento

import com.example.fridge.modelo.Producto
import com.example.fridge.modelo.ResultadoTarea
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.channels.Channel
import java.util.Collections

class GestorHilos {
    // ejecuta el analisis interno con hilos y corrutinas
    suspend fun ejecutarAnalisisHilosYCorrutinas(productos: List<Producto>): ResumenHilos {
        return withContext(Dispatchers.Default) {
            val resultadoThread = revisarCaducidadesConThread(productos)
            val resultadoCorrutina = calcularCompraConCorrutina(productos)
            val resultadoCanal = ejecutarProductorConsumidor(productos)
            val comparacion = compararSecuencialConcurrenteHilos(productos)
            val error = generarErrorControladoHilo()

            val detalles = buildString {
                appendLine("hilo real")
                appendLine(formatearResultado(resultadoThread))
                appendLine("corrutina")
                appendLine(formatearResultado(resultadoCorrutina))
                appendLine("productor consumidor")
                appendLine(formatearResultado(resultadoCanal))
                appendLine("comparacion secuencial y concurrente")
                appendLine("tiempo secuencial ${comparacion.first} ms")
                appendLine("tiempo concurrente ${comparacion.second} ms")
                appendLine("error controlado")
                appendLine(formatearResultado(error))
            }

            ResumenHilos(
                detalles = detalles,
                tiempoSecuencial = comparacion.first,
                tiempoConcurrente = comparacion.second
            )
        }
    }

    // usa thread para revisar productos caducados
    private fun revisarCaducidadesConThread(productos: List<Producto>): ResultadoTarea {
        val inicio = System.currentTimeMillis()
        val salida = StringBuilder()
        var estado = "finalizado"
        var correcto = true

        val hilo = Thread {
            try {
                val caducados = productos.count { producto -> producto.estaCaducado() }
                val proximos = productos.count { producto -> producto.estaProximoACaducar() }
                salida.append("productos caducados $caducados\n")
                salida.append("productos proximos $proximos")
            } catch (e: Exception) {
                estado = "error controlado"
                correcto = false
                salida.append(e.message ?: "error desconocido")
            }
        }

        hilo.start()
        hilo.join()
        val tiempo = System.currentTimeMillis() - inicio

        return ResultadoTarea(
            nombre = "revision con thread",
            salida = salida.toString(),
            estado = estado,
            tiempoMs = tiempo,
            correcto = correcto
        )
    }

    // usa async para calcular compra sugerida
    private suspend fun calcularCompraConCorrutina(productos: List<Producto>): ResultadoTarea = coroutineScope {
        val inicio = System.currentTimeMillis()
        try {
            val diferido = async(Dispatchers.Default) {
                val sugeridos = productos
                    .filter { producto -> producto.cantidad <= 1 || producto.estaCaducado() }
                    .joinToString(", ") { producto -> producto.nombre }
                if (sugeridos.isBlank()) "no hace falta comprar nada por ahora" else "comprar $sugeridos"
            }
            val salida = diferido.await()
            ResultadoTarea(
                nombre = "compra con corrutina",
                salida = salida,
                estado = "finalizado",
                tiempoMs = System.currentTimeMillis() - inicio,
                correcto = true
            )
        } catch (e: Exception) {
            ResultadoTarea(
                nombre = "compra con corrutina",
                salida = e.message ?: "error desconocido",
                estado = "error controlado",
                tiempoMs = System.currentTimeMillis() - inicio,
                correcto = false
            )
        }
    }

    // comunica productor y consumidor con channel
    private suspend fun ejecutarProductorConsumidor(productos: List<Producto>): ResultadoTarea = coroutineScope {
        val inicio = System.currentTimeMillis()
        val eventos = Collections.synchronizedList(mutableListOf<String>())
        val canal = Channel<Producto>()

        try {
            val productor = launch(Dispatchers.Default) {
                productos.forEach { producto ->
                    eventos.add("productor envia ${producto.nombre}")
                    canal.send(producto)
                }
                canal.close()
            }

            val consumidor = launch(Dispatchers.Default) {
                for (producto in canal) {
                    eventos.add("consumidor recibe ${producto.nombre}")
                    val estado = when {
                        producto.estaCaducado() -> "caducado"
                        producto.estaProximoACaducar() -> "proximo"
                        else -> "correcto"
                    }
                    eventos.add("consumidor marca ${producto.nombre} como $estado")
                }
            }

            productor.join()
            consumidor.join()

            ResultadoTarea(
                nombre = "channel productor consumidor",
                salida = eventos.joinToString("\n"),
                estado = "finalizado",
                tiempoMs = System.currentTimeMillis() - inicio,
                correcto = true
            )
        } catch (e: Exception) {
            ResultadoTarea(
                nombre = "channel productor consumidor",
                salida = e.message ?: "error desconocido",
                estado = "error controlado",
                tiempoMs = System.currentTimeMillis() - inicio,
                correcto = false
            )
        }
    }

    // compara tareas secuenciales y concurrentes
    private suspend fun compararSecuencialConcurrenteHilos(productos: List<Producto>): Pair<Long, Long> = coroutineScope {
        val inicioSecuencial = System.currentTimeMillis()
        contarProductos(productos)
        contarCaducados(productos)
        generarCompraSugerida(productos)
        val tiempoSecuencial = System.currentTimeMillis() - inicioSecuencial

        val inicioConcurrente = System.currentTimeMillis()
        val tareaUno = async { contarProductos(productos) }
        val tareaDos = async { contarCaducados(productos) }
        val tareaTres = async { generarCompraSugerida(productos) }
        tareaUno.await()
        tareaDos.await()
        tareaTres.await()
        val tiempoConcurrente = System.currentTimeMillis() - inicioConcurrente

        Pair(tiempoSecuencial, tiempoConcurrente)
    }

    // simula un fallo controlado de una tarea
    private fun generarErrorControladoHilo(): ResultadoTarea {
        val inicio = System.currentTimeMillis()
        return try {
            val listaVacia = emptyList<Producto>()
            if (listaVacia.isEmpty()) throw IllegalStateException("lista vacia para validar control de errores")
            ResultadoTarea("error de hilo", "sin error", "finalizado", System.currentTimeMillis() - inicio, true)
        } catch (e: Exception) {
            ResultadoTarea(
                nombre = "error de hilo",
                salida = e.message ?: "error desconocido",
                estado = "error controlado",
                tiempoMs = System.currentTimeMillis() - inicio,
                correcto = false
            )
        }
    }

    private suspend fun contarProductos(productos: List<Producto>): Int {
        delay(80)
        return productos.size
    }

    private suspend fun contarCaducados(productos: List<Producto>): Int {
        delay(80)
        return productos.count { producto -> producto.estaCaducado() }
    }

    private suspend fun generarCompraSugerida(productos: List<Producto>): List<String> {
        delay(80)
        return productos.filter { producto -> producto.cantidad <= 1 || producto.estaProximoACaducar() }.map { producto -> producto.nombre }
    }

    // da formato a un resultado de tarea
    private fun formatearResultado(resultado: ResultadoTarea): String {
        return buildString {
            appendLine("nombre ${resultado.nombre}")
            appendLine("salida ${resultado.salida.trim()}")
            appendLine("estado ${resultado.estado}")
            appendLine("tiempo ${resultado.tiempoMs} ms")
            appendLine("correcto ${resultado.correcto}")
        }
    }
}

data class ResumenHilos(
    val detalles: String,
    val tiempoSecuencial: Long,
    val tiempoConcurrente: Long
)
