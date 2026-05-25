package com.example.fridge.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fridge.modelo.DetalleReceta
import com.example.fridge.modelo.IngredienteReceta
import com.example.fridge.modelo.Producto
import com.example.fridge.modelo.RecetaSugerida
import com.example.fridge.recetas.RecipeApiServicio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PantallaRecetas(
    productos: List<Producto>,
    onMostrarMensaje: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var cargando by remember { mutableStateOf(false) }
    var mensajeCarga by remember { mutableStateOf("") }
    var recetas by remember { mutableStateOf<List<RecetaSugerida>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    var recetaSeleccionada by remember { mutableStateOf<RecetaSugerida?>(null) }
    var detalleReceta by remember { mutableStateOf<DetalleReceta?>(null) }
    var cargandoDetalle by remember { mutableStateOf(false) }
    var errorDetalle by remember { mutableStateOf<String?>(null) }
    val ingredientes = remember(productos) { RecipeApiServicio.prepararIngredientes(productos) }

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
                Text(
                    text = "Sugerencias de cocina",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Busca ideas con lo que ya tienes en casa, dando preferencia a los productos que caducan antes.",
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )
                Button(
                    onClick = {
                        scope.launch {
                            cargando = true
                            mensajeCarga = "Buscando recetas y traduciendo resultados"
                            error = null
                            try {
                                recetas = withContext(Dispatchers.IO) {
                                    RecipeApiServicio.buscarRecetas(productos)
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
                                mensajeCarga = ""
                            }
                        }
                    },
                    enabled = !cargando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (cargando) "Buscando" else "Buscar recetas")
                }
                if (cargando) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = mensajeCarga,
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            lineHeight = 19.sp
                        )
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
                TarjetaReceta(
                    receta = receta,
                    onClick = {
                        recetaSeleccionada = receta
                        detalleReceta = null
                        errorDetalle = null
                        cargandoDetalle = true
                        scope.launch {
                            try {
                                detalleReceta = withContext(Dispatchers.IO) {
                                    RecipeApiServicio.obtenerDetalleReceta(receta.id)
                                }
                            } catch (e: Exception) {
                                errorDetalle = e.message ?: "No se pudo cargar la receta"
                            } finally {
                                cargandoDetalle = false
                            }
                        }
                    }
                )
            }
        }
    }

    if (recetaSeleccionada != null) {
        DetalleRecetaDialogo(
            titulo = recetaSeleccionada?.titulo.orEmpty(),
            detalle = detalleReceta,
            cargando = cargandoDetalle,
            error = errorDetalle,
            onCerrar = {
                recetaSeleccionada = null
                detalleReceta = null
                errorDetalle = null
                cargandoDetalle = false
            }
        )
    }
}

@Composable
private fun SeccionIngredientes(ingredientes: List<IngredienteReceta>) {
    var desplegado by remember { mutableStateOf(false) }

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Ingredientes disponibles",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (ingredientes.isEmpty()) {
                            "Sin productos para buscar recetas"
                        } else {
                            "${ingredientes.size} ingredientes preparados"
                        },
                        fontSize = 14.sp,
                        lineHeight = 19.sp
                    )
                }
                IconButton(
                    onClick = { desplegado = !desplegado },
                    enabled = ingredientes.isNotEmpty()
                ) {
                    Icon(
                        imageVector = if (desplegado) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (desplegado) "Ocultar ingredientes" else "Mostrar ingredientes"
                    )
                }
            }

            if (ingredientes.isEmpty()) {
                Text("Añade productos a la despensa para buscar recetas.", fontSize = 16.sp)
            }

            AnimatedVisibility(visible = desplegado && ingredientes.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Producto", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                        Text(
                            "Cantidad",
                            modifier = Modifier.width(82.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Estado",
                            modifier = Modifier.width(88.dp),
                            textAlign = TextAlign.End,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    ingredientes.forEach { ingrediente ->
                        FilaIngrediente(ingrediente)
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaIngrediente(ingrediente: IngredienteReceta) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = ingrediente.nombreOriginal.formatearNombreIngrediente(),
            modifier = Modifier.weight(1f),
            fontSize = 15.sp,
            lineHeight = 20.sp
        )
        Text(
            text = ingrediente.cantidad.toString(),
            modifier = Modifier.width(82.dp),
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            lineHeight = 20.sp
        )
        Text(
            text = if (ingrediente.proximoACaducar) "Prioritario" else "",
            modifier = Modifier.width(88.dp),
            textAlign = TextAlign.End,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 19.sp
        )
    }
}

@Composable
private fun TarjetaReceta(receta: RecetaSugerida, onClick: () -> Unit) {
    val faltantesVisibles = receta.ingredientesFaltantes.take(8)
    val faltantesOcultos = receta.ingredientesFaltantes.size - faltantesVisibles.size

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
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
            Text("Toca para ver la receta completa", fontSize = 14.sp, lineHeight = 19.sp)
            if (receta.ingredientesUsados.isNotEmpty()) {
                HorizontalDivider()
                Text("De tu despensa", fontWeight = FontWeight.SemiBold)
                Text(receta.ingredientesUsados.joinToString(", "), fontSize = 15.sp, lineHeight = 20.sp)
            }
            if (receta.ingredientesFaltantes.isNotEmpty()) {
                HorizontalDivider()
                Text("Te faltaría", fontWeight = FontWeight.SemiBold)
                Text(
                    text = if (faltantesOcultos > 0) {
                        "${faltantesVisibles.joinToString(", ")} y $faltantesOcultos más"
                    } else {
                        faltantesVisibles.joinToString(", ")
                    },
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun DetalleRecetaDialogo(
    titulo: String,
    detalle: DetalleReceta?,
    cargando: Boolean,
    error: String?,
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(detalle?.titulo ?: titulo) },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when {
                    cargando -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator()
                            Text("Cargando receta completa", textAlign = TextAlign.Center)
                        }
                    }
                    error != null -> {
                        Text(error, color = MaterialTheme.colorScheme.error)
                    }
                    detalle != null -> {
                        if (detalle.ingredientes.isNotEmpty()) {
                            Text("Ingredientes", fontWeight = FontWeight.Bold)
                            Text(detalle.ingredientes.joinToString("\n") { ingrediente -> "• $ingrediente" })
                        }
                        if (detalle.pasos.isNotEmpty()) {
                            HorizontalDivider()
                            Text("Pasos", fontWeight = FontWeight.Bold)
                            detalle.pasos.forEachIndexed { indice, paso ->
                                Text("${indice + 1}. $paso", lineHeight = 20.sp)
                            }
                        } else if (detalle.resumen.isNotBlank()) {
                            HorizontalDivider()
                            Text("Resumen", fontWeight = FontWeight.Bold)
                            Text(detalle.resumen, lineHeight = 20.sp)
                        } else {
                            Text("Spoonacular no ha devuelto instrucciones para esta receta.")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onCerrar) {
                Text("Cerrar")
            }
        }
    )
}

private fun String.formatearNombreIngrediente(): String {
    return trim().replaceFirstChar { letra ->
        if (letra.isLowerCase()) letra.titlecase() else letra.toString()
    }
}
