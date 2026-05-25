package com.example.fridge.datos

import android.content.Context
import com.example.fridge.modelo.InformeDespensa
import com.example.fridge.modelo.ItemCompra
import com.example.fridge.modelo.Producto
import org.json.JSONArray

object AlmacenDatos {
    private const val NOMBRE_PREFS = "fridge_datos"
    private const val CLAVE_PRODUCTOS = "productos"
    private const val CLAVE_COMPRA = "compra"
    private const val CLAVE_INFORMES = "informes"
    private const val CLAVE_SPOONACULAR_API_KEY = "spoonacular_api_key"

    // obtiene las preferencias de android
    private fun prefs(context: Context) = context.getSharedPreferences(NOMBRE_PREFS, Context.MODE_PRIVATE)

    // guarda todos los productos en formato json
    fun guardarProductos(context: Context, productos: List<Producto>) {
        val array = JSONArray()
        productos.forEach { producto -> array.put(producto.aJson()) }
        prefs(context).edit().putString(CLAVE_PRODUCTOS, array.toString()).apply()
    }

    // carga productos desde sharedpreferences
    fun obtenerProductos(context: Context): List<Producto> {
        val texto = prefs(context).getString(CLAVE_PRODUCTOS, "[]") ?: "[]"
        val array = JSONArray(texto)
        val lista = mutableListOf<Producto>()
        for (indice in 0 until array.length()) {
            val producto = Producto.desdeJson(array.getJSONObject(indice))
            if (producto != null) lista.add(producto)
        }
        return lista
    }

    // guarda la lista de la compra en formato json
    fun guardarCompra(context: Context, compra: List<ItemCompra>) {
        val array = JSONArray()
        compra.forEach { item -> array.put(item.aJson()) }
        prefs(context).edit().putString(CLAVE_COMPRA, array.toString()).apply()
    }

    // carga la lista de la compra guardada
    fun obtenerCompra(context: Context): List<ItemCompra> {
        val texto = prefs(context).getString(CLAVE_COMPRA, "[]") ?: "[]"
        val array = JSONArray(texto)
        val lista = mutableListOf<ItemCompra>()
        for (indice in 0 until array.length()) {
            val item = ItemCompra.desdeJson(array.getJSONObject(indice))
            if (item != null) lista.add(item)
        }
        return lista
    }

    // guarda los informes generados por el usuario
    fun guardarInformes(context: Context, informes: List<InformeDespensa>) {
        val array = JSONArray()
        informes.forEach { informe -> array.put(informe.aJson()) }
        prefs(context).edit().putString(CLAVE_INFORMES, array.toString()).apply()
    }

    // carga los informes guardados
    fun obtenerInformes(context: Context): List<InformeDespensa> {
        val texto = prefs(context).getString(CLAVE_INFORMES, "[]") ?: "[]"
        val array = JSONArray(texto)
        val lista = mutableListOf<InformeDespensa>()
        for (indice in 0 until array.length()) {
            val informe = InformeDespensa.desdeJson(array.getJSONObject(indice))
            if (informe != null) lista.add(informe)
        }
        return lista
    }

    fun guardarSpoonacularApiKey(context: Context, apiKey: String) {
        prefs(context).edit().putString(CLAVE_SPOONACULAR_API_KEY, apiKey).apply()
    }

    fun obtenerSpoonacularApiKey(context: Context): String {
        return prefs(context).getString(CLAVE_SPOONACULAR_API_KEY, "") ?: ""
    }

    // borra todo para poder repetir la demostracion
    fun borrarTodo(context: Context) {
        prefs(context).edit().clear().apply()
    }
}
