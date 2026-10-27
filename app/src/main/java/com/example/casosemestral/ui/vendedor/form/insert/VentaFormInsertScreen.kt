package com.example.casosemestral.ui.vendedor.form.insert

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.casosemestral.metodosAux.NumericOutlinedTextField
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.time.Duration.Companion.milliseconds

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentaFormScreen(
    navController: NavController,
    vm: VentaFormInsertViewModel,
    username: String
){

    val estado by vm.estado.collectAsState()

    // ESTADO PARA PODER SCROLLEAR
    val scrollState = rememberScrollState()
    val scrollStateCliente = rememberScrollState()
    val scrollStateServicio = rememberScrollState()
    val scrollStateProveedor = rememberScrollState()

    // ESTADOS PARA EL SNACKBAR FLOTANTE
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(
        Modifier.fillMaxSize()
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {

            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(120.dp)
                    .background(color = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Registro de Venta",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState), // HACE QUE SOLO ESTE BLOQUE SEA SCROLLEABLE
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // OBSERVAMOS LA LISTA DE CLIENTES QUE VIENE DESDE EL VIEWMODEL
                val clientes by vm.listaClientes.collectAsState()
                var expandedCliente by remember { mutableStateOf(false) }

                // CAMPO CLIENTE CON MENÚ DESPLEGABLE
                ExposedDropdownMenuBox(
                    expanded = expandedCliente,
                    onExpandedChange = { expandedCliente = !expandedCliente },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = estado.cliente,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Cliente") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCliente) },
                        isError = estado.errors.cliente != null,
                        supportingText = {
                            estado.errors.cliente?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedCliente,
                        onDismissRequest = { expandedCliente = false }
                    ) {
                        Column(
                            modifier = Modifier.heightIn(max = 180.dp)
                                               .verticalScroll(scrollStateCliente)
                        ) {
                            if (clientes.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No hay clientes registrados") },
                                    onClick = { expandedCliente = false }
                                )
                            } else {
                                clientes.forEach { cliente ->

                                    val textoFila = "${cliente.nombre} (${cliente.correo})"

                                    DropdownMenuItem(
                                        text = { Text(textoFila) },
                                        onClick = {
                                            vm.onClienteChange(textoFila)
                                            expandedCliente = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // OBSERVAMOS LA LISTA DE SERVICIOS QUE VIENE DESDE EL VIEWMODEL
                val servicios by vm.listaServicios.collectAsState()
                var expandedServicios by remember { mutableStateOf(false) }

                // CAMPO CONVENIO O SERVICIO CON MENÚ DESPLEGABLE
                ExposedDropdownMenuBox(
                    expanded = expandedServicios,
                    onExpandedChange = { expandedServicios = !expandedServicios },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = estado.convenioOServicio,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Convenio o Servicio") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedServicios) },
                        isError = estado.errors.convenioOServicio != null,
                        supportingText = {
                            estado.errors.convenioOServicio?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedServicios,
                        onDismissRequest = { expandedServicios = false }
                    ) {
                        Column(
                            modifier = Modifier.heightIn(max = 180.dp)
                                .verticalScroll(scrollStateServicio)
                        ) {
                            if (servicios.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No hay servicios o convenios registrados") },
                                    onClick = { expandedServicios = false }
                                )
                            } else {
                                servicios.forEach { servicio ->
                                    DropdownMenuItem(
                                        text = { Text(servicio.nombre) },
                                        onClick = {
                                            vm.onConvenioOServicioChange(servicio.id,servicio.nombre)
                                            expandedServicios = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // OBSERVAMOS LA LISTA DE PRESTADORES ASOCIADOS QUE VIENE DESDE EL VIEWMODEL
                val proveedores by vm.listaProveedoresAsociados.collectAsState()
                var expandedProveedores by remember { mutableStateOf(false) }

                // CAMPO PRESTADORES ASOCIADOS CON MENÚ DESPLEGABLE
                ExposedDropdownMenuBox(
                    expanded = expandedProveedores,
                    onExpandedChange = { expandedProveedores = !expandedProveedores },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = estado.prestadorAsociado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Prestador Asociado") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProveedores) },
                        isError = estado.errors.prestadorAsociado != null,
                        supportingText = {
                            estado.errors.prestadorAsociado?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedProveedores,
                        onDismissRequest = { expandedProveedores = false }
                    ) {
                        Column(
                            modifier = Modifier.heightIn(max = 180.dp)
                                .verticalScroll(scrollStateProveedor)
                        ) {
                            if (proveedores.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No hay prestadores asociados registrados") },
                                    onClick = { expandedProveedores = false }
                                )
                            } else {
                                proveedores.forEach { proveedor ->
                                    DropdownMenuItem(
                                        text = { Text(proveedor.nombre) },
                                        onClick = {
                                            vm.onPrestadorAsociadoChange(proveedor.nombre)
                                            expandedProveedores = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // CAMPO MONTO
                NumericOutlinedTextField(
                    value = estado.monto,
                    onValueChange = vm::onMontoChange,
                    label = "Monto",
                    isError = estado.errors.monto != null,
                    supportingText = {
                        estado.errors.monto?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // CAMPO DE RESPALDO FOTOGRAFICO
                // ESCUCHAMOS EL RESULTADO QUE DEVOLVIO LA PANTALLA DEL SCANNER
                val qrResultadoState = navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.getLiveData<String>("qr_result_key")
                    ?.observeAsState()

                val qrResultado = qrResultadoState?.value

                // SI HAY UN RESULTADO NUEVO SE LE PASA AL VIEWMODEL Y SE LIMPIA LA CLAVE
                LaunchedEffect(qrResultado) {
                    qrResultado?.let { codigoQr ->
                        vm.onRespaldoFotoChange(codigoQr)

                        // SE LIMPIA LA CLAVE PARA QUE AL VOLVERSE A USAR ESTE VACIA
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.remove<String>("qr_result_key")
                    }
                }

                // FILA RESPALDO FOTOGRAFICO (CON BOTON DE CAMARA / QR)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // CAMPO DE TEXTO DE SOLO LECTURA QUE MUESTRA EL RESULTADO DEL QR
                    OutlinedTextField(
                        value = estado.respaldoFoto,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Respaldo Fotografía (QR)") },
                        modifier = Modifier.weight(1f),
                        maxLines = 3,
                        enabled = true,
                        isError = estado.errors.respaldoFoto != null,
                        supportingText = {
                            estado.errors.respaldoFoto?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            errorLabelColor = MaterialTheme.colorScheme.error
                        )
                    )

                    // BOTON DE ICONO PARA ABRIR LA PANTALLA DE LA CAMARA QR
                    IconButton(
                        onClick = {
                            navController.navigate("qr_scanner_screen/$username")
                        },
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Escanear QR",
                            tint = if (estado.errors.respaldoFoto == null) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error
                        )
                    }
                }

                // CAMPO ESTADO CON MENÚ DESPLEGABLE (Exposed Dropdown Menu)
                val options = listOf("Borrador", "Enviado")
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        // SE MARCA EL CAMPO COMO ANCLA DEL DESPLEGABLE CON MENUANCHOR
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        value = estado.estado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estado") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        isError = estado.errors.estado != null,
                        supportingText = {
                            estado.errors.estado?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        options.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {

                                    vm.onEstadoChange(selectionOption)

                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // FECHA DE ENVIO
                val context = LocalContext.current
                val calendar = Calendar.getInstance()

                val datePickerDialog = DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val fechaFormateada =
                            String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                        vm.onFechaEnvioChange(fechaFormateada)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )

                // FILA QUE CONTIENE EL CAMPO DE TEXTO Y EL BOTÓN DEL CALENDARIO
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // CAMPO DE TEXTO DE SOLO LECTURA QUE MUESTRA LA FECHA SELECCIONADA
                    OutlinedTextField(
                        value = estado.fechaEnvio,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fecha de Envío") },
                        modifier = Modifier.weight(1f),
                        enabled = false,
                        isError = estado.errors.fechaEnvio != null,
                        supportingText = {
                            estado.errors.fechaEnvio?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = if (estado.errors.fechaEnvio != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                            disabledLabelColor = if (estado.errors.fechaEnvio != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                            disabledSupportingTextColor = MaterialTheme.colorScheme.error
                        )
                    )

                    // BOTON ICONO QUE DESPLIEGA EL CALENDARIO DEL SISTEMA
                    IconButton(
                        onClick = { datePickerDialog.show() },
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Seleccionar fecha",
                            tint = if (estado.errors.fechaEnvio == null) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            val textoExito = "Venta registrada con éxito"

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // BOTON SALIR SIN GUARDAR
                Button(
                    onClick = {
                        // VUELVE A LA PANTALLA DE MENU SIN GUARDAR LA VENTA
                        navController.navigate("vendedor_screen/$username") {
                            popUpTo("vendedor_screen/$username") { inclusive = true }
                        }
                    },
                    modifier = Modifier.weight(1f).heightIn(56.dp)
                ) {
                    Text(
                        "Salir sin guardar",
                        maxLines = 2,
                        textAlign = TextAlign.Center
                    )
                }

                // BOTON ENVIAR Y SALIR
                Button(
                    onClick = {
                        if (vm.validarForm()) {
                            vm.guardarVenta(username) {
                                // VUELVE A LA PANTALLA DE MENU LUEGO DE GUARDAR LA VENTA
                                vm.resetearForm()

                                navController.navigate("vendedor_screen/$username?mensaje=$textoExito") {
                                    popUpTo("vendedor_screen/$username") { inclusive = true }
                                }

                            }
                        }
                    },
                    modifier = Modifier.weight(1f).heightIn(56.dp)
                ) {
                    Text(
                        "Registrar y salir",
                        maxLines = 2,
                        textAlign = TextAlign.Center
                    )
                }

                // BOTON ENVIAR Y REINICIAR EL FORM
                Button(
                    onClick = {
                        if (vm.validarForm()) {
                            vm.guardarVenta(username) {
                                // RESETEA EL FORM LUEGO DE GUARDAR EL REGISTRO
                                vm.resetearForm()
                                scope.launch {
                                    snackbarHostState.showSnackbar(textoExito)
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f).heightIn(56.dp)
                ) {
                    Text(
                        "Registrar",
                        maxLines = 2,
                        textAlign = TextAlign.Center
                    )
                }
            }

        }

        // SNACKBAR HOST FLOTANTE CON DISEÑO DE ÉXITO
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
                        Text(
                            text = "OK",
                            color = MaterialTheme.colorScheme.primary
                        )
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