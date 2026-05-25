package com.example.fridge.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fridge.modelo.InformeDespensa
import com.example.fridge.modelo.Producto
import com.example.fridge.util.UtilFechas
import kotlinx.coroutines.launch

@Composable
fun PantallaInforme(
    ultimoInforme: InformeDespensa?,
    productos: List<Producto>,
    onAnadirCompra: (Producto) -> Unit,
    onGenerarInforme: suspend () -> InformeDespensa,
    onInformeGenerado: (InformeDespensa) -> Unit,
    onMostrarMensaje: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var cargando by remember { mutableStateOf(false) }
    val caducados = productos.filter { producto -> producto.estaCaducado() }.sortedBy { producto -> producto.diasRestantes() }
    val proximos = productos.filter { producto -> producto.estaProximoACaducar() }.sortedBy { producto -> producto.diasRestantes() }
    val sugeridosCompra = productos
        .filter { producto -> producto.cantidad <= 1 || producto.estaCaducado() || producto.estaProximoACaducar() }
        .distinctBy { producto -> producto.nombre.lowercase() }
        .sortedWith(compareBy<Producto> { producto -> producto.diasRestantes() }.thenBy { producto -> producto.nombre })
    val recomendacion = when {
        productos.isEmpty() -> "Añade productos para empezar a controlar tu despensa."
        caducados.isNotEmpty() -> "Retira los productos caducados y apunta recambios si los necesitas."
        proximos.isNotEmpty() -> "Consume primero los productos próximos a caducar."
        sugeridosCompra.isNotEmpty() -> "Revisa los productos con poca cantidad y completa la lista de la compra."
        else -> "Tu despensa está controlada."
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TituloSeccion("Informe de despensa")
        Text(
            text = "Revisa qué consumir primero y qué conviene apuntar en la compra.",
            fontSize = 17.sp,
            lineHeight = 23.sp
        )

        Button(
            onClick = {
                scope.launch {
                    try {
                        cargando = true
                        val informe = onGenerarInforme()
                        onInformeGenerado(informe)
                        onMostrarMensaje("Informe generado en ${informe.rutaResultados}")
                    } catch (e: Exception) {
                        onMostrarMensaje("No se pudo generar el informe: ${e.message ?: "error desconocido"}")
                    } finally {
                        cargando = false
                    }
                }
            },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 54.dp)
        ) {
            Text(if (cargando) "Actualizando" else "Actualizar informe", fontSize = 16.sp)
        }

        val informe = ultimoInforme
        if (informe == null) {
            Text("Todavía no hay informes generados", fontSize = 17.sp)
        } else {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Plan para hoy", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(recomendacion, fontSize = 18.sp, lineHeight = 25.sp)
                    HorizontalDivider()
                    Text("Actualizado ${UtilFechas.formatearFecha(informe.fecha)}", fontSize = 15.sp)
                    if (informe.rutaResultados.isNotBlank()) {
                        Text("Archivo: ${informe.rutaResultados}", fontSize = 14.sp, lineHeight = 19.sp)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                ResumenDato("Productos", informe.totalProductos.toString(), Modifier.weight(1f))
                ResumenDato("Próximos", informe.productosProximos.toString(), Modifier.weight(1f))
                ResumenDato("Caducados", informe.productosCaducados.toString(), Modifier.weight(1f))
            }

            SeccionProductos(
                titulo = "Caducados",
                productos = caducados,
                textoVacio = "No tienes productos caducados",
                onAnadirCompra = onAnadirCompra
            )

            SeccionProductos(
                titulo = "Próximos a caducar",
                productos = proximos,
                textoVacio = "No tienes productos próximos a caducar",
                onAnadirCompra = onAnadirCompra
            )

            SeccionProductos(
                titulo = "Compra sugerida",
                productos = sugeridosCompra,
                textoVacio = "No hay sugerencias de compra ahora mismo",
                onAnadirCompra = onAnadirCompra
            )

        }
    }
}

@Composable
private fun ResumenDato(titulo: String, valor: String, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(valor, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(titulo, fontSize = 14.sp, lineHeight = 18.sp)
        }
    }
}

@Composable
private fun SeccionProductos(
    titulo: String,
    productos: List<Producto>,
    textoVacio: String,
    onAnadirCompra: (Producto) -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            if (productos.isEmpty()) {
                Text(textoVacio, fontSize = 16.sp)
            } else {
                productos.forEachIndexed { indice, producto ->
                    ProductoInforme(producto = producto, onAnadirCompra = onAnadirCompra)
                    if (indice < productos.lastIndex) HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun ProductoInforme(producto: Producto, onAnadirCompra: (Producto) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(producto.nombre, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Text(
            text = "${producto.textoCaducidad()} · Cantidad ${producto.cantidad} · ${formatearCategoria(producto.categoria)}",
            fontSize = 15.sp,
            lineHeight = 20.sp
        )
        OutlinedButton(
            onClick = { onAnadirCompra(producto) },
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 48.dp)
        ) {
            Text("Añadir a la compra", fontSize = 15.sp)
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
