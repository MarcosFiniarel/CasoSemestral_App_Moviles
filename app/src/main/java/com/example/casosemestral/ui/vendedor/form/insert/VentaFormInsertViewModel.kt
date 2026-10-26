package com.example.casosemestral.ui.vendedor.form.insert

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.casosemestral.model.data.cliente.ClienteEntity
import com.example.casosemestral.model.data.local.AppDatabase
import com.example.casosemestral.model.data.repository.ClienteRepository
import com.example.casosemestral.model.data.repository.VentaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

// CLASE VIEWMODEL DEL FORM DE INSERCION DE LAS VENTAS
class VentaFormInsertViewModel (app: Application): AndroidViewModel(app){

    // SE INVOCA AL REPOSITORIO PARA UTILIZARLO PARA INSERTAR LOS DATOS DESPUES DE VALIDARLOS
    private val repo: VentaRepository = VentaRepository(
        AppDatabase.getDatabase(app).ventaDao()
    )

    // SE INVOCA EL REPO DE CLIENTES PARA USAR LA LISTA DE CLIENTES EN LA PANTALLA
    private val clienteRepository: ClienteRepository = ClienteRepository(
        AppDatabase.getDatabase(app).clienteDao()
    )

    // EXPONEMOS LA LISTA DE CLIENTES DESDE LA BD HACIA LA UI DE FORMA REACTIVA
    val listaClientes: StateFlow<List<ClienteEntity>> = clienteRepository.obtenerClientes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ESTADO INTERNO MUTABLE
    private val _estado = MutableStateFlow(VentaFormInsertUiState())

    // ESTADO EXPUESTO PARA LA UI
    val estado: StateFlow<VentaFormInsertUiState> = _estado

    // FUNCIONES DE MONITOREO DE CAMBIOS EN LOS CAMPOS DE FORM DE VENTAS

    fun onClienteChange(value: String){
        _estado.update {
            it.copy(cliente = value, errors = it.errors.copy(cliente = null))
        }
    }

    fun onConvenioOServicioChange(value: String){
        _estado.update {
            it.copy(convenioOServicio = value, errors = it.errors.copy(convenioOServicio = null))
        }
    }

    fun onPrestadorAsociadoChange(value: String){
        _estado.update {
            it.copy(prestadorAsociado = value, errors = it.errors.copy(prestadorAsociado = null))
        }
    }

    fun onMontoChange(value: Double){
        _estado.update {
            it.copy(monto = value, errors = it.errors.copy(monto = null))
        }
    }

    fun onRespaldoFotoChange(value: String){
        _estado.update {
            it.copy(respaldoFoto = value, errors = it.errors.copy(respaldoFoto = null))
        }
    }

    fun onEstadoChange(value: String){
        _estado.update {
            it.copy(estado = value, errors = it.errors.copy(estado = null))
        }
    }

    fun onFechaEnvioChange(value: String){
        _estado.update {
            it.copy(fechaEnvio = value, errors = it.errors.copy(fechaEnvio = null))
        }
    }

    // FUNCION DE LA VALIDACION DE FORM
    @RequiresApi(Build.VERSION_CODES.O)
    fun validarForm(): Boolean {

        val estadoActual = _estado.value

        val fechaHoy = LocalDate.now()

        val errorFecha = if (estadoActual.fechaEnvio.isBlank()) {
            "Campo Obligatorio"
        } else {
            val fechaIngresada = LocalDate.parse(estadoActual.fechaEnvio)
            if (fechaIngresada.isBefore(fechaHoy)) {
                "La fecha no puede ser anterior a hoy"
            } else {
                null
            }
        }

        val errores = VentaFormInsertUiError(
            if (estadoActual.cliente.isBlank()) "Campo Obligatorio" else null,
            if (estadoActual.convenioOServicio.isBlank()) "Campo Obligatorio" else null,
            if (estadoActual.prestadorAsociado.isBlank()) "Campo Obligatorio" else null,
            if (estadoActual.monto <= 0.0) "Valor no Válido" else null,
            if (estadoActual.respaldoFoto.isBlank()) "Campo Obligatorio" else null,
            if (estadoActual.estado.isBlank()) "Campo Obligatorio" else null,
            errorFecha
        )
        val hayError = listOfNotNull(
            errores.cliente,
            errores.convenioOServicio,
            errores.prestadorAsociado,
            errores.monto,
            errores.respaldoFoto,
            errores.estado,
            errores.fechaEnvio
        ).isNotEmpty()

        _estado.update { it.copy(errors = errores) }

        return !hayError
    }

    // FUNCION DE GUARDADO DE VENTA QUE UTILIZA EL REPOSITORIO DE VENTAS PARA PODER GRABAR EN LA BASE DE DATOS
    @RequiresApi(Build.VERSION_CODES.O)
    fun guardarVenta(vendedor: String, onSuccess: () -> Unit){
        if (!validarForm()){return}

        val estadoActual = _estado.value

        viewModelScope.launch {

            repo.guardarVenta(
                vendedorResponsable = vendedor,
                cliente = estadoActual.cliente,
                convenioOServicio = estadoActual.convenioOServicio,
                prestadorAsociado = estadoActual.prestadorAsociado,
                monto = estadoActual.monto,
                respaldoFoto = estadoActual.respaldoFoto,
                estado = estadoActual.estado,
                fechaEnvio = estadoActual.fechaEnvio
            )

            onSuccess()
        }
    }

    // FUNCION DE BLANQUEAMIENTO DE FORMULARIO
    fun resetearForm(){
        _estado.update { VentaFormInsertUiState() }
    }
}