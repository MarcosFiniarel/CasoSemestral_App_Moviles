package com.example.casosemestral.ui.gestor

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.DoorBack
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MenuGestorScreen(
    navController: NavController,
    username: String,
    vm: MenuGestorViewModel
) {
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
                    text = "Bienvenido gestor -- $username --",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.align(Alignment.BottomStart)
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                )
            }

            LazyColumn(modifier = Modifier.weight(1f).padding(vertical = 8.dp)) {

                item {
                    val cantidadNuevas by vm.cantidadNuevasVentas.collectAsState()

                    NavigationDrawerItem(
                        label = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Auditar ventas")
                                if (cantidadNuevas > 0) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.tertiaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                    ) {
                                        Text(
                                            text = if (cantidadNuevas > 99) "99+" else cantidadNuevas.toString(),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        },
                        selected = false,
                        onClick = {
                            navController.navigate("gestor_lista_ventas_screen/$username/AUDITAR") {
                                popUpTo("gestor_lista_ventas_screen/$username/AUDITAR") { inclusive = true }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.RemoveRedEye,
                                contentDescription = "Auditoria de venta",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    )
                }

                item {
                    val cantidadHistorico by vm.cantidadHistoricoNuevo.collectAsState()

                    NavigationDrawerItem(
                        label = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Historico de ventas auditadas")
                                if (cantidadHistorico > 0) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.tertiaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                    ) {
                                        Text(
                                            text = if (cantidadHistorico > 99) "99+" else cantidadHistorico.toString(),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        },
                        selected = false,
                        onClick = {
                            navController.navigate("gestor_lista_ventas_screen/$username/HISTORICO") {
                                popUpTo("gestor_lista_ventas_screen/$username/HISTORICO") { inclusive = true }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.LockClock,
                                contentDescription = "Historico de Ventas",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    )
                }

                item {
                    NavigationDrawerItem(
                        label = { Text("Calculo de comisiones") },
                        selected = false,
                        onClick = {
                            //navController.navigate("vendedor_lista_screen/$username") {
                            //    popUpTo("vendedor_lista_screen/$username") { inclusive = true }
                            //}
                        },
                        icon = {
                            Icon(
                                Icons.Default.MonetizationOn,
                                contentDescription = "Calculo de Comisiones",
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
    }
}