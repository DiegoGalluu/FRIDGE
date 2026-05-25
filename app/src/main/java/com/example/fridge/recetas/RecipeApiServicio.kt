package com.example.fridge.recetas

import com.example.fridge.modelo.IngredienteReceta
import com.example.fridge.modelo.DetalleReceta
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
            .take(30)
    }

    fun buscarRecetas(productos: List<Producto>): List<RecetaSugerida> {
        val ingredientes = prepararIngredientes(productos)
        val apiKey = RecipeApiConfig.API_KEY
        require(apiKey.isNotBlank()) { "La búsqueda de recetas con Spoonacular no está configurada todavía." }
        require(ingredientes.isNotEmpty()) { "Añade productos a la despensa para buscar recetas." }

        val ingredientesBusqueda = TraductorRecetas.traducirIngredientesParaBusqueda(ingredientes.take(20))
        val prioritariosBusqueda = ingredientesBusqueda.filter { ingrediente -> ingrediente.proximoACaducar }
        val masUrgentes = prioritariosBusqueda
            .minOfOrNull { ingrediente -> ingrediente.diasRestantes }
            ?.let { dias -> prioritariosBusqueda.filter { ingrediente -> ingrediente.diasRestantes == dias } }
            .orEmpty()
        val ingredientesParametro = ingredientesBusqueda.joinToString(",") { ingrediente -> ingrediente.nombreBusqueda }
        val url = URL(
            "https://api.spoonacular.com/recipes/findByIngredients" +
                "?ingredients=${codificar(ingredientesParametro)}" +
                "&number=20" +
                "&ranking=2" +
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
                throw IllegalStateException(mensajeError(codigo, cuerpo))
            }

            val recetas = JSONArray(cuerpo)
                .toRecetas()
                .filterNot { receta -> receta.esMenuDeCelebracion() }
                .filter { receta -> receta.totalUsados > 0 }
                .priorizarIngredientesCaducidad(prioritariosBusqueda)
                .sortedWith(
                    compareByDescending<RecetaSugerida> { receta -> receta.usaIngredienteDe(masUrgentes) }
                        .thenByDescending { receta -> receta.puntuacionPrioridad(prioritariosBusqueda) }
                        .thenByDescending { receta -> receta.totalUsados }
                        .thenBy { receta -> receta.totalFaltantes }
                        .thenBy { receta -> receta.titulo }
                )
                .take(6)

            TraductorRecetas.traducirRecetas(recetas)
        } finally {
            conexion.disconnect()
        }
    }

    fun obtenerDetalleReceta(id: Int): DetalleReceta {
        val apiKey = RecipeApiConfig.API_KEY
        require(apiKey.isNotBlank()) { "La búsqueda de recetas con Spoonacular no está configurada todavía." }

        val url = URL(
            "https://api.spoonacular.com/recipes/$id/information" +
                "?includeNutrition=false" +
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
                throw IllegalStateException(mensajeError(codigo, cuerpo))
            }

            val objeto = JSONObject(cuerpo)
            TraductorRecetas.traducirDetalle(
                DetalleReceta(
                    titulo = objeto.optString("title", "Receta"),
                    ingredientes = objeto.optJSONArray("extendedIngredients").nombresIngredientesDetalle(),
                    pasos = objeto.extraerPasos(),
                    resumen = objeto.optString("summary").limpiarHtml()
                )
            )
        } finally {
            conexion.disconnect()
        }
    }

    private fun JSONArray?.toRecetas(): List<RecetaSugerida> {
        if (this == null) return emptyList()

        val recetas = mutableListOf<RecetaSugerida>()
        for (indice in 0 until length()) {
            val objeto = getJSONObject(indice)
            recetas.add(
                RecetaSugerida(
                    id = objeto.optInt("id"),
                    titulo = objeto.optString("title", "Receta sin título"),
                    ingredientesUsados = objeto.optJSONArray("usedIngredients").nombresIngredientesSpoonacular(),
                    ingredientesFaltantes = objeto.optJSONArray("missedIngredients").nombresIngredientesSpoonacular(),
                    ingredientesSinUsar = emptyList()
                )
            )
        }
        return recetas
    }

    private fun JSONArray?.nombresIngredientesSpoonacular(): List<String> {
        if (this == null) return emptyList()
        val nombres = mutableListOf<String>()
        for (indice in 0 until length()) {
            val item = getJSONObject(indice)
            val nombre = item.optString("originalName")
                .ifBlank { item.optString("name") }
                .ifBlank { item.optString("original") }
            if (nombre.isNotBlank()) nombres.add(nombre.formatearNombre())
        }
        return nombres
    }

    private fun JSONArray?.nombresIngredientesDetalle(): List<String> {
        if (this == null) return emptyList()
        val nombres = mutableListOf<String>()
        for (indice in 0 until length()) {
            val item = getJSONObject(indice)
            val nombre = item.optString("original")
                .ifBlank { item.optString("originalName") }
                .ifBlank { item.optString("name") }
            if (nombre.isNotBlank()) nombres.add(nombre.formatearNombre())
        }
        return nombres
    }

    private fun JSONObject.extraerPasos(): List<String> {
        val analizados = optJSONArray("analyzedInstructions")
        if (analizados != null && analizados.length() > 0) {
            val pasosJson = analizados.optJSONObject(0)?.optJSONArray("steps")
            if (pasosJson != null) {
                val pasos = mutableListOf<String>()
                for (indice in 0 until pasosJson.length()) {
                    val paso = pasosJson.optJSONObject(indice)?.optString("step").orEmpty().limpiarHtml()
                    if (paso.isNotBlank()) pasos.add(paso)
                }
                if (pasos.isNotEmpty()) return pasos
            }
        }

        val instrucciones = optString("instructions").limpiarHtml()
        return instrucciones
            .split(". ")
            .map { paso -> paso.trim() }
            .filter { paso -> paso.isNotBlank() }
    }

    private fun mensajeError(codigo: Int, cuerpo: String): String {
        val mensajeApi = runCatching {
            val error = JSONObject(cuerpo)
            listOfNotNull(
                error.optString("code").takeIf { codigoError -> codigoError.isNotBlank() },
                error.optString("status").takeIf { estado -> estado.isNotBlank() },
                error.optString("message").takeIf { mensaje -> mensaje.isNotBlank() }
            ).joinToString(": ")
        }.getOrNull()
        return if (mensajeApi.isNullOrBlank()) {
            "Spoonacular respondió con código $codigo."
        } else {
            "Spoonacular respondió con código $codigo. $mensajeApi"
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
            "sal" -> "salt"
            "pimienta" -> "black pepper"
            "aceite", "aceite de oliva" -> "olive oil"
            "mantequilla" -> "butter"
            "harina" -> "flour"
            "azucar", "azúcar" -> "sugar"
            "limon", "limón", "limones" -> "lemon"
            "perejil" -> "parsley"
            "oregano", "orégano" -> "oregano"
            "albahaca" -> "basil"
            "comino" -> "cumin"
            "pimenton", "pimentón" -> "paprika"
            "calabacin", "calabacín" -> "zucchini"
            "berenjena", "berenjenas" -> "eggplant"
            "champiñon", "champiñón", "champiñones" -> "mushrooms"
            "brocoli", "brócoli" -> "broccoli"
            "espinacas" -> "spinach"
            "guisantes" -> "peas"
            "garbanzos" -> "chickpeas"
            "lentejas" -> "lentils"
            "ternera" -> "beef"
            "cerdo" -> "pork"
            "jamon", "jamón" -> "ham"
            "bacon", "beicon" -> "bacon"
            "nata" -> "cream"
            "yogur griego" -> "greek yogurt"
            else -> normalizado
        }
    }

    private fun String.normalizar(): String {
        return Normalizer.normalize(trim().lowercase(), Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")
    }

    private fun RecetaSugerida.esMenuDeCelebracion(): Boolean {
        val tituloNormalizado = titulo.normalizar()
        val palabrasBloqueadas = listOf(
            "menu",
            "feast",
            "celebration",
            "banquet",
            "buffet",
            "bautizo",
            "baptism",
            "nochebuena",
            "fiesta",
            "celebracion",
            "banquete",
            "christmas eve",
            "complete menu",
            "full feast"
        )
        return palabrasBloqueadas.any { palabra -> tituloNormalizado.contains(palabra) }
    }

    private fun List<RecetaSugerida>.priorizarIngredientesCaducidad(
        prioritarios: List<IngredienteReceta>
    ): List<RecetaSugerida> {
        if (prioritarios.isEmpty()) return this
        val conPrioritarios = filter { receta -> receta.usaIngredienteDe(prioritarios) }
        return conPrioritarios.ifEmpty { this }
    }

    private fun RecetaSugerida.puntuacionPrioridad(prioritarios: List<IngredienteReceta>): Int {
        return prioritarios.sumOf { ingrediente ->
            if (usaIngrediente(ingrediente)) {
                1000 - (ingrediente.diasRestantes.coerceAtLeast(0) * 100)
            } else {
                0
            }
        }
    }

    private fun RecetaSugerida.usaIngredienteDe(ingredientes: List<IngredienteReceta>): Boolean {
        return ingredientes.any { ingrediente -> usaIngrediente(ingrediente) }
    }

    private fun RecetaSugerida.usaIngrediente(ingrediente: IngredienteReceta): Boolean {
        val busqueda = ingrediente.nombreBusqueda.normalizar()
        val original = ingrediente.nombreOriginal.normalizar()
        return ingredientesUsados.any { usado ->
            val normalizado = usado.normalizar()
            normalizado.contienePalabra(busqueda) || normalizado.contienePalabra(original)
        }
    }

    private fun String.contienePalabra(palabra: String): Boolean {
        return "(^|[^a-z0-9])${Regex.escape(palabra)}([^a-z0-9]|$)"
            .toRegex()
            .containsMatchIn(this)
    }

    private fun String.formatearNombre(): String {
        return trim().replaceFirstChar { letra ->
            if (letra.isLowerCase()) letra.titlecase() else letra.toString()
        }
    }

    private fun String.limpiarHtml(): String {
        return replace("<[^>]+>".toRegex(), " ")
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("\\s+".toRegex(), " ")
            .trim()
    }
}
