package com.example.casosemestral.ui.vendedor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.DoorBack
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MenuScreen(
    navController: NavController,
    username: String,
    mensajeExito: String? = null
){
    val snackbarHostState = remember { SnackbarHostState() }

    // SI VIENE UN MENSAJE DE EXITO SE DISPARA EL SNACKBAR
    LaunchedEffect(mensajeExito) {
        mensajeExito?.let { texto ->
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
                    text = "Bienvenido vendedor ¡¡$username!!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.align(Alignment.BottomStart)
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                )
            }

            LazyColumn(modifier = Modifier.weight(1f).padding(vertical = 8.dp)) {

                item {
                    NavigationDrawerItem(
                        label = { Text("Registrar Venta") },
                        selected = false,
                        onClick = {
                            navController.navigate("vendedor_form_screen/$username") {
                                popUpTo("vendedor_form_screen/$username") { inclusive = true }
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Default.Sell,
                                contentDescription = "Registro de venta",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    )
                }

                item {
                    NavigationDrawerItem(
                        label = { Text("Listado de Ventas Registradas") },
                        selected = false,
                        onClick = {
                            navController.navigate("vendedor_lista_screen/$username") {
                                popUpTo("vendedor_lista_screen/$username") { inclusive = true }
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Default.Checklist,
                                contentDescription = "Listado de Ventas",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    )
                }

                item {
                    NavigationDrawerItem(
                        label = { Text("Cerrar Sesión") },
                        selected = false,
                        onClick = {
                            navController.navigate("login_screen") {
                                popUpTo("login_screen") { inclusive = true }
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Default.DoorBack,
                                contentDescription = "Cerrar Sesion",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    )
                }

            }
        }

        // SNACKBAR HOST EN EL MENÚ
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) { data ->
            Snackbar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                action = {
                    TextButton(onClick = { data.dismiss() }) {
                        Text("OK", color = MaterialTheme.colorScheme.primary)
                    }
                }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(data.visuals.message)
                }
            }
        }
    }
}