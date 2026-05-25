package com.example.fridge.procesamiento

import android.content.Context
import com.example.fridge.modelo.Producto
import java.io.File

object ExportadorDespensa {
    // crea un archivo csv interno con la despensa
    fun exportar(context: Context, productos: List<Producto>): File {
        val archivo = File(context.filesDir, "despensa_exportada.csv")
        val texto = buildString {
            appendLine("nombre,categoria,cantidad,dias")
            productos.forEach { producto ->
                appendLine("${producto.nombre},${producto.categoria},${producto.cantidad},${producto.diasRestantes()}")
            }
        }
        archivo.writeText(texto)
        return archivo
    }
}
