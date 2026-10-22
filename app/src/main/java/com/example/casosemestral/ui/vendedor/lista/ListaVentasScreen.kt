package com.example.casosemestral.ui.vendedor.lista

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun ListaVentasScreen(
    navController: NavController,
    vm: VentaListViewModel,
    username: String,
    mensaje: String? = null
){

    // OBTENEMOS LA LISTA DE VENTAS HACIENDO USO DEL VIEWMODEL
    val listaVentas by vm.ventasState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var esError by remember { mutableStateOf(false) }

    // SI TRAE UN MENSAJE CHQUEAMOS SI ES DE BORRADO O DE ACTUALIZADO Y SE LANZA EL SANCKBAR
    LaunchedEffect(mensaje) {
        mensaje?.let { texto ->
            esError = mensaje == "Venta Borrada"
            snackbarHostState.showSnackbar(texto)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(120.dp)
                    .background(color = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Ventas de $username",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.align(Alignment.BottomStart)
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                )
            }

            if (listaVentas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay ventas registradas",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listaVentas, key = { venta -> venta.id }) { venta ->
                        VentaItemCard(
                            venta = venta,
                            onVerDetalleClick = {
                                navController.navigate("vendedor_detail_screen/$username/${venta.id}")
                            },
                            onActualizarClick = {
                                navController.navigate("vendedor_update_screen/$username/${venta.id}")
                            },
                            onEliminarClick = {
                                vm.eliminarVenta(venta)
                                esError = true
                                scope.launch {
                                    snackbarHostState.showSnackbar("Venta eliminada correctamente")
                                }
                            }
                        )

                    }
                }
            }

            // BOTON PARA VOLVER AL MENU
            OutlinedButton(
                onClick = {
                    navController.navigate("vendedor_screen/$username") {
                        popUpTo("vendedor_screen/$username") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(4.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Volver al Menú")
            }
        }

        // HOST DEL SNACKBAR DINÁMICO AL FONDO
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) { data ->
            Snackbar(
                containerColor = if (!esError) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                contentColor = if (!esError) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                action = {
                    TextButton(onClick = { data.dismiss() }) {
                        Text("OK", color = if (!esError) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (!esError) Icons.Default.CheckCircle else Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(data.visuals.message)
                }
            }
        }

    }
}