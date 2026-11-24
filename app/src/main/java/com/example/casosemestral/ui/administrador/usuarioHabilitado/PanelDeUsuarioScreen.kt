package com.example.casosemestral.ui.administrador.usuarioHabilitado

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.casosemestral.model.user.UserEntity

@Composable
fun PanelDeUsuarioScreen(
    navController: NavController,
    vm: PanelDeUsuarioViewModel,
    username: String,
    onVolverMenu: () -> Unit = {
        navController.navigate("administrador_screen/$username") {
            popUpTo("administrador_screen") { inclusive = true }
        }
    }
) {
    val usuarios by vm.usuarios.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollEstado = rememberScrollState()

    val gestores by remember(usuarios) {
        derivedStateOf { usuarios.filter { it.perfil.equals("Gestor", ignoreCase = true) } }
    }
    val vendedores by remember(usuarios) {
        derivedStateOf { usuarios.filter { it.perfil.equals("Vendedor", ignoreCase = true) } }
    }

    LaunchedEffect(Unit) {
        vm.mensajeEvento.collect { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
        }
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
                Text(
                    text = "Gestión de Usuarios",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // CONTENIDO SCROLLEABLE CON DOS TARJETAS SEPARADAS
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollEstado),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SeccionPerfilUsuariosCard(
                    titulo = "Gestores",
                    usuarios = gestores,
                    textoVacio = "No hay gestores registrados...",
                    onToggleEstado = { usuario, nuevoEstado ->
                        vm.cambiarEstadoCuenta(usuario, nuevoEstado)
                    }
                )

                SeccionPerfilUsuariosCard(
                    titulo = "Vendedores",
                    usuarios = vendedores,
                    textoVacio = "No hay vendedores registrados...",
                    onToggleEstado = { usuario, nuevoEstado ->
                        vm.cambiarEstadoCuenta(usuario, nuevoEstado)
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onVolverMenu,
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
private fun SeccionPerfilUsuariosCard(
    titulo: String,
    usuarios: List<UserEntity>,
    textoVacio: String,
    onToggleEstado: (UserEntity, Boolean) -> Unit
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
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider()

            if (usuarios.isEmpty()) {
                Text(
                    text = textoVacio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            } else {
                usuarios.forEachIndexed { index, usuario ->
                    FilaUsuarioSwitch(
                        usuario = usuario,
                        onCheckedChange = { checked -> onToggleEstado(usuario, checked) }
                    )
                    if (index < usuarios.lastIndex) {
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaUsuarioSwitch(
    usuario: UserEntity,
    onCheckedChange: (Boolean) -> Unit
) {
    val etiquetaEstado = if (usuario.estadoCuenta) "Habilitado" else "Deshabilitado"
    val colorEstado = if (usuario.estadoCuenta) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = usuario.nombre,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Perfil: ${usuario.perfil} · ${usuario.zonaSucursal}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = etiquetaEstado,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = colorEstado
            )
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = usuario.estadoCuenta,
                onCheckedChange = onCheckedChange
            )
        }
    }
}