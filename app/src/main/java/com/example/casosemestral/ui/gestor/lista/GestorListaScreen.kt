package com.example.casosemestral.ui.gestor.lista

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun GestorListaScreen(
    navController: NavController,
    vm: GestorListaViewModel,
    username: String,
    modo: String
){
    // OBTENEMOS LA LISTA DE VENTAS HACIENDO USO DEL VIEWMODEL
    val listaVentas by vm.ventas.collectAsState()

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
                    text = "Auditoria de $username",
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
                    if (modo == "AUDITAR") {
                        Text(
                            text = "No hay auditorias pendientes",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    } else {
                        Text(
                            text = "No hay historico disponible",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listaVentas, key = { venta -> venta.id }) { venta ->
                        VentaGestorItemCard(
                            venta = venta,
                            onAuditarClick = {
                                navController.navigate("gestor_detail_screen/$username/${venta.id}/$modo") {
                                    popUpTo("gestor_detail_screen/$username/${venta.id}/$modo") { inclusive = true }
                                }
                            },
                            modo = modo
                        )

                    }
                }
            }

            // BOTON PARA VOLVER AL MENU
            OutlinedButton(
                onClick = {
                    navController.navigate("gestor_screen/$username") {
                        popUpTo("gestor_screen/$username") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(4.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Volver al Menú")
            }
        }
    }
}