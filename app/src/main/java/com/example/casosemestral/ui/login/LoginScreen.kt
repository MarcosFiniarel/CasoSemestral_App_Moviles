package com.example.casosemestral.ui.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.KeyOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController,
    vm: LoginViewModel
){

    val estado by vm.estado.collectAsState()
    var showPass by remember{ mutableStateOf(value = false) }

    // ESTADOS PARA CONTROLAR EL SNACKBAR FLOTANTE CON CORRUTINAS
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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
                    text = "Inicio de Sesión",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.align(Alignment.BottomStart)
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // CAMPO DE USUARIO
                OutlinedTextField(
                    value = estado.username,
                    onValueChange = { vm.onUsernameChange(it) },
                    label = { Text("Usuario") },
                    isError = estado.errors.username != null,
                    supportingText = {
                        estado.errors.username?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // CAMPO DE CONTRASEÑA
                OutlinedTextField(
                    value = estado.password,
                    onValueChange = { vm.onPasswordChange(it) },
                    label = { Text("Contraseña") },
                    visualTransformation = if (showPass)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = { showPass = !showPass },
                        ) {
                            if (showPass) Icon(Icons.Default.KeyOff, contentDescription = "Ocultar", tint = MaterialTheme.colorScheme.primary)
                            else Icon(Icons.Default.Key, contentDescription = "Ver", tint = MaterialTheme.colorScheme.secondary)
                        }
                    },
                    isError = estado.errors.password != null,
                    supportingText = {
                        estado.errors.password?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // SWITCH PARA SELECCIONAR EL PERFIL (Vendedor / Gestor)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (estado.esGestor) "Perfil: Gestor" else "Perfil: Vendedor",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = estado.esGestor,
                        onCheckedChange = { vm.onEsGestorChange(it) }
                    )
                }

                // BOTÓN DE INICIO DE SESIÓN
                Button(
                    onClick = {

                        val perfilSeleccionado = if (estado.esGestor) "Gestor" else "Vendedor"

                        vm.autenticar(
                            perfilSeleccionado = perfilSeleccionado,
                            onExito = { username ->

                                if (estado.esGestor) {
                                    navController.navigate("gestor_screen") {
                                        popUpTo("login_screen") { inclusive = true }
                                    }
                                } else {
                                    navController.navigate("vendedor_screen/$username") {
                                        popUpTo("login_screen") { inclusive = true }
                                    }
                                }
                            },
                            onError = { mensajeError ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(mensajeError)
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !estado.isLoading
                ) {
                    if (estado.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(if (estado.esGestor) "Entrar como Gestor" else "Entrar como Vendedor")
                    }
                }
            }
        }

        // SNACKBAR HOST FLOTANTE CON DISEÑO PERSONALIZADO E ICONO
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) { data ->
            Snackbar(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(data.visuals.message)
                }
            }
        }
    }
}