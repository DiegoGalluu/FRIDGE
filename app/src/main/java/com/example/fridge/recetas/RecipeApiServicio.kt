package com.example.fridge.recetas

import com.example.fridge.modelo.IngredienteReceta
import com.example.fridge.modelo.Producto
import com.example.fridge.modelo.RecetaSugerida
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.Normalizer

object RecipeApiServicio {
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

    fun buscarRecetas(productos: List<Producto>): List<RecetaSugerida> {
        val ingredientes = prepararIngredientes(productos)
        val apiKey = RecipeApiConfig.API_KEY
        require(apiKey.isNotBlank()) { "La búsqueda de recetas no está configurada todavía." }
        require(ingredientes.isNotEmpty()) { "Añade productos a la despensa para buscar recetas." }

        val ingredientesParametro = ingredientes.joinToString(",") { ingrediente -> ingrediente.nombreBusqueda }
        val url = URL(
            "https://recipeapi.io/api/v1/recipes" +
                "?ingredients=${codificar(ingredientesParametro)}" +
                "&per_page=6"
        )

        val conexion = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 12000
            readTimeout = 12000
            setRequestProperty("Authorization", "Bearer ${apiKey.trim()}")
        }

        return try {
            val codigo = conexion.responseCode
            val cuerpo = if (codigo in 200..299) {
                conexion.inputStream.bufferedReader().readText()
            } else {
                conexion.errorStream?.bufferedReader()?.readText().orEmpty()
            }

            if (codigo !in 200..299) {
                throw IllegalStateException(mensajeError(codigo, cuerpo))
            }

            JSONObject(cuerpo).optJSONArray("data").toRecetas(ingredientes)
        } finally {
            conexion.disconnect()
        }
    }

    private fun JSONArray?.toRecetas(ingredientesDisponibles: List<IngredienteReceta>): List<RecetaSugerida> {
        if (this == null) return emptyList()

        val recetas = mutableListOf<RecetaSugerida>()
        for (indice in 0 until length()) {
            val objeto = getJSONObject(indice)
            val ingredientesReceta = objeto.optJSONArray("ingredients").nombresIngredientes()
            val disponiblesNormalizados = ingredientesDisponibles.map { ingrediente ->
                ingrediente.nombreBusqueda.normalizar()
            }
            val usados = ingredientesReceta.filter { nombre ->
                val normalizado = nombre.normalizar()
                disponiblesNormalizados.any { disponible ->
                    normalizado.contains(disponible) || disponible.contains(normalizado)
                }
            }
            val usadosNormalizados = usados.map { nombre -> nombre.normalizar() }
            val sinUsar = ingredientesDisponibles
                .filter { ingrediente ->
                    usadosNormalizados.none { usado ->
                        usado.contains(ingrediente.nombreBusqueda.normalizar()) ||
                            ingrediente.nombreBusqueda.normalizar().contains(usado)
                    }
                }
                .map { ingrediente -> ingrediente.nombreOriginal.formatearNombre() }
            val faltantes = ingredientesReceta
                .filterNot { nombre -> usados.contains(nombre) }
                .take(8)

            recetas.add(
                RecetaSugerida(
                    id = objeto.optInt("id"),
                    titulo = objeto.optString("name", "Receta sin título"),
                    ingredientesUsados = usados,
                    ingredientesFaltantes = faltantes,
                    ingredientesSinUsar = sinUsar
                )
            )
        }
        return recetas
    }

    private fun JSONArray?.nombresIngredientes(): List<String> {
        if (this == null) return emptyList()
        val nombres = mutableListOf<String>()
        for (indice in 0 until length()) {
            val item = getJSONObject(indice)
            val nombre = item.optString("name")
            if (nombre.isNotBlank()) nombres.add(nombre.formatearNombre())
        }
        return nombres
    }

    private fun mensajeError(codigo: Int, cuerpo: String): String {
        val mensajeApi = runCatching {
            JSONObject(cuerpo).optJSONObject("error")?.optString("message")
        }.getOrNull()
        return if (mensajeApi.isNullOrBlank()) {
            "RecipeAPI.io respondió con código $codigo."
        } else {
            "RecipeAPI.io respondió con código $codigo. $mensajeApi"
        }
    }

    private fun codificar(valor: String): String {
        return URLEncoder.encode(valor, "UTF-8")
    }

    private fun traducirIngrediente(nombre: String): String {
        val normalizado = nombre.normalizar()
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
