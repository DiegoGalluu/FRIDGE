package com.example.fridge.recetas

import com.example.fridge.modelo.IngredienteReceta
import com.example.fridge.modelo.Producto
import com.example.fridge.modelo.RecetaSugerida
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.Normalizer

object SpoonacularServicio {
    fun prepararIngredientes(productos: List<Producto>): List<IngredienteReceta> {
        return productos
            .filter { producto -> producto.cantidad > 0 }
            .sortedWith(
                compareByDescending<Producto> { producto -> producto.estaProximoACaducar() }
                    .thenBy { producto -> producto.diasRestantes() }
                    .thenByDescending { producto -> producto.cantidad }
                    .thenBy { producto -> producto.nombre }
            )
            .map { producto ->
                IngredienteReceta(
                    nombreOriginal = producto.nombre,
                    nombreBusqueda = traducirIngrediente(producto.nombre),
                    cantidad = producto.cantidad,
                    proximoACaducar = producto.estaProximoACaducar(),
                    diasRestantes = producto.diasRestantes()
                )
            }
            .distinctBy { ingrediente -> ingrediente.nombreBusqueda.lowercase() }
            .take(10)
    }

    fun buscarRecetas(productos: List<Producto>, apiKey: String): List<RecetaSugerida> {
        val ingredientes = prepararIngredientes(productos)
        require(apiKey.isNotBlank()) { "Introduce una API key de Spoonacular." }
        require(ingredientes.isNotEmpty()) { "Añade productos a la despensa para buscar recetas." }

        val ingredientesParametro = ingredientes.joinToString(",") { ingrediente -> ingrediente.nombreBusqueda }
        val url = URL(
            "https://api.spoonacular.com/recipes/findByIngredients" +
                "?ingredients=${codificar(ingredientesParametro)}" +
                "&number=6" +
                "&ranking=1" +
                "&ignorePantry=true" +
                "&apiKey=${codificar(apiKey.trim())}"
        )

        val conexion = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 12000
            readTimeout = 12000
        }

        return try {
            val codigo = conexion.responseCode
            val cuerpo = if (codigo in 200..299) {
                conexion.inputStream.bufferedReader().readText()
            } else {
                conexion.errorStream?.bufferedReader()?.readText().orEmpty()
            }

            if (codigo !in 200..299) {
                throw IllegalStateException("Spoonacular respondió con código $codigo. $cuerpo")
            }

            JSONArray(cuerpo).toRecetas()
        } finally {
            conexion.disconnect()
        }
    }

    private fun JSONArray.toRecetas(): List<RecetaSugerida> {
        val recetas = mutableListOf<RecetaSugerida>()
        for (indice in 0 until length()) {
            val objeto = getJSONObject(indice)
            recetas.add(
                RecetaSugerida(
                    id = objeto.optInt("id"),
                    titulo = objeto.optString("title", "Receta sin título"),
                    ingredientesUsados = objeto.optJSONArray("usedIngredients").nombresIngredientes(),
                    ingredientesFaltantes = objeto.optJSONArray("missedIngredients").nombresIngredientes(),
                    ingredientesSinUsar = objeto.optJSONArray("unusedIngredients").nombresIngredientes()
                )
            )
        }
        return recetas
    }

    private fun JSONArray?.nombresIngredientes(): List<String> {
        if (this == null) return emptyList()
        val nombres = mutableListOf<String>()
        for (indice in 0 until length()) {
            val nombre = getJSONObject(indice).optString("name")
            if (nombre.isNotBlank()) nombres.add(nombre.replaceFirstChar { letra -> letra.uppercase() })
        }
        return nombres
    }

    private fun codificar(valor: String): String {
        return URLEncoder.encode(valor, "UTF-8")
    }

    private fun traducirIngrediente(nombre: String): String {
        val normalizado = Normalizer.normalize(nombre.trim().lowercase(), Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")
        return when (normalizado) {
            "leche" -> "milk"
            "tomate", "tomates" -> "tomato"
            "pollo" -> "chicken"
            "arroz" -> "rice"
            "queso" -> "cheese"
            "huevo", "huevos" -> "egg"
            "patata", "patatas" -> "potato"
            "cebolla", "cebollas" -> "onion"
            "zanahoria", "zanahorias" -> "carrot"
            "manzana", "manzanas" -> "apple"
            "pasta" -> "pasta"
            "pan" -> "bread"
            "atun" -> "tuna"
            "salmon" -> "salmon"
            "yogur", "yogurt" -> "yogurt"
            "lechuga" -> "lettuce"
            "ajo" -> "garlic"
            "pimiento", "pimientos" -> "bell pepper"
            else -> normalizado
        }
    }
}
