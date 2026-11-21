package com.example.casosemestral.ui.gestor.comisiones

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
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.casosemestral.model.reporteDeComisiones.ReporteComisionDetalleEntity
import com.example.casosemestral.model.reporteDeComisiones.ReporteComisionEntity
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComisionScreen(
    navController: NavController,
    vm: ComisionViewModel,
    username: String
) {
    val vendedores by vm.vendedores.collectAsState()
    val vendedorSeleccionado by vm.vendedorSeleccionado.collectAsState()
    val borradorReporte by vm.borradorReporte.collectAsState()
    val borradorDetalles by vm.borradorDetalles.collectAsState()
    val reportesHistoricos by vm.reportesHistoricos.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var dropdownExpandido by remember { mutableStateOf(false) }
    val scrollContenido = rememberScrollState()

    // Escucha eventos/alertas (como intentar emitir sin ventas disponibles)
    LaunchedEffect(Unit) {
        vm.mensajeEvento.collect { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
        }
    }

    val textoCampoVendedor = when {
        vendedorSeleccionado.isNotBlank() -> vendedorSeleccionado
        vendedores.isEmpty() -> "No hay vendedores registrados..."
        else -> "Selecciona un vendedor..."
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // CABECERA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(color = MaterialTheme.colorScheme.primary)
            ) {
                val tituloCabecera = if (vm.modo == "HISTORICO") {
                    "Histórico de Comisiones"
                } else {
                    "Cálculo de Comisiones"
                }
                Text(
                    text = tituloCabecera,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // SELECTOR DE VENDEDOR DEBAJO DE LA CABECERA (SIN SELECCIÓN INICIAL)
            ExposedDropdownMenuBox(
                expanded = dropdownExpandido,
                onExpandedChange = {
                    if (vendedores.isNotEmpty()) dropdownExpandido = !dropdownExpandido
                }
            ) {
                OutlinedTextField(
                    value = textoCampoVendedor,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Vendedor") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpandido)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = dropdownExpandido,
                    onDismissRequest = { dropdownExpandido = false }
                ) {
                    vendedores.forEach { vendedor ->
                        DropdownMenuItem(
                            text = { Text(vendedor) },
                            onClick = {
                                dropdownExpandido = false
                                vm.seleccionarVendedor(vendedor)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ÁREA PRINCIPAL CON SCROLL PARA BORRADORES LARGOS O HISTÓRICOS EXTENSOS
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollContenido),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when {
                    vendedores.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (vm.modo == "HISTORICO") {
                                    "Sin vendedores disponibles para ver su histórico..."
                                } else {
                                    "Sin vendedores registrados para calcular..."
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    vm.modo == "HISTORICO" -> {
                        if (reportesHistoricos.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No hay reportes emitidos para este vendedor...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        } else {
                            reportesHistoricos.forEach { reporte ->
                                ReporteEmitidoItemCard(reporte = reporte)
                            }
                        }
                    }

                    else -> {
                        BorradorComisionDetailCard(
                            reporte = borradorReporte,
                            detalles = borradorDetalles,
                            onEmitirClick = { vm.emitirReporteActual() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // BOTÓN VOLVER AL MENÚ
            OutlinedButton(
                onClick = {
                    navController.navigate("gestor_screen/$username") {
                        popUpTo("gestor_screen/$username") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Volver al Menú")
            }
        }

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

@Composable
private fun BorradorComisionDetailCard(
    reporte: ReporteComisionEntity?,
    detalles: List<ReporteComisionDetalleEntity>,
    onEmitirClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Borrador de Reporte",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (reporte != null) {
                Text("Vendedor: ${reporte.vendedor}", style = MaterialTheme.typography.bodyMedium)
                Text("Desde: ${reporte.fecha_inicio}", style = MaterialTheme.typography.bodySmall)
                Text("Hasta: ${reporte.fecha_corte}", style = MaterialTheme.typography.bodySmall)
                Text(
                    text = "Porcentaje: ${(reporte.porcentaje * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            HorizontalDivider()

            if (detalles.isEmpty()) {
                Text(
                    text = "No hay ventas aprobadas disponibles a la fecha...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            } else {
                Text(
                    text = "Ventas incluidas (${detalles.size}):",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                detalles.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${item.cliente} · ${item.convenio_o_servicio}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Venta: $${String.format(Locale.US, "%.2f", item.monto_venta)} (${item.fecha_venta})",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Text(
                            text = "+$${String.format(Locale.US, "%.2f", item.comision_venta)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            HorizontalDivider()

            val totalVentas = reporte?.total_ventas ?: 0.0
            val totalComision = reporte?.total_comision ?: 0.0
            Text(
                text = "Total ventas: $${String.format(Locale.US, "%.2f", totalVentas)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Total comisión: $${String.format(Locale.US, "%.2f", totalComision)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onEmitirClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ReceiptLong, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Emitir reporte")
            }
        }
    }
}

@Composable
private fun ReporteEmitidoItemCard(
    reporte: ReporteComisionEntity
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Reporte #${reporte.id} · ${reporte.vendedor}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Emitido: ${reporte.fecha_emision}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Período: ${reporte.fecha_inicio} -> ${reporte.fecha_corte}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Ventas: ${reporte.cantidad_ventas} | Total: $${String.format(Locale.US, "%.2f", reporte.total_ventas)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Comisión (${(reporte.porcentaje * 100).toInt()}%): $${String.format(Locale.US, "%.2f", reporte.total_comision)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}