package com.example.casosemestral.ui.gestor.lista.form

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun GestorFormScreen(
    navController: NavController,
    vmF: GestorFormViewModel,
    ventaId: Long,
    username: String,
    modo: String
){
    // SE TRAEN LOS ESTADOS DESDE EL VM DEL FORM
    val venta by vmF.venta.collectAsState()
    val estado by vmF.estado.collectAsState()

    // SE DISPARA LA CARGA DE VENTAS AL INICIAR LA PANTALLA
    LaunchedEffect(ventaId) {
        vmF.cargarVenta(ventaId)
    }

    // TEXTO DINAMICO SEGUN EL MODO
    val tituloCabecera = when (modo) {
        "APROBAR" -> "Aprobar Venta #${ventaId}"
        "RECHAZO" -> "Rechazar Venta #${ventaId}"
        "CORREGIR" -> "Solicitar Corrección #${ventaId}"
        else -> "Auditoría de Venta"
    }

    val textoBotonConfirmar = when (modo) {
        "APROBAR" -> "Confirmar Aprobación"
        "RECHAZO" -> "Confirmar Rechazo"
        "CORREGIR" -> "Enviar Solicitud de Corrección"
        else -> "Confirmar"
    }

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
                text = tituloCabecera,
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
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Resolución de Auditoría",
                            style = MaterialTheme.typography.titleMedium
                        )

                        // CAMPO DE OBSERVACIONES: SE MUESTRA EN APROBAR Y RECHAZAR (OPCIONAL) Y CORREGIR (OBLIGATORIO)
                        OutlinedTextField(
                            value = estado.observacion,
                            onValueChange = { vmF.onObservacionChange(it) },
                            label = {
                                Text(
                                    if (modo == "CORREGIR") "Observaciones (Obligatorio)"
                                    else "Observaciones (Opcional)"
                                )
                            },
                            isError = estado.errors.observacion != null,
                            supportingText = {
                                estado.errors.observacion?.let { errorMsg ->
                                    Text(
                                        text = errorMsg,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5
                        )

                        // CAMPO DE MOTIVO DE RECHAZO: EXCLUSIVO PARA MODO RECHAZO Y ES OBLIGATORIO
                        if (modo == "RECHAZO") {
                            OutlinedTextField(
                                value = estado.motivoRechazo,
                                onValueChange = { vmF.onMotivoRechazoChange(it) },
                                label = { Text("Motivo del rechazo (Obligatorio)") },
                                isError = estado.errors.motivoRechazo != null,
                                supportingText = {
                                    estado.errors.motivoRechazo?.let { errorMsg ->
                                        Text(
                                            text = errorMsg,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                maxLines = 5
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // BOTON PRINCIPAL DE CONFIRMACION
                        Button(
                            onClick = {
                                if(vmF.validarForm()) {
                                    vmF.actualizarVenta(
                                        venta = venta!!,
                                        gestor = username,
                                        onSuccess = {
                                            navController.navigate("gestor_lista_ventas_screen/$username/AUDITAR") {
                                                popUpTo("gestor_lista_ventas_screen/$username/AUDITAR") {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (modo == "RECHAZO") MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = when (modo) {
                                    "APROBAR" -> Icons.Default.CheckCircle
                                    "RECHAZO" -> Icons.Default.Delete
                                    else -> Icons.Default.PendingActions
                                },
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(textoBotonConfirmar)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                navController.navigate("gestor_detail_screen/$username/${ventaId}/AUDITAR") {
                    popUpTo("gestor_detail_screen/$username/${ventaId}/AUDITAR") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cancelar y Volver")
        }
    }
}