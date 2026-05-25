package com.example.fridge.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fridge.modelo.Producto
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaNuevoProducto(
    onGuardar: (Producto) -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nombreProducto by remember { mutableStateOf("") }
    var cantidadProducto by remember { mutableStateOf("") }
    var categoriaProducto by remember { mutableStateOf("Lácteos") }
    var fechaCaducidadMillis by remember { mutableStateOf<Long?>(null) }
    var mostrarCalendario by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    val categorias = listOf("Lácteos", "Carne", "Pescado", "Verdura", "Fruta", "Bebida", "Despensa", "Otro")
    val fechaCaducidad = fechaCaducidadMillis?.let { millis -> fechaDesdeMillis(millis) }
    val diasCaducidad = fechaCaducidad?.let { fecha -> diasHastaCaducidad(fecha) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TituloSeccion("Añadir producto")

        OutlinedTextField(
            value = nombreProducto,
            onValueChange = { nombreProducto = it },
            label = { Text("Nombre del producto") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = cantidadProducto,
            onValueChange = { cantidadProducto = it },
            label = { Text("Cantidad") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Text("Categoría")
        categorias.chunked(2).forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                fila.forEach { categoria ->
                    if (categoriaProducto == categoria) {
                        Button(onClick = { categoriaProducto = categoria }, modifier = Modifier.weight(1f)) {
                            Text(categoria)
                        }
                    } else {
                        OutlinedButton(onClick = { categoriaProducto = categoria }, modifier = Modifier.weight(1f)) {
                            Text(categoria)
                        }
                    }
                }
                if (fila.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }

        Text("Fecha de caducidad")
        OutlinedButton(
            onClick = { mostrarCalendario = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.DateRange, contentDescription = "calendario")
                Text(fechaCaducidad?.let { fecha -> "Caduca el ${formatearFecha(fecha)}" } ?: "Elegir fecha")
            }
        }

        if (diasCaducidad != null) {
            Text(describirDiasCaducidad(diasCaducidad))
        }

        if (mensajeError != null) {
            Text(text = mensajeError ?: "", color = androidx.compose.ui.graphics.Color(0xFFC62828))
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = {
                val nombreLimpio = nombreProducto.trim()
                val cantidad = cantidadProducto.toIntOrNull() ?: 1

                mensajeError = when {
                    nombreLimpio.isBlank() -> "El nombre es obligatorio"
                    cantidad <= 0 -> "La cantidad debe ser positiva"
                    diasCaducidad == null -> "Elige una fecha de caducidad"
                    else -> null
                }

                if (mensajeError == null) {
                    onGuardar(
                        Producto(
                            nombre = nombreLimpio,
                            cantidad = cantidad,
                            categoria = categoriaProducto,
                            diasCaducidad = diasCaducidad ?: 0
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }

        OutlinedButton(onClick = onCancelar, modifier = Modifier.fillMaxWidth()) {
            Text("Cancelar")
        }
    }

    if (mostrarCalendario) {
        val estadoCalendario = rememberDatePickerState(initialSelectedDateMillis = fechaCaducidadMillis)
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        fechaCaducidadMillis = estadoCalendario.selectedDateMillis
                        mostrarCalendario = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = estadoCalendario)
        }
    }
}

private val FormatoFechaCaducidad: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

private fun fechaDesdeMillis(millis: Long): LocalDate {
    return Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
}

private fun formatearFecha(fecha: LocalDate): String {
    return fecha.format(FormatoFechaCaducidad)
}

private fun diasHastaCaducidad(fecha: LocalDate): Int {
    return ChronoUnit.DAYS.between(LocalDate.now(), fecha).toInt()
}

private fun describirDiasCaducidad(dias: Int): String {
    return when {
        dias < 0 -> "Caducado hace ${abs(dias)} días"
        dias == 0 -> "Caduca hoy"
        dias == 1 -> "Caduca en 1 día"
        else -> "Caduca en $dias días"
    }
}
