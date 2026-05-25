package com.example.fridge.modelo

import org.json.JSONObject
import java.util.UUID

data class InformeDespensa(
    val id: String = UUID.randomUUID().toString(),
    val fecha: Long = System.currentTimeMillis(),
    val totalProductos: Int,
    val productosProximos: Int,
    val productosCaducados: Int,
    val textoResumen: String,
    val tiempoSecuencial: Long,
    val tiempoConcurrente: Long,
    val detallesProcesos: String,
    val detallesHilos: String,
    val rutaResultados: String = ""
) {
    // convierte el informe a json para guardarlo
    fun aJson(): JSONObject {
        return JSONObject()
            .put("id", id)
            .put("fecha", fecha)
            .put("totalProductos", totalProductos)
            .put("productosProximos", productosProximos)
            .put("productosCaducados", productosCaducados)
            .put("textoResumen", textoResumen)
            .put("tiempoSecuencial", tiempoSecuencial)
            .put("tiempoConcurrente", tiempoConcurrente)
            .put("detallesProcesos", detallesProcesos)
            .put("detallesHilos", detallesHilos)
            .put("rutaResultados", rutaResultados)
    }

    companion object {
        // crea un informe desde json con control de errores
        fun desdeJson(objeto: JSONObject): InformeDespensa? {
            return try {
                InformeDespensa(
                    id = objeto.optString("id", UUID.randomUUID().toString()),
                    fecha = objeto.optLong("fecha", System.currentTimeMillis()),
                    totalProductos = objeto.optInt("totalProductos", 0),
                    productosProximos = objeto.optInt(
                        "productosProximos",
                        objeto.optInt("productosUrgentes", 0)
                    ),
                    productosCaducados = objeto.optInt("productosCaducados", 0),
                    textoResumen = objeto.optString("textoResumen", "sin resumen"),
                    tiempoSecuencial = objeto.optLong("tiempoSecuencial", 0L),
                    tiempoConcurrente = objeto.optLong("tiempoConcurrente", 0L),
                    detallesProcesos = objeto.optString("detallesProcesos", ""),
                    detallesHilos = objeto.optString("detallesHilos", ""),
                    rutaResultados = objeto.optString("rutaResultados", "")
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
