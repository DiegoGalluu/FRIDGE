package com.example.fridge.modelo

import org.json.JSONObject
import java.util.UUID

data class ItemCompra(
    val id: String = UUID.randomUUID().toString(),
    val nombre: String
) {
    // convierte el item a json para guardarlo
    fun aJson(): JSONObject {
        return JSONObject()
            .put("id", id)
            .put("nombre", nombre)
    }

    companion object {
        // crea un item desde json con control de errores
        fun desdeJson(objeto: JSONObject): ItemCompra? {
            return try {
                ItemCompra(
                    id = objeto.optString("id", UUID.randomUUID().toString()),
                    nombre = objeto.getString("nombre")
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
