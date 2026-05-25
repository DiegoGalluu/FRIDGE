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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        "proximos" -> productos.filter { producto -> producto.estaProximoACaducar() }
        "caducados" -> productos.filter { producto -> producto.estaCaducado() }
        else -> productos
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TituloSeccion("Mi despensa")
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            BotonFiltro("Todos", filtro == "todos", Modifier.weight(1f)) { filtro = "todos" }
            BotonFiltro("Próximos", filtro == "proximos", Modifier.weight(1f)) { filtro = "proximos" }
            BotonFiltro("Caducados", filtro == "caducados", Modifier.weight(1f)) { filtro = "caducados" }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (productosFiltrados.isEmpty()) {
            Text("No hay productos para este filtro")
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
            text = { Text("Elige qué quieres hacer con este producto") },
            confirmButton = {
                TextButton(onClick = {
                    productoSeleccionado = null
                    onConsumirProducto(producto)
                }) { Text("Consumido") }
            },
            dismissButton = {
                Column {
                    TextButton(onClick = {
                        productoSeleccionado = null
                        onAnadirCompra(producto)
                    }) { Text("Añadir a la compra") }
                    TextButton(onClick = {
                        productoSeleccionado = null
                        onEliminarProducto(producto)
                    }) { Text("Eliminar") }
                    TextButton(onClick = { productoSeleccionado = null }) { Text("Cancelar") }
                }
            }
        )
    }
}

@Composable
private fun BotonFiltro(texto: String, seleccionado: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val botonModifier = modifier.height(52.dp)
    val padding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
    if (seleccionado) {
        Button(
            onClick = onClick,
            modifier = botonModifier,
            contentPadding = padding
        ) { TextoFiltro(texto) }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = botonModifier,
            contentPadding = padding
        ) { TextoFiltro(texto) }
    }
}

@Composable
private fun TextoFiltro(texto: String) {
    Text(
        text = texto,
        maxLines = 1,
        overflow = TextOverflow.Clip,
        softWrap = false,
        fontSize = 14.sp
    )
}

@Composable
private fun TarjetaProducto(producto: Producto, onClick: () -> Unit) {
    val colorTexto = when {
        producto.estaCaducado() -> Color(0xFFC62828)
        producto.estaProximoACaducar() -> Color(0xFFEF6C00)
        else -> Color(0xFF2E7D32)
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(producto.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Cantidad ${producto.cantidad}")
            Text("Categoría ${formatearCategoria(producto.categoria)}")
            Text(producto.textoCaducidad(), color = colorTexto, fontWeight = FontWeight.Bold)
        }
    }
}

private fun formatearCategoria(categoria: String): String {
    return when (categoria.lowercase()) {
        "lacteos", "lácteos" -> "Lácteos"
        "carne" -> "Carne"
        "pescado" -> "Pescado"
        "verdura" -> "Verdura"
        "fruta" -> "Fruta"
        "bebida" -> "Bebida"
        "despensa" -> "Despensa"
        "otro" -> "Otro"
        else -> categoria.replaceFirstChar { letra -> letra.uppercase() }
    }
}
