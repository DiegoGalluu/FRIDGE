package com.example.fridge.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
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
import com.example.fridge.modelo.InformeDespensa
import com.example.fridge.util.UtilFechas
import kotlinx.coroutines.launch

@Composable
fun PantallaInforme(
    ultimoInforme: InformeDespensa?,
    onGenerarInforme: suspend () -> InformeDespensa,
    onInformeGenerado: (InformeDespensa) -> Unit,
    onMostrarMensaje: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var cargando by remember { mutableStateOf(false) }
    var mostrarDetalles by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TituloSeccion("informe de despensa")
        Text("genera un resumen de tus productos y revisa que debes comprar")

        Button(
            onClick = {
                scope.launch {
                    try {
                        cargando = true
                        val informe = onGenerarInforme()
                        onInformeGenerado(informe)
                        onMostrarMensaje("informe generado")
                    } catch (e: Exception) {
                        onMostrarMensaje("no se pudo generar el informe: ${e.message ?: "error desconocido"}")
                    } finally {
                        cargando = false
                    }
                }
            },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (cargando) "generando" else "generar informe")
        }

        val informe = ultimoInforme
        if (informe == null) {
            Text("todavia no hay informes generados")
        } else {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("resumen", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("fecha ${UtilFechas.formatearFecha(informe.fecha)}")
                    Text("total de productos ${informe.totalProductos}")
                    Text("productos que caducan pronto ${informe.productosUrgentes}")
                    Text("productos caducados ${informe.productosCaducados}")
                    Text("tiempo de analisis secuencial ${informe.tiempoSecuencial} ms")
                    Text("tiempo de analisis concurrente ${informe.tiempoConcurrente} ms")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(informe.textoResumen)
                }
            }

            Button(onClick = { mostrarDetalles = !mostrarDetalles }, modifier = Modifier.fillMaxWidth()) {
                Text(if (mostrarDetalles) "ocultar detalles tecnicos" else "mostrar detalles tecnicos")
            }

            if (mostrarDetalles) {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("detalles tecnicos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("psp ra1 procesos", fontWeight = FontWeight.Bold)
                        Text(informe.detallesProcesos)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("psp ra2 hilos y corrutinas", fontWeight = FontWeight.Bold)
                        Text(informe.detallesHilos)
                    }
                }
            }
        }
    }
}
