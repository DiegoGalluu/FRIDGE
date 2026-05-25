package com.example.fridge.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.fridge.modelo.IngredienteReceta
import com.example.fridge.modelo.Producto
import com.example.fridge.modelo.RecetaSugerida
import com.example.fridge.recetas.SpoonacularServicio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PantallaRecetas(
    productos: List<Producto>,
    apiKeyGuardada: String,
    onGuardarApiKey: (String) -> Unit,
    onMostrarMensaje: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var apiKey by remember(apiKeyGuardada) { mutableStateOf(apiKeyGuardada) }
    var cargando by remember { mutableStateOf(false) }
    var recetas by remember { mutableStateOf<List<RecetaSugerida>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    val ingredientes = remember(productos) { SpoonacularServicio.prepararIngredientes(productos) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TituloSeccion("Recetas")
        Text(
            text = "Busca recetas usando tu despensa. FRIDGE prioriza los productos próximos a caducar para darles salida antes.",
            fontSize = 17.sp,
            lineHeight = 23.sp
        )

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("API key de Spoonacular", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("Clave gratuita") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = {
                            onGuardarApiKey(apiKey.trim())
                            onMostrarMensaje("API key guardada")
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Guardar clave")
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                cargando = true
                                error = null
                                onGuardarApiKey(apiKey.trim())
                                try {
                                    recetas = withContext(Dispatchers.IO) {
                                        SpoonacularServicio.buscarRecetas(productos, apiKey.trim())
                                    }
                                    if (recetas.isEmpty()) {
                                        onMostrarMensaje("No se encontraron recetas")
                                    } else {
                                        onMostrarMensaje("Recetas actualizadas")
                                    }
                                } catch (e: Exception) {
                                    error = e.message ?: "No se pudieron buscar recetas"
                                } finally {
                                    cargando = false
                                }
                            }
                        },
                        enabled = !cargando,
                        modifier = Modifier.weight(1f).defaultMinSize(minHeight = 48.dp)
                    ) {
                        Text(if (cargando) "Buscando" else "Buscar")
                    }
                }
            }
        }

        SeccionIngredientes(ingredientes)

        if (error != null) {
            Text(
                text = error ?: "",
                color = MaterialTheme.colorScheme.error,
                fontSize = 15.sp,
                lineHeight = 20.sp
            )
        }

        if (recetas.isEmpty()) {
            Text("Todavía no hay recetas sugeridas", fontSize = 17.sp)
        } else {
            recetas.forEach { receta ->
                TarjetaReceta(receta)
            }
        }
    }
}

@Composable
private fun SeccionIngredientes(ingredientes: List<IngredienteReceta>) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Ingredientes enviados", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (ingredientes.isEmpty()) {
                Text("Añade productos a la despensa para buscar recetas.", fontSize = 16.sp)
            } else {
                ingredientes.forEach { ingrediente ->
                    val prioridad = if (ingrediente.proximoACaducar) " · prioridad" else ""
                    Text(
                        text = "${ingrediente.nombreOriginal} · cantidad ${ingrediente.cantidad}$prioridad",
                        fontSize = 15.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaReceta(receta: RecetaSugerida) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(receta.titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                text = "Usa ${receta.totalUsados} ingrediente(s) de tu despensa · faltan ${receta.totalFaltantes}",
                fontSize = 15.sp,
                lineHeight = 20.sp
            )
            if (receta.ingredientesUsados.isNotEmpty()) {
                HorizontalDivider()
                Text("De tu despensa", fontWeight = FontWeight.SemiBold)
                Text(receta.ingredientesUsados.joinToString(", "), fontSize = 15.sp, lineHeight = 20.sp)
            }
            if (receta.ingredientesFaltantes.isNotEmpty()) {
                HorizontalDivider()
                Text("Te faltaría", fontWeight = FontWeight.SemiBold)
                Text(receta.ingredientesFaltantes.joinToString(", "), fontSize = 15.sp, lineHeight = 20.sp)
            }
            if (receta.ingredientesSinUsar.isNotEmpty()) {
                Text("Sin usar: ${receta.ingredientesSinUsar.joinToString(", ")}", fontSize = 14.sp, lineHeight = 19.sp)
            }
        }
    }
}
