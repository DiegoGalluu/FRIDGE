package com.example.fridge.procesamiento

import com.example.fridge.modelo.ResultadoProceso
import java.io.File
import kotlin.concurrent.thread

class GestorProcesos {
    // ejecuta un comando externo y captura sus datos
    fun lanzarProceso(nombre: String, comando: List<String>): ResultadoProceso {
        val inicio = System.currentTimeMillis()
        return try {
            val proceso = ProcessBuilder(comando).start()
            val salida = proceso.inputStream.bufferedReader().readText()
            val error = proceso.errorStream.bufferedReader().readText()
            val codigo = proceso.waitFor()
            val tiempo = System.currentTimeMillis() - inicio
            ResultadoProceso(
                nombre = nombre,
                comando = comando.joinToString(" "),
                salidaEstandar = salida.ifBlank { "sin salida estandar" },
                salidaError = error.ifBlank { "sin salida de error" },
                codigoSalida = codigo,
                tiempoMs = tiempo,
                correcto = codigo == 0
            )
        } catch (e: Exception) {
            val tiempo = System.currentTimeMillis() - inicio
            ResultadoProceso(
                nombre = nombre,
                comando = comando.joinToString(" "),
                salidaEstandar = "sin salida estandar",
                salidaError = e.message ?: "error desconocido",
                codigoSalida = -1,
                tiempoMs = tiempo,
                correcto = false
            )
        }
    }

    // ejecuta comandos basicos para procesar el informe
    fun ejecutarProcesosBasicos(archivo: File): List<ResultadoProceso> {
        val ruta = rutaSegura(archivo)
        return listOf(
            lanzarProceso("mensaje simple", listOf("sh", "-c", "echo informe de despensa")),
            lanzarProceso("leer archivo csv", listOf("sh", "-c", "cat '$ruta'")),
            lanzarProceso("contar lineas", listOf("sh", "-c", "wc -l '$ruta'")),
            lanzarProceso("ordenar archivo", listOf("sh", "-c", "sort '$ruta'"))
        )
    }

    // usa un archivo temporal como puente entre operaciones
    fun comunicarProcesosConArchivo(archivo: File): List<ResultadoProceso> {
        val rutaEntrada = rutaSegura(archivo)
        val archivoPuente = File(archivo.parentFile, "despensa_ordenada.csv")
        val rutaSalida = rutaSegura(archivoPuente)
        val productor = lanzarProceso(
            "productor sort",
            listOf("sh", "-c", "sort '$rutaEntrada' > '$rutaSalida'")
        )
        val consumidor = lanzarProceso(
            "consumidor cat",
            listOf("sh", "-c", "cat '$rutaSalida'")
        )
        return listOf(productor, consumidor)
    }

    // compara procesos secuenciales y concurrentes
    fun compararSecuencialConcurrente(archivo: File): Pair<Long, Long> {
        val ruta = rutaSegura(archivo)
        val comandos = listOf(
            listOf("sh", "-c", "echo analisis uno"),
            listOf("sh", "-c", "cat '$ruta'"),
            listOf("sh", "-c", "wc -l '$ruta'")
        )

        val inicioSecuencial = System.currentTimeMillis()
        comandos.forEachIndexed { indice, comando ->
            lanzarProceso("secuencial ${indice + 1}", comando)
        }
        val tiempoSecuencial = System.currentTimeMillis() - inicioSecuencial

        val inicioConcurrente = System.currentTimeMillis()
        val hilos = comandos.mapIndexed { indice, comando ->
            thread {
                lanzarProceso("concurrente ${indice + 1}", comando)
            }
        }
        hilos.forEach { hilo -> hilo.join() }
        val tiempoConcurrente = System.currentTimeMillis() - inicioConcurrente

        return Pair(tiempoSecuencial, tiempoConcurrente)
    }

    // genera un fallo controlado sin cerrar la app
    fun generarErrorControladoProceso(): ResultadoProceso {
        return lanzarProceso(
            "error controlado",
            listOf("sh", "-c", "cat archivo_que_no_existe.csv")
        )
    }

    // crea un texto completo para la pantalla de informe
    fun generarInformeProcesos(archivo: File): ResumenProcesos {
        val basicos = ejecutarProcesosBasicos(archivo)
        val comunicacion = comunicarProcesosConArchivo(archivo)
        val comparacion = compararSecuencialConcurrente(archivo)
        val error = generarErrorControladoProceso()

        val detalles = buildString {
            appendLine("procesos basicos")
            basicos.forEach { resultado -> appendLine(formatearResultado(resultado)) }
            appendLine("comunicacion entre procesos")
            comunicacion.forEach { resultado -> appendLine(formatearResultado(resultado)) }
            appendLine("comparacion secuencial y concurrente")
            appendLine("tiempo secuencial ${comparacion.first} ms")
            appendLine("tiempo concurrente ${comparacion.second} ms")
            appendLine("error controlado")
            appendLine(formatearResultado(error))
        }

        return ResumenProcesos(
            detalles = detalles,
            tiempoSecuencial = comparacion.first,
            tiempoConcurrente = comparacion.second
        )
    }

    // prepara la ruta para usarla en comandos sh
    private fun rutaSegura(archivo: File): String {
        return archivo.absolutePath.replace("'", "'\\''")
    }

    // da formato a un resultado de proceso
    private fun formatearResultado(resultado: ResultadoProceso): String {
        return buildString {
            appendLine("nombre ${resultado.nombre}")
            appendLine("comando ${resultado.comando}")
            appendLine("stdout ${resultado.salidaEstandar.trim()}")
            appendLine("stderr ${resultado.salidaError.trim()}")
            appendLine("codigo ${resultado.codigoSalida}")
            appendLine("tiempo ${resultado.tiempoMs} ms")
            appendLine("correcto ${resultado.correcto}")
        }
    }
}

data class ResumenProcesos(
    val detalles: String,
    val tiempoSecuencial: Long,
    val tiempoConcurrente: Long
)
