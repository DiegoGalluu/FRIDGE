package com.example.fridge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fridge.datos.AlmacenDatos
import com.example.fridge.datos.DatosEjemplo
import com.example.fridge.modelo.InformeDespensa
import com.example.fridge.modelo.ItemCompra
import com.example.fridge.modelo.Producto
import com.example.fridge.psp.ExportadorDespensa
import com.example.fridge.psp.GestorHilos
import com.example.fridge.psp.GestorProcesos
import com.example.fridge.ui.FridgeTheme
import com.example.fridge.ui.PantallaCompra
import com.example.fridge.ui.PantallaDespensa
import com.example.fridge.ui.PantallaInforme
import com.example.fridge.ui.PantallaInicio
import com.example.fridge.ui.PantallaNuevoProducto
import com.example.fridge.util.Rutas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FridgeTheme {
                AplicacionFridge()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AplicacionFridge() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val listaProductos = remember { mutableStateListOf<Producto>() }
    val listaCompra = remember { mutableStateListOf<ItemCompra>() }
    val listaInformes = remember { mutableStateListOf<InformeDespensa>() }
    var mostrarAyuda by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        listaProductos.addAll(AlmacenDatos.obtenerProductos(context))
        listaCompra.addAll(AlmacenDatos.obtenerCompra(context))
        listaInformes.addAll(AlmacenDatos.obtenerInformes(context))
    }

    fun mostrarMensaje(texto: String) {
        scope.launch { snackbarHostState.showSnackbar(texto) }
    }

    fun navegar(ruta: String) {
        navController.navigate(ruta) {
            launchSingleTop = true
            restoreState = true
            popUpTo(Rutas.INICIO) { saveState = true }
        }
    }

    fun guardarProductos(nuevaLista: List<Producto>) {
        listaProductos.clear()
        listaProductos.addAll(nuevaLista)
        AlmacenDatos.guardarProductos(context, listaProductos)
    }

    fun guardarCompra(nuevaLista: List<ItemCompra>) {
        listaCompra.clear()
        listaCompra.addAll(nuevaLista)
        AlmacenDatos.guardarCompra(context, listaCompra)
    }

    fun guardarInformes(nuevaLista: List<InformeDespensa>) {
        listaInformes.clear()
        listaInformes.addAll(nuevaLista)
        AlmacenDatos.guardarInformes(context, listaInformes)
    }

    val rutaActual = rutaActual(navController)
    val titulo = tituloRuta(rutaActual)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "FRIDGE",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
                DrawerItem("inicio", Icons.Filled.Home, rutaActual == Rutas.INICIO) {
                    scope.launch { drawerState.close() }
                    navegar(Rutas.INICIO)
                }
                DrawerItem("mi despensa", Icons.AutoMirrored.Filled.List, rutaActual == Rutas.DESPENSA) {
                    scope.launch { drawerState.close() }
                    navegar(Rutas.DESPENSA)
                }
                DrawerItem("anadir producto", Icons.Filled.Add, rutaActual == Rutas.NUEVO_PRODUCTO) {
                    scope.launch { drawerState.close() }
                    navController.navigate(Rutas.NUEVO_PRODUCTO)
                }
                DrawerItem("lista de la compra", Icons.Filled.ShoppingCart, rutaActual == Rutas.COMPRA) {
                    scope.launch { drawerState.close() }
                    navegar(Rutas.COMPRA)
                }
                DrawerItem("informe de despensa", Icons.Filled.Assessment, rutaActual == Rutas.INFORME) {
                    scope.launch { drawerState.close() }
                    navegar(Rutas.INFORME)
                }
                DrawerItem("ayuda", Icons.AutoMirrored.Filled.Help, false) {
                    scope.launch { drawerState.close() }
                    mostrarAyuda = true
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(titulo) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "menu")
                        }
                    }
                )
            },
            bottomBar = {
                BarraInferior(rutaActual = rutaActual, onNavegar = { ruta -> navegar(ruta) })
            },
            floatingActionButton = {
                if (rutaActual == Rutas.DESPENSA) {
                    FloatingActionButton(onClick = { navController.navigate(Rutas.NUEVO_PRODUCTO) }) {
                        Icon(Icons.Filled.Add, contentDescription = "anadir")
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValores ->
            ContenidoNavegacion(
                paddingValores = paddingValores,
                navController = navController,
                productos = listaProductos,
                compra = listaCompra,
                informes = listaInformes,
                onCargarEjemplo = {
                    guardarProductos(DatosEjemplo.productos())
                    mostrarMensaje("datos de ejemplo cargados")
                },
                onGuardarProducto = { producto ->
                    guardarProductos(listaProductos + producto)
                    mostrarMensaje("producto guardado")
                    navegar(Rutas.DESPENSA)
                },
                onConsumirProducto = { producto ->
                    guardarProductos(listaProductos.filter { it.id != producto.id })
                    mostrarMensaje("producto marcado como consumido")
                },
                onAnadirCompraDesdeProducto = { producto ->
                    guardarCompra(listaCompra + ItemCompra(nombre = producto.nombre))
                    mostrarMensaje("anadido a la lista de la compra")
                },
                onEliminarProducto = { producto ->
                    guardarProductos(listaProductos.filter { it.id != producto.id })
                    mostrarMensaje("producto eliminado")
                },
                onAnadirItemCompra = { nombre ->
                    guardarCompra(listaCompra + ItemCompra(nombre = nombre))
                    mostrarMensaje("anadido a la lista de la compra")
                },
                onEliminarItemCompra = { item, comprado ->
                    guardarCompra(listaCompra.filter { it.id != item.id })
                    mostrarMensaje(if (comprado) "item marcado como comprado" else "item eliminado")
                },
                onGenerarInforme = {
                    val productosActuales = listaProductos.toList()
                    withContext(Dispatchers.IO) {
                        generarInformeCompleto(context, productosActuales)
                    }
                },
                onInformeGenerado = { informe ->
                    guardarInformes(listOf(informe) + listaInformes)
                },
                onMostrarMensaje = { texto -> mostrarMensaje(texto) },
                onNavegar = { ruta -> navegar(ruta) }
            )
        }
    }

    if (mostrarAyuda) {
        AlertDialog(
            onDismissRequest = { mostrarAyuda = false },
            title = { Text("ayuda") },
            text = {
                Text("usa inicio para ver el resumen, despensa para controlar alimentos, compra para apuntar lo que falta e informe para revisar el estado de tu despensa")
            },
            confirmButton = {
                TextButton(onClick = { mostrarAyuda = false }) { Text("entendido") }
            }
        )
    }
}

@Composable
private fun ContenidoNavegacion(
    paddingValores: PaddingValues,
    navController: NavHostController,
    productos: List<Producto>,
    compra: List<ItemCompra>,
    informes: List<InformeDespensa>,
    onCargarEjemplo: () -> Unit,
    onGuardarProducto: (Producto) -> Unit,
    onConsumirProducto: (Producto) -> Unit,
    onAnadirCompraDesdeProducto: (Producto) -> Unit,
    onEliminarProducto: (Producto) -> Unit,
    onAnadirItemCompra: (String) -> Unit,
    onEliminarItemCompra: (ItemCompra, Boolean) -> Unit,
    onGenerarInforme: suspend () -> InformeDespensa,
    onInformeGenerado: (InformeDespensa) -> Unit,
    onMostrarMensaje: (String) -> Unit,
    onNavegar: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Rutas.INICIO,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValores)
    ) {
        composable(Rutas.INICIO) {
            PantallaInicio(
                productos = productos,
                compra = compra,
                informes = informes,
                onCargarEjemplo = onCargarEjemplo,
                onNuevoProducto = { navController.navigate(Rutas.NUEVO_PRODUCTO) },
                onVerDespensa = { onNavegar(Rutas.DESPENSA) },
                onVerCompra = { onNavegar(Rutas.COMPRA) },
                onVerInforme = { onNavegar(Rutas.INFORME) }
            )
        }
        composable(Rutas.DESPENSA) {
            PantallaDespensa(
                productos = productos,
                onConsumirProducto = onConsumirProducto,
                onAnadirCompra = onAnadirCompraDesdeProducto,
                onEliminarProducto = onEliminarProducto
            )
        }
        composable(Rutas.NUEVO_PRODUCTO) {
            PantallaNuevoProducto(
                onGuardar = onGuardarProducto,
                onCancelar = { navController.popBackStack() }
            )
        }
        composable(Rutas.COMPRA) {
            PantallaCompra(
                compra = compra,
                onAnadirItem = onAnadirItemCompra,
                onEliminarItem = onEliminarItemCompra
            )
        }
        composable(Rutas.INFORME) {
            PantallaInforme(
                ultimoInforme = informes.firstOrNull(),
                productos = productos,
                onAnadirCompra = onAnadirCompraDesdeProducto,
                onGenerarInforme = onGenerarInforme,
                onInformeGenerado = onInformeGenerado,
                onMostrarMensaje = onMostrarMensaje
            )
        }
    }
}

private fun generarInformeCompleto(context: android.content.Context, productos: List<Producto>): InformeDespensa {
    val archivo = ExportadorDespensa.exportar(context, productos)
    val resumenProcesos = GestorProcesos().generarInformeProcesos(archivo)
    val resumenHilos = kotlinx.coroutines.runBlocking { GestorHilos().ejecutarAnalisisHilosYCorrutinas(productos) }

    val proximos = productos.count { producto -> producto.estaProximoACaducar() }
    val caducados = productos.count { producto -> producto.estaCaducado() }
    val masProximo = productos.minByOrNull { producto -> producto.diasRestantes() }
    val recomendacion = when {
        productos.isEmpty() -> "anade productos para empezar a controlar tu despensa"
        caducados > 0 -> "revisa los productos caducados y anade recambios a la lista de la compra"
        proximos > 0 -> "consume primero los productos proximos a caducar"
        else -> "tu despensa esta controlada"
    }

    val textoResumen = buildString {
        append("producto mas proximo a caducar ")
        append(masProximo?.nombre ?: "ninguno")
        append("\n")
        append("recomendacion de compra ")
        append(recomendacion)
    }

    return InformeDespensa(
        totalProductos = productos.size,
        productosProximos = proximos,
        productosCaducados = caducados,
        textoResumen = textoResumen,
        tiempoSecuencial = resumenProcesos.tiempoSecuencial + resumenHilos.tiempoSecuencial,
        tiempoConcurrente = resumenProcesos.tiempoConcurrente + resumenHilos.tiempoConcurrente,
        detallesProcesos = resumenProcesos.detalles,
        detallesHilos = resumenHilos.detalles
    )
}

@Composable
private fun BarraInferior(rutaActual: String, onNavegar: (String) -> Unit) {
    NavigationBar {
        ItemBarra("inicio", Rutas.INICIO, Icons.Filled.Home, rutaActual, onNavegar)
        ItemBarra("despensa", Rutas.DESPENSA, Icons.AutoMirrored.Filled.List, rutaActual, onNavegar)
        ItemBarra("compra", Rutas.COMPRA, Icons.Filled.ShoppingCart, rutaActual, onNavegar)
    }
}

@Composable
private fun RowScope.ItemBarra(
    texto: String,
    ruta: String,
    icono: ImageVector,
    rutaActual: String,
    onNavegar: (String) -> Unit
) {
    val seleccionado = rutaActual == ruta
    val colorPrincipal = MaterialTheme.colorScheme.primary
    val fondoIndicador by animateColorAsState(
        targetValue = if (seleccionado) colorPrincipal else Color.Transparent,
        animationSpec = tween(durationMillis = 140),
        label = "fondoIndicadorNavbar"
    )
    val colorIcono by animateColorAsState(
        targetValue = if (seleccionado) Color.White else colorPrincipal,
        animationSpec = tween(durationMillis = 140),
        label = "colorIconoNavbar"
    )

    Column(
        modifier = Modifier
            .weight(1f)
            .clip(MaterialTheme.shapes.small)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onNavegar(ruta)
            }
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = fondoIndicador,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = texto,
                tint = colorIcono,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = texto,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DrawerItem(texto: String, icono: ImageVector, seleccionado: Boolean, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(texto) },
        selected = seleccionado,
        onClick = onClick,
        icon = { Icon(icono, contentDescription = texto) },
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
    )
}

@Composable
private fun rutaActual(navController: NavHostController): String {
    val entrada by navController.currentBackStackEntryAsState()
    return entrada?.destination?.route ?: Rutas.INICIO
}

private fun tituloRuta(ruta: String): String {
    return when (ruta) {
        Rutas.DESPENSA -> "mi despensa"
        Rutas.NUEVO_PRODUCTO -> "anadir producto"
        Rutas.COMPRA -> "lista de la compra"
        Rutas.INFORME -> "informe"
        else -> "FRIDGE"
    }
}
