package com.example.fridge.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fridge.modelo.Producto

@Composable
fun PantallaDespensa(
    productos: List<Producto>,
    onConsumirProducto: (Producto) -> Unit,
    onAnadirCompra: (Producto) -> Unit,
    onEliminarProducto: (Producto) -> Unit,
    modifier: Modifier = Modifier
) {
    var filtro by remember { mutableStateOf("todos") }
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }

    val productosFiltrados = when (filtro) {
        "pronto" -> productos.filter { producto -> producto.caducaPronto() }
        "caducados" -> productos.filter { producto -> producto.estaCaducado() }
        else -> productos
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TituloSeccion("mi despensa")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            BotonFiltro("todos", filtro == "todos", Modifier.weight(1f)) { filtro = "todos" }
            BotonFiltro("pronto", filtro == "pronto", Modifier.weight(1f)) { filtro = "pronto" }
            BotonFiltro("caducados", filtro == "caducados", Modifier.weight(1f)) { filtro = "caducados" }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (productosFiltrados.isEmpty()) {
            Text("no hay productos para este filtro")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(productosFiltrados, key = { producto -> producto.id }) { producto ->
                    TarjetaProducto(
                        producto = producto,
                        onClick = { productoSeleccionado = producto }
                    )
                }
            }
        }
    }

    val producto = productoSeleccionado
    if (producto != null) {
        AlertDialog(
            onDismissRequest = { productoSeleccionado = null },
            title = { Text(producto.nombre) },
            text = { Text("elige que quieres hacer con este producto") },
            confirmButton = {
                TextButton(onClick = {
                    productoSeleccionado = null
                    onConsumirProducto(producto)
                }) { Text("consumido") }
            },
            dismissButton = {
                Column {
                    TextButton(onClick = {
                        productoSeleccionado = null
                        onAnadirCompra(producto)
                    }) { Text("anadir a compra") }
                    TextButton(onClick = {
                        productoSeleccionado = null
                        onEliminarProducto(producto)
                    }) { Text("eliminar") }
                    TextButton(onClick = { productoSeleccionado = null }) { Text("cancelar") }
                }
            }
        )
    }
}

@Composable
private fun BotonFiltro(texto: String, seleccionado: Boolean, modifier: Modifier, onClick: () -> Unit) {
    if (seleccionado) {
        Button(onClick = onClick, modifier = modifier) { Text(texto) }
    } else {
        OutlinedButton(onClick = onClick, modifier = modifier) { Text(texto) }
    }
}

@Composable
private fun TarjetaProducto(producto: Producto, onClick: () -> Unit) {
    val colorTexto = when {
        producto.estaCaducado() -> Color(0xFFC62828)
        producto.caducaPronto() -> Color(0xFFEF6C00)
        else -> Color(0xFF2E7D32)
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(producto.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("cantidad ${producto.cantidad}")
            Text("categoria ${producto.categoria}")
            Text(producto.textoCaducidad(), color = colorTexto, fontWeight = FontWeight.Bold)
        }
    }
}
