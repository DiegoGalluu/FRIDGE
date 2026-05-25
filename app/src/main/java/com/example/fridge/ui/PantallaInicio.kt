package com.example.fridge.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
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
    onVerDespensa: () -> Unit,
    onVerCompra: () -> Unit,
    onVerInforme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val proximos = productos.count { producto -> producto.estaProximoACaducar() }

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
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onVerDespensa)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Productos", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(productos.size.toString(), fontSize = 42.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = if (proximos == 1) "1 producto próximo a caducar" else "$proximos productos próximos a caducar",
                    fontSize = 17.sp,
                    lineHeight = 23.sp
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            DatoInicio("Lista de la compra", compra.size.toString(), Modifier.weight(1f), onVerCompra)
            DatoInicio("Informes", informes.size.toString(), Modifier.weight(1f), onVerInforme)
        }
    }
}

@Composable
private fun DatoInicio(titulo: String, valor: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    ElevatedCard(modifier = modifier.clickable(onClick = onClick)) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(valor, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Text(titulo, fontSize = 16.sp, lineHeight = 20.sp)
        }
    }
}
