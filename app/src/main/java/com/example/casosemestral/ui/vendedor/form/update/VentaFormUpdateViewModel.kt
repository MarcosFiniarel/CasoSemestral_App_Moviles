package com.example.casosemestral.ui.vendedor.form.update

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.casosemestral.model.data.local.AppDatabase
import com.example.casosemestral.model.data.proveedor.ProveedorEntity
import com.example.casosemestral.model.data.repository.ProveedorRepository
import com.example.casosemestral.model.data.repository.ServicioRepository
import com.example.casosemestral.model.data.repository.VentaRepository
import com.example.casosemestral.model.data.servicio.ServicioEntity
import com.example.casosemestral.model.data.venta.VentaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class VentaFormUpdateViewModel (
    app: Application,
    savedStateHandle: SavedStateHandle
): AndroidViewModel(app) {

    // SE INVOCA AL REPOSITORIO PARA UTILIZARLO PARA INSERTAR LOS DATOS DESPUES DE VALIDARLOS
    private val repo: VentaRepository = VentaRepository(
        AppDatabase.getDatabase(app).ventaDao()
    )

    // SE INVOCA AL REPOSITORIO DE SERVICIOS
    private val servicioRepository: ServicioRepository = ServicioRepository(
        AppDatabase.getDatabase(app).servicioDao()
    )

    // SE INVOCA AL REPOSITORIO DE PROVEEDORES
    private val proveedorRepository: ProveedorRepository = ProveedorRepository(
        AppDatabase.getDatabase(app).proveedorDao()
    )

    // SE RECUPERA EL ID DE LA VENTA DESDE LOS DATOS DE NAVEGACION
    private val ventaId: Long = savedStateHandle["ventaId"] ?: 0L

    // SE ESTABLECE LA REFERENCIA A LA ENTIDAD ORIGINAL PARA LUEGO HACER EL UPDATE
    private var ventaOriginal: VentaEntity? = null

    // EXPONEMOS LA LISTA DE SERVICIOS DESDE LA BD HACIA LA UI DE FORMA REACTIVA
    val listaServicios: StateFlow<List<ServicioEntity>> = servicioRepository.obtenerServicios()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ESTADO INTERNO PARA EL ID DEL SERVICIO SELECCIONADO EN EDICIÓN
    private val _servicioIdSeleccionado = MutableStateFlow<Long?>(null)

    // LISTA DE PROVEEDORES DEPENDIENTE DEL SERVICIO SELECCIONADO
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val listaProveedoresAsociados: StateFlow<List<ProveedorEntity>> = _servicioIdSeleccionado
        .flatMapLatest { servicioId ->
            if (servicioId == null) {
                flowOf(emptyList())
            } else {
                proveedorRepository.obtenerProveedoresDeServicio(servicioId)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ESTADO INTERNO MUTABLE
    private val _estado = MutableStateFlow(VentaFormUpdateUiState())

    // ESTADO EXPUESTO PARA LA UI
    val estado: StateFlow<VentaFormUpdateUiState> = _estado

    // CARGAR LOS DATOS DE LA VENTA ORIGINAL AL INICIALIZAR EL VIEW MODEL
    init {
        cargarVentaOriginal()
    }

    // FUNCION PARA CARGAR LOS DATOS DE LA VENTA ORIGINAL A LOS ESTADOS
    private fun cargarVentaOriginal(){
        viewModelScope.launch {
            ventaOriginal = repo.obtenerVentaPorId(ventaId)
            ventaOriginal?.let { venta ->
                _estado.value = VentaFormUpdateUiState(venta)
            }
        }
    }

    // FUNCIONES DE MONITOREO DE CAMBIOS EN LOS CAMPOS DE FORM DE VENTAS

    fun onConvenioNuevoChange(servicioId: Long, value: String){
        _estado.update {
            it.copy(convenioNuevo = value, errors = it.errors.copy(convenioNuevo = null), prestadorNuevo = "")
        }
        _servicioIdSeleccionado.value = servicioId
    }

    fun onPrestadorNuevoChange(value: String){
        _estado.update {
            it.copy(prestadorNuevo = value, errors = it.errors.copy(prestadorNuevo = null))
        }
    }

    fun onMontoNuevoChange(value: Double){
        _estado.update {
            it.copy(montoNuevo = value, errors = it.errors.copy(montoNuevo = null))
        }
    }

    fun onRespaldoNuevoChange(value: String){
        _estado.update {
            it.copy(respaldoNuevo = value, errors = it.errors.copy(respaldoNuevo = null))
        }
    }

    fun onEstadoNuevoChange(value: String){
        _estado.update {
            it.copy(estadoNuevo = value, errors = it.errors.copy(estadoNuevo = null))
        }
    }

    fun onFechaEnvioNuevoChange(value: String){
        _estado.update {
            it.copy(fechaEnvioNuevo = value, errors = it.errors.copy(fechaEnvioNuevo = null))
        }
    }

    // FUNCION DE LA VALIDACION DE FORM
    @RequiresApi(Build.VERSION_CODES.O)
    fun validarForm(): Boolean {

        val estadoActual = _estado.value

        val fechaHoy = LocalDate.now()

        val errorFecha = if (estadoActual.fechaEnvioNuevo.isBlank()) {
            "Campo Obligatorio"
        } else {
            val fechaIngresada = LocalDate.parse(estadoActual.fechaEnvioNuevo)
            if (fechaIngresada.isBefore(fechaHoy)) {
                "La fecha no puede ser anterior a hoy"
            } else {
                null
            }
        }

        val errores = VentaFormUpdateUiError(
            if (estadoActual.convenioNuevo.isBlank()) "Campo Obligatorio" else null,
            if (estadoActual.prestadorNuevo.isBlank()) "Campo Obligatorio" else null,
            if (estadoActual.montoNuevo <= 0.0) "Valor no Válido" else null,
            if (estadoActual.respaldoNuevo.isBlank()) "Campo Obligatorio" else null,
            if (estadoActual.estadoNuevo.isBlank()) "Campo Obligatorio" else null,
            errorFecha
        )
        val hayError = listOfNotNull(
            errores.convenioNuevo,
            errores.prestadorNuevo,
            errores.montoNuevo,
            errores.respaldoNuevo,
            errores.estadoNuevo,
            errores.fechaEnvioNuevo
        ).isNotEmpty()

        _estado.update { it.copy(errors = errores) }

        return !hayError
    }

    // FUNCION DE GUARDADO DE VENTA QUE UTILIZA EL REPOSITORIO DE VENTAS PARA PODER GRABAR EN LA BASE DE DATOS
    @RequiresApi(Build.VERSION_CODES.O)
    fun actualizarVenta(onSuccess: () -> Unit){
        if (!validarForm()){return}

        val estadoActual = _estado.value
        val ventaActual = ventaOriginal

        if (ventaActual != null) {
             viewModelScope.launch {

                 repo.actualizarVentaVendedor(
                     venta = ventaActual,
                     convenioNuevo = estadoActual.convenioNuevo,
                     prestadorNuevo = estadoActual.prestadorNuevo,
                     montoNuevo = estadoActual.montoNuevo,
                     respaldoNuevo = estadoActual.respaldoNuevo,
                     estadoNuevo = estadoActual.estadoNuevo,
                     fechaEnvioNuevo = estadoActual.fechaEnvioNuevo
                 )

                 onSuccess()
             }
         }
    }
}