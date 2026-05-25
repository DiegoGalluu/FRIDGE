package com.example.fridge.recetas

import com.example.fridge.modelo.IngredienteReceta
import com.example.fridge.modelo.DetalleReceta
import com.example.fridge.modelo.RecetaSugerida
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.Normalizer

object TraductorRecetas {
    private const val SEPARADOR = "\n:::FRIDGE_ITEM:::\n"
    private val cache = mutableMapOf<String, String>()

    fun traducirIngredientesParaBusqueda(ingredientes: List<IngredienteReceta>): List<IngredienteReceta> {
        if (ingredientes.isEmpty()) return ingredientes

        val originales = ingredientes.map { ingrediente -> ingrediente.nombreOriginal }
        val traducciones = traducirLista(originales, origen = "es", destino = "en")

        return ingredientes.mapIndexed { indice, ingrediente ->
            val traducido = traducciones.getOrNull(indice)
                ?.normalizar()
                ?.takeIf { texto -> texto.isNotBlank() }
                ?: ingrediente.nombreBusqueda

            ingrediente.copy(nombreBusqueda = traducido)
        }
    }

    fun traducirRecetas(recetas: List<RecetaSugerida>): List<RecetaSugerida> {
        if (recetas.isEmpty()) return recetas

        val textos = recetas.flatMap { receta ->
            listOf(receta.titulo) + receta.ingredientesUsados + receta.ingredientesFaltantes
        }.distinct()
        val traducciones = textos.zip(traducirLista(textos, origen = "en", destino = "es")).toMap()

        return recetas.map { receta ->
            receta.copy(
                titulo = traducciones[receta.titulo]?.formatearNombre() ?: receta.titulo,
                ingredientesUsados = receta.ingredientesUsados.map { ingrediente ->
                    traducciones[ingrediente]?.formatearNombre() ?: ingrediente
                },
                ingredientesFaltantes = receta.ingredientesFaltantes.map { ingrediente ->
                    traducciones[ingrediente]?.formatearNombre() ?: ingrediente
                },
                ingredientesSinUsar = emptyList()
            )
        }
    }

    fun traducirDetalle(detalle: DetalleReceta): DetalleReceta {
        val textos = listOf(detalle.titulo, detalle.resumen) + detalle.ingredientes + detalle.pasos
        val traducciones = textos.zip(traducirLista(textos, origen = "en", destino = "es")).toMap()

        return detalle.copy(
            titulo = traducciones[detalle.titulo]?.formatearNombre() ?: detalle.titulo,
            ingredientes = detalle.ingredientes.map { ingrediente ->
                traducciones[ingrediente]?.formatearNombre() ?: ingrediente
            },
            pasos = detalle.pasos.map { paso -> traducciones[paso]?.formatearNombre() ?: paso },
            resumen = traducciones[detalle.resumen] ?: detalle.resumen
        )
    }

    private fun traducirLista(textos: List<String>, origen: String, destino: String): List<String> {
        val limpios = textos.map { texto -> texto.trim() }
        if (limpios.isEmpty()) return emptyList()

        val pendientes = limpios.filter { texto -> texto.isNotBlank() && cache["$origen-$destino:$texto"] == null }
        if (pendientes.isNotEmpty()) {
            val traducidos = traducirBloque(pendientes, origen, destino)
            pendientes.zip(traducidos).forEach { (original, traduccion) ->
                cache["$origen-$destino:$original"] = traduccion
            }
        }

        return limpios.map { texto -> cache["$origen-$destino:$texto"] ?: texto }
    }

    private fun traducirBloque(textos: List<String>, origen: String, destino: String): List<String> {
        return try {
            val combinado = textos.joinToString(SEPARADOR)
            val traducido = traducirTextoRemoto(combinado, origen, destino)
            val partes = traducido
                .split(":::FRIDGE_ITEM:::", "::: FRIDGE_ITEM :::", "FRIDGE_ITEM")
                .map { parte -> parte.trim() }
                .filter { parte -> parte.isNotBlank() }

            if (partes.size == textos.size) {
                partes
            } else {
                textos.map { texto -> traducirTextoRemoto(texto, origen, destino) }
            }
        } catch (e: Exception) {
            textos
        }
    }

    private fun traducirTextoRemoto(texto: String, origen: String, destino: String): String {
        val url = URL(
            "https://translate.googleapis.com/translate_a/single" +
                "?client=gtx" +
                "&sl=$origen" +
                "&tl=$destino" +
                "&dt=t" +
                "&q=${codificar(texto)}"
        )
        val conexion = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 6000
            readTimeout = 6000
        }

        return try {
            if (conexion.responseCode !in 200..299) return texto
            val cuerpo = conexion.inputStream.bufferedReader().readText()
            val segmentos = JSONArray(cuerpo).optJSONArray(0) ?: return texto
            buildString {
                for (indice in 0 until segmentos.length()) {
                    append(segmentos.optJSONArray(indice)?.optString(0).orEmpty())
                }
            }.trim().ifBlank { texto }
        } finally {
            conexion.disconnect()
        }
    }

    private fun codificar(valor: String): String {
        return URLEncoder.encode(valor, "UTF-8")
    }

    private fun String.normalizar(): String {
        return Normalizer.normalize(trim().lowercase(), Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")
    }

    private fun String.formatearNombre(): String {
        return trim().replaceFirstChar { letra ->
            if (letra.isLowerCase()) letra.titlecase() else letra.toString()
        }
    }
}
