package com.example.casosemestral.ui.vendedor.form.update

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.casosemestral.metodosAux.NumericOutlinedTextField
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentaFormUpdateScreen(
    navController: NavController,
    vm: VentaFormUpdateViewModel,
    ventaId: Long?,
    username: String
){

    val estado by vm.estado.collectAsState()
    val scrollState = rememberScrollState()
    val scrollStateServicio = rememberScrollState()
    val scrollStateProveedor = rememberScrollState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Box(
            modifier = Modifier.fillMaxWidth()
                .height(120.dp)
                .background(color = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Actualizando venta: $ventaId",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.align(Alignment.BottomStart).padding(12.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

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
                    value = estado.convenioNuevo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Convenio o Servicio") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedServicios) },
                    isError = estado.errors.convenioNuevo != null,
                    supportingText = {
                        estado.errors.convenioNuevo?.let {
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
                                        vm.onConvenioNuevoChange(servicio.id,servicio.nombre)
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
                    value = estado.prestadorNuevo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Prestador Asociado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProveedores) },
                    isError = estado.errors.prestadorNuevo != null,
                    supportingText = {
                        estado.errors.prestadorNuevo?.let {
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
                                        vm.onPrestadorNuevoChange(proveedor.nombre)
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
                value = estado.montoNuevo,
                onValueChange = vm::onMontoNuevoChange,
                label = "Monto",
                isError = estado.errors.montoNuevo != null,
                supportingText = {
                    estado.errors.montoNuevo?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // CAMPO RESPALDO FOTOGRAFICO
            // ESCUCHAMOS EL RESULTADO QUE DEVOLVIO LA PANTALLA DEL SCANNER
            val qrResultadoState = navController.currentBackStackEntry
                ?.savedStateHandle
                ?.getLiveData<String>("qr_result_key")
                ?.observeAsState()

            val qrResultado = qrResultadoState?.value

            // SI HAY UN RESULTADO NUEVO SE LE PASA AL VIEWMODEL Y SE LIMPIA LA CLAVE
            LaunchedEffect(qrResultado) {
                qrResultado?.let { codigoQr ->
                    vm.onRespaldoNuevoChange(codigoQr)

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
                    value = estado.respaldoNuevo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Respaldo Fotografía (QR)") },
                    modifier = Modifier.weight(1f),
                    maxLines = 3,
                    enabled = true,
                    isError = estado.errors.respaldoNuevo != null,
                    supportingText = {
                        estado.errors.respaldoNuevo?.let {
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
                        tint = if (estado.errors.respaldoNuevo == null) MaterialTheme.colorScheme.primary
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
                    value = estado.estadoNuevo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Estado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    isError = estado.errors.estadoNuevo != null,
                    supportingText = {
                        estado.errors.estadoNuevo?.let {
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

                                vm.onEstadoNuevoChange(selectionOption)

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
                    vm.onFechaEnvioNuevoChange(fechaFormateada)
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
                    value = estado.fechaEnvioNuevo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha de Envío") },
                    modifier = Modifier.weight(1f),
                    enabled = false,
                    isError = estado.errors.fechaEnvioNuevo != null,
                    supportingText = {
                        estado.errors.fechaEnvioNuevo?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = if (estado.errors.fechaEnvioNuevo != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                        disabledLabelColor = if (estado.errors.fechaEnvioNuevo != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
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
                        tint = if (estado.errors.fechaEnvioNuevo == null) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // BOTON SALIR SIN GUARDAR
            Button(
                onClick = {
                    // VUELVE A LA PANTALLA DE LISTAR VENTAS SIN ACTUALIZAR NADA
                    navController.navigate("vendedor_lista_screen/$username"){
                        popUpTo("vendedor_lista_screen/$username") { inclusive = true }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar",
                    maxLines = 1,
                    textAlign = TextAlign.Center)
            }

            // BOTON ENVIAR Y SALIR
            Button(
                onClick = {
                    if (vm.validarForm()){
                        vm.actualizarVenta{
                            // VUELVE A LA PANTALLA DE LISTAR VENTAS LUEGO DE ACTUALIZAR LA VENTA
                            navController.navigate("vendedor_lista_screen/$username?mensaje=Venta Actualizada"){
                                popUpTo("vendedor_lista_screen/$username") { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Actualizar",
                    maxLines = 1,
                    textAlign = TextAlign.Center)
            }
        }

    }
}