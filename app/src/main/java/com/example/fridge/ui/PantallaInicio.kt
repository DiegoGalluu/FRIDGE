package com.example.fridge.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fridge.modelo.InformeDespensa
import com.example.fridge.modelo.ItemCompra
import com.example.fridge.modelo.Producto

@Composable
fun PantallaInicio(
    productos: List<Producto>,
    compra: List<ItemCompra>,
    informes: List<InformeDespensa>,
    onCargarEjemplo: () -> Unit,
    onNuevoProducto: () -> Unit,
    onVerDespensa: () -> Unit,
    onVerCompra: () -> Unit,
    onVerInforme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val urgentes = productos.count { producto -> producto.caducaPronto() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
            Column {
                Text(
                    text = "FRIDGE",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "controla tu comida y evita desperdiciar alimentos",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        FilaDosTarjetas("productos guardados", productos.size.toString(), "caducan pronto", urgentes.toString())
        FilaDosTarjetas("en lista de compra", compra.size.toString(), "informes generados", informes.size.toString())

        Spacer(modifier = Modifier.height(4.dp))

        Button(onClick = onNuevoProducto, modifier = Modifier.fillMaxWidth()) {
            Text("anadir producto")
        }
        OutlinedButton(onClick = onVerDespensa, modifier = Modifier.fillMaxWidth()) {
            Text("ver despensa")
        }
        OutlinedButton(onClick = onVerCompra, modifier = Modifier.fillMaxWidth()) {
            Text("lista de compra")
        }
        OutlinedButton(onClick = onVerInforme, modifier = Modifier.fillMaxWidth()) {
            Text("generar informe")
        }

        if (productos.isEmpty()) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("no hay productos guardados")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onCargarEjemplo, modifier = Modifier.fillMaxWidth()) {
                        Text("cargar datos de ejemplo")
                    }
                }
            }
        }
    }
}
