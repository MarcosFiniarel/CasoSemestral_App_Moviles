package com.example.casosemestral.ui.administrador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.DoorBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MenuAdministradorScreen(
    navController: NavController,
    username: String,
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
                    text = "Bienvenido administrador || $username ||",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.align(Alignment.BottomStart)
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                )
            }

            LazyColumn(modifier = Modifier.weight(1f).padding(vertical = 8.dp)) {

                item {
                    NavigationDrawerItem(
                        label = { Text("Panel de admins") },
                        selected = false,
                        onClick = {
                            navController.navigate("panel_de_admin_screen/$username") {
                                popUpTo("panel_de_admin_screen/$username") { inclusive = true }
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Default.Badge,
                                contentDescription = "Panel de admins",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    )
                }

                item {
                    NavigationDrawerItem(
                        label = { Text("Panel de usuarios") },
                        selected = false,
                        onClick = {
                            navController.navigate("panel_de_usuarios_screen/$username") {
                                popUpTo("panel_de_usuarios_screen/$username") { inclusive = true }
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Panel de usuarios",
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