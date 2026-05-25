package com.example.fridge.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object UtilFechas {
    // formatea una fecha para mostrarla en pantalla
    fun formatearFecha(fecha: Long): String {
        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return formato.format(Date(fecha))
    }
}
