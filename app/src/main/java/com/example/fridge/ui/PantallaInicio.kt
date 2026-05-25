package com.example.fridge.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "FRIDGE",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Controla tu despensa sin complicarte.",
                fontSize = 18.sp,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("productos guardados", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(productos.size.toString(), fontSize = 42.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = if (urgentes == 1) "1 producto caduca pronto" else "$urgentes productos caducan pronto",
                    fontSize = 17.sp,
                    lineHeight = 23.sp
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            DatoInicio("compra", compra.size.toString(), Modifier.weight(1f))
            DatoInicio("informes", informes.size.toString(), Modifier.weight(1f))
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onNuevoProducto, modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 54.dp)) {
                Text("anadir producto", fontSize = 16.sp)
            }
            OutlinedButton(onClick = onVerDespensa, modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 54.dp)) {
                Text("ver despensa", fontSize = 16.sp)
            }
            OutlinedButton(onClick = onVerCompra, modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 54.dp)) {
                Text("lista de la compra", fontSize = 16.sp)
            }
            OutlinedButton(onClick = onVerInforme, modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 54.dp)) {
                Text("generar informe", fontSize = 16.sp)
            }
        }

        if (productos.isEmpty()) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("no hay productos guardados", fontSize = 17.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onCargarEjemplo, modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 54.dp)) {
                        Text("cargar datos de ejemplo", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DatoInicio(titulo: String, valor: String, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(valor, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Text(titulo, fontSize = 16.sp, lineHeight = 20.sp)
        }
    }
}
