package com.example.fridge.modelo

data class ResultadoProceso(
    val nombre: String,
    val comando: String,
    val salidaEstandar: String,
    val salidaError: String,
    val codigoSalida: Int,
    val tiempoMs: Long,
    val correcto: Boolean
)
