package com.example.fridge.modelo

data class RecetaSugerida(
    val id: Int,
    val titulo: String,
    val ingredientesUsados: List<String>,
    val ingredientesFaltantes: List<String>,
    val ingredientesSinUsar: List<String>
) {
    val totalUsados: Int get() = ingredientesUsados.size
    val totalFaltantes: Int get() = ingredientesFaltantes.size
}

data class IngredienteReceta(
    val nombreOriginal: String,
    val nombreBusqueda: String,
    val cantidad: Int,
    val proximoACaducar: Boolean,
    val diasRestantes: Int
)
