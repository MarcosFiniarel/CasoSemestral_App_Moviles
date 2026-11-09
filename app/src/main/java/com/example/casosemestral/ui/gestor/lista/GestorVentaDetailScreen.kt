package com.example.casosemestral.ui.gestor.lista

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestorVentaDetailScreen(
    navController: NavController,
    vm: GestorListaViewModel,
    ventaId: Long,
    username: String,
    modo: String
) {
    // SE BUSCA LA VENTA POR SU ID EN LA LISTA DE VENTAS QUE NOS TRAJIMOS DE LA BD PARA NO VOLVER A HACER LA CONSULTA A LA BD
    val listaVentas by vm.ventas.collectAsState()
    val venta = listaVentas.find { it.id == ventaId }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(color = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "Detalle de Venta #${ventaId}",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (venta == null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("Cargando o registro no encontrado...")
            }
        } else {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Información Completa del Registro",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        HorizontalDivider(thickness = 1.dp)

                        AtributoItem("ID Venta", "#${venta.id}")
                        AtributoItem("Fecha y Hora Creación", venta.fecha_y_hora)
                        AtributoItem("Vendedor Responsable", venta.vendedor_responsable)
                        AtributoItem("Cliente", venta.cliente)
                        AtributoItem("Convenio / Servicio", venta.convenio_o_servicio)
                        AtributoItem("Prestador Asociado", venta.prestador_asociado)
                        AtributoItem("Monto", "$${venta.monto}")
                        AtributoItem("Respaldo Fotográfico (QR)", venta.respaldo_fotografico)
                        AtributoItem("Estado", venta.estado)
                        AtributoItem("Fecha Envío", venta.fecha_envio)
                        AtributoItem("Última Actualización", venta.fecha_de_ultima_actualizacion)

                        if (modo == "HISTORICO"){
                            HorizontalDivider(thickness = 1.dp)

                            AtributoItem("Gestor Asignado", venta.gestor ?: "No asignado")
                            AtributoItem("Fecha Revisión", venta.fecha_revision ?: "Pendiente")
                            AtributoItem("Código Autorización",venta.codigo_autorizacion?.toString() ?: "Sin código")
                            AtributoItem("Observaciones",venta.observaciones ?: "Sin observaciones")
                            AtributoItem("Motivo Rechazo", venta.motivo_rechazo ?: "N/A")
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        if (venta.estado == "Enviado") {
                            Button(
                                onClick = {
                                    // pendiente
                                }
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Aprobar")
                            }

                            Button(
                                onClick = {
                                    // pendiente
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Rechazar")
                            }

                            Button(
                                onClick = {
                                    // pendiente
                                }
                            ) {
                                Icon(Icons.Default.PendingActions, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Solicitar Correccion")
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = venta.estado,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                navController.navigate("gestor_lista_ventas_screen/$username/$modo") {
                    popUpTo("gestor_lista_ventas_screen/$username/$modo") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            if (modo == "AUDITAR") {
                Text("Volver a Auditoria de Ventas")
            } else {
                Text("Volver al Historico de Ventas")
            }
        }
    }
}

// COMPONENTE AUXILIAR PARA DAR FORMATO DE CLAVE / VALOR A CADA CAMPO
@Composable
private fun AtributoItem(label: String, valor: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}