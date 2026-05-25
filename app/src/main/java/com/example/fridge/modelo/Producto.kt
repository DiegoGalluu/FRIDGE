package com.example.fridge.modelo

import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.math.abs

data class Producto(
    val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val cantidad: Int,
    val categoria: String,
    val diasCaducidad: Int,
    val fechaCreacion: Long = System.currentTimeMillis()
) {
    // calcula los dias que quedan desde la fecha en que se guardo
    fun diasRestantes(): Int {
        val ahora = System.currentTimeMillis()
        val diasPasados = TimeUnit.MILLISECONDS.toDays(ahora - fechaCreacion).toInt()
        return diasCaducidad - diasPasados
    }

    // indica si el producto ya esta caducado
    fun estaCaducado(): Boolean = diasRestantes() < 0

    // indica si el producto esta proximo a caducar
    fun estaProximoACaducar(): Boolean = diasRestantes() in 0..3

    // devuelve un texto facil para el usuario
    fun textoCaducidad(): String {
        val dias = diasRestantes()
        return when {
            dias < 0 -> "Caducado hace ${abs(dias)} días"
            dias == 0 -> "Caduca hoy"
            dias == 1 -> "Caduca en 1 día"
            else -> "Caduca en $dias días"
        }
    }

    // convierte el producto a json para guardarlo
    fun aJson(): JSONObject {
        return JSONObject()
            .put("id", id)
            .put("nombre", nombre)
            .put("cantidad", cantidad)
            .put("categoria", categoria)
            .put("diasCaducidad", diasCaducidad)
            .put("fechaCreacion", fechaCreacion)
    }

    companion object {
        // crea un producto desde json con control de errores
        fun desdeJson(objeto: JSONObject): Producto? {
            return try {
                Producto(
                    id = objeto.optString("id", UUID.randomUUID().toString()),
                    nombre = objeto.getString("nombre"),
                    cantidad = objeto.optInt("cantidad", 1),
                    categoria = objeto.optString("categoria", "otro"),
                    diasCaducidad = objeto.optInt("diasCaducidad", 7),
                    fechaCreacion = objeto.optLong("fechaCreacion", System.currentTimeMillis())
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
