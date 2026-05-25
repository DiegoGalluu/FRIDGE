package com.example.fridge.procesamiento

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.fridge.modelo.InformeDespensa
import com.example.fridge.modelo.Producto
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object ExportadorResultados {
    private const val CARPETA_FRIDGE = "FRIDGE"

    // crea un markdown con fecha y hora en la carpeta Documentos del dispositivo
    fun exportar(context: Context, informe: InformeDespensa, productos: List<Producto>): String {
        val nombreArchivo = nombreArchivo(informe.fecha)
        val markdown = generarMarkdown(informe, productos)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            exportarConMediaStore(context, nombreArchivo, markdown)
        } else {
            exportarConArchivoPublico(nombreArchivo, markdown)
        }
    }

    private fun exportarConMediaStore(context: Context, nombreArchivo: String, markdown: String): String {
        val valores = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, nombreArchivo)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/markdown")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOCUMENTS}/$CARPETA_FRIDGE")
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), valores)
            ?: throw IllegalStateException("No se pudo crear resultados.md en Documentos.")

        resolver.openOutputStream(uri)?.use { salida ->
            salida.write(markdown.toByteArray())
        } ?: throw IllegalStateException("No se pudo escribir resultados.md en Documentos.")

        return "Documentos/$CARPETA_FRIDGE/$nombreArchivo"
    }

    private fun exportarConArchivoPublico(nombreArchivo: String, markdown: String): String {
        val carpeta = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), CARPETA_FRIDGE)
        if (!carpeta.exists()) carpeta.mkdirs()
        val archivo = File(carpeta, nombreArchivo)
        archivo.writeText(markdown)
        return archivo.absolutePath
    }

    private fun nombreArchivo(fechaMillis: Long): String {
        val fecha = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
            .withZone(ZoneId.systemDefault())
            .format(Instant.ofEpochMilli(fechaMillis))
        return "resultados_$fecha.md"
    }

    private fun generarMarkdown(informe: InformeDespensa, productos: List<Producto>): String {
        val proximos = productos.filter { producto -> producto.estaProximoACaducar() }
        val caducados = productos.filter { producto -> producto.estaCaducado() }
        val fecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            .withZone(ZoneId.systemDefault())
            .format(Instant.ofEpochMilli(informe.fecha))

        return buildString {
            appendLine("# Resultados de FRIDGE")
            appendLine()
            appendLine("Archivo generado automaticamente al actualizar el informe de despensa.")
            appendLine()
            appendLine("- Nombre del archivo: ${nombreArchivo(informe.fecha)}")
            appendLine()
            appendLine("## Resumen")
            appendLine()
            appendLine("- Fecha: $fecha")
            appendLine("- Total de productos: ${informe.totalProductos}")
            appendLine("- Productos proximos a caducar: ${informe.productosProximos}")
            appendLine("- Productos caducados: ${informe.productosCaducados}")
            appendLine("- Tiempo secuencial total: ${informe.tiempoSecuencial} ms")
            appendLine("- Tiempo concurrente total: ${informe.tiempoConcurrente} ms")
            appendLine()
            appendLine("## Despensa analizada")
            appendLine()
            if (productos.isEmpty()) {
                appendLine("No hay productos guardados.")
            } else {
                appendLine("| Producto | Categoria | Cantidad | Dias restantes | Estado |")
                appendLine("|---|---|---:|---:|---|")
                productos.sortedBy { producto -> producto.diasRestantes() }.forEach { producto ->
                    appendLine(
                        "| ${producto.nombre} | ${producto.categoria} | ${producto.cantidad} | " +
                            "${producto.diasRestantes()} | ${estadoProducto(producto)} |"
                    )
                }
            }
            appendLine()
            appendLine("## Salida visible del informe")
            appendLine()
            appendLine("```text")
            appendLine(informe.textoResumen)
            appendLine("```")
            appendLine()
            appendLine("## Procesos y comunicacion")
            appendLine()
            appendLine("```text")
            appendLine(informe.detallesProcesos.trim())
            appendLine("```")
            appendLine()
            appendLine("## Hilos, corrutinas y canal")
            appendLine()
            appendLine("```text")
            appendLine(informe.detallesHilos.trim())
            appendLine("```")
            appendLine()
            appendLine("## Comparacion secuencial y concurrente")
            appendLine()
            appendLine("| Modo | Tiempo |")
            appendLine("|---|---:|")
            appendLine("| Secuencial | ${informe.tiempoSecuencial} ms |")
            appendLine("| Concurrente | ${informe.tiempoConcurrente} ms |")
            appendLine()
            appendLine("## Errores controlados")
            appendLine()
            appendLine("Los apartados anteriores incluyen las salidas de error controlado de procesos y tareas concurrentes. La app captura esos fallos y los convierte en resultados visibles sin cerrarse.")
            appendLine()
            appendLine("## Conclusiones")
            appendLine()
            appendLine("- La despensa se exporta a CSV y se procesa sin bloquear la interfaz.")
            appendLine("- Las tareas secuenciales y concurrentes registran tiempos comparables.")
            appendLine("- La comunicacion interna mediante archivo y canal queda reflejada en las salidas.")
            appendLine("- Los errores esperados se gestionan como resultados controlados.")
            if (caducados.isNotEmpty()) {
                appendLine("- Hay productos caducados que conviene revisar antes de planificar recetas o compras.")
            } else if (proximos.isNotEmpty()) {
                appendLine("- Hay productos proximos a caducar que conviene priorizar.")
            } else {
                appendLine("- No hay alertas urgentes de caducidad en la despensa actual.")
            }
        }
    }

    private fun estadoProducto(producto: Producto): String {
        return when {
            producto.estaCaducado() -> "Caducado"
            producto.estaProximoACaducar() -> "Proximo"
            else -> "Correcto"
        }
    }
}
