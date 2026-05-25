package com.example.fridge.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fridge.modelo.ItemCompra

@Composable
fun PantallaCompra(
    compra: List<ItemCompra>,
    onAnadirItem: (String) -> Unit,
    onEliminarItem: (ItemCompra, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var nombreItem by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var itemSeleccionado by remember { mutableStateOf<ItemCompra?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TituloSeccion("lista de la compra")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = nombreItem,
                onValueChange = { nombreItem = it },
                label = { Text("producto") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                val nombreLimpio = nombreItem.trim()
                if (nombreLimpio.isBlank()) {
                    mensajeError = "escribe un producto"
                } else {
                    mensajeError = null
                    onAnadirItem(nombreLimpio)
                    nombreItem = ""
                }
            }) {
                Text("anadir")
            }
        }

        if (mensajeError != null) Text(mensajeError ?: "")

        if (compra.isEmpty()) {
            Text("no hay productos en la lista")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(compra, key = { item -> item.id }) { item ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { itemSeleccionado = item }
                    ) {
                        Text(
                            text = item.nombre,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }

    val item = itemSeleccionado
    if (item != null) {
        AlertDialog(
            onDismissRequest = { itemSeleccionado = null },
            title = { Text(item.nombre) },
            text = { Text("elige que quieres hacer") },
            confirmButton = {
                TextButton(onClick = {
                    itemSeleccionado = null
                    onEliminarItem(item, true)
                }) { Text("comprado") }
            },
            dismissButton = {
                Column {
                    TextButton(onClick = {
                        itemSeleccionado = null
                        onEliminarItem(item, false)
                    }) { Text("eliminar") }
                    TextButton(onClick = { itemSeleccionado = null }) { Text("cancelar") }
                }
            }
        )
    }
}
