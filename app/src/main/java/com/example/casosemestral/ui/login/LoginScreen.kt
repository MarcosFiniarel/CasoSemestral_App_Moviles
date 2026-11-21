package com.example.casosemestral.ui.login

import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.KeyOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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

@OptIn(ExperimentalMaterial3Api::class)
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

                // Dentro de tu LoginScreen (junto a tus otros estados de UI)
                var rolSeleccionado by remember { mutableStateOf("Vendedor") }
                val roles = listOf("Vendedor", "Gestor", "Administrador")
                var expanded by remember { mutableStateOf(false) }

                // CAMPO ROL CON MENÚ DESPLEGABLE (Exposed Dropdown Menu)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        value = rolSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rol") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        roles.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    rolSeleccionado = selectionOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(vertical = 12.dp))

                // BOTÓN DE INICIO DE SESIÓN
                Button(
                    onClick = {

                        val perfilSeleccionado = rolSeleccionado

                        vm.autenticar(
                            perfilSeleccionado = perfilSeleccionado,
                            onExito = { username ->

                                when(perfilSeleccionado){
                                    "Vendedor" -> {
                                        navController.navigate("vendedor_screen/$username") {
                                            popUpTo("login_screen") { inclusive = true }
                                        }
                                    }
                                    "Gestor" -> {
                                        navController.navigate("gestor_screen/$username") {
                                            popUpTo("login_screen") { inclusive = true }
                                        }
                                    }
                                    "Administrador" -> {
                                        navController.navigate("vendedor_screen/$username") {
                                            popUpTo("login_screen") { inclusive = true }
                                        }
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
                        Text(when(rolSeleccionado){
                            "Vendedor" -> {
                                "Entrar como vendedor"
                            }
                            "Gestor" -> {
                                "Entrar como gestor"
                            }
                            "Administrador" -> {
                                "Entrar como administrador"
                            }
                            else -> {""}
                        })
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