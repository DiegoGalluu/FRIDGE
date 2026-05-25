package com.example.fridge.modelo

data class ResultadoTarea(
    val nombre: String,
    val salida: String,
    val estado: String,
    val tiempoMs: Long,
    val correcto: Boolean
)
