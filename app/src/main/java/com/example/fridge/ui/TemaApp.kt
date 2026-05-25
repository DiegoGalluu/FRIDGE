package com.example.fridge.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VerdePrincipal = Color(0xFF2E7D32)
private val VerdeSuave = Color(0xFFC8E6C9)
private val NaranjaAviso = Color(0xFFFFA726)
private val RojoSuave = Color(0xFFE57373)
private val FondoClaro = Color(0xFFF7FBF6)
private val GrisOscuro = Color(0xFF263238)

private val ColoresFridge = lightColorScheme(
    primary = VerdePrincipal,
    secondary = VerdeSuave,
    tertiary = NaranjaAviso,
    error = RojoSuave,
    background = FondoClaro,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = GrisOscuro,
    onTertiary = GrisOscuro,
    onBackground = GrisOscuro,
    onSurface = GrisOscuro
)

@Composable
fun FridgeTheme(content: @Composable () -> Unit) {
    // aplica colores sencillos de material design
    MaterialTheme(
        colorScheme = ColoresFridge,
        typography = MaterialTheme.typography,
        content = content
    )
}
