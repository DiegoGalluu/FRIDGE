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
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fridge.modelo.Producto

@Composable
fun PantallaNuevoProducto(
    onGuardar: (Producto) -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nombreProducto by remember { mutableStateOf("") }
    var cantidadProducto by remember { mutableStateOf("") }
    var categoriaProducto by remember { mutableStateOf("lacteos") }
    var diasCaducidad by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    val categorias = listOf("lacteos", "carne", "pescado", "verdura", "fruta", "bebida", "despensa", "otro")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TituloSeccion("anadir producto")

        OutlinedTextField(
            value = nombreProducto,
            onValueChange = { nombreProducto = it },
            label = { Text("nombre del producto") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = cantidadProducto,
            onValueChange = { cantidadProducto = it },
            label = { Text("cantidad") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Text("categoria")
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

        OutlinedTextField(
            value = diasCaducidad,
            onValueChange = { diasCaducidad = it },
            label = { Text("caduca en dias") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        if (mensajeError != null) {
            Text(text = mensajeError ?: "", color = androidx.compose.ui.graphics.Color(0xFFC62828))
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = {
                val nombreLimpio = nombreProducto.trim()
                val cantidad = cantidadProducto.toIntOrNull() ?: 1
                val dias = diasCaducidad.toIntOrNull() ?: 7

                mensajeError = when {
                    nombreLimpio.isBlank() -> "el nombre es obligatorio"
                    cantidad <= 0 -> "la cantidad debe ser positiva"
                    dias < 0 -> "los dias no pueden ser negativos"
                    else -> null
                }

                if (mensajeError == null) {
                    onGuardar(
                        Producto(
                            nombre = nombreLimpio,
                            cantidad = cantidad,
                            categoria = categoriaProducto,
                            diasCaducidad = dias
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("guardar")
        }

        OutlinedButton(onClick = onCancelar, modifier = Modifier.fillMaxWidth()) {
            Text("cancelar")
        }
    }
}
