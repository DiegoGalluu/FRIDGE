package com.example.fridge.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TarjetaResumen(titulo: String, valor: String, modifier: Modifier = Modifier) {
    // Card de Material 3 para evidenciar el uso del componente Card basico.
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = valor, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = titulo, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun TituloSeccion(texto: String) {
    // titulo comun para separar bloques
    Text(
        text = texto,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun FilaDosTarjetas(
    izquierdaTitulo: String,
    izquierdaValor: String,
    derechaTitulo: String,
    derechaValor: String
) {
    // fila con dos tarjetas de resumen
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TarjetaResumen(izquierdaTitulo, izquierdaValor, Modifier.weight(1f))
        TarjetaResumen(derechaTitulo, derechaValor, Modifier.weight(1f))
    }
}
