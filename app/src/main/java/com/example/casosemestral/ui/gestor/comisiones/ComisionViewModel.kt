package com.example.casosemestral.ui.gestor.comisiones

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.casosemestral.model.data.local.AppDatabase
import com.example.casosemestral.model.data.repository.ComisionRepository
import com.example.casosemestral.model.reporteDeComisiones.ReporteComisionDetalleEntity
import com.example.casosemestral.model.reporteDeComisiones.ReporteComisionEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ComisionViewModel(
    app: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(app) {

    private val db = AppDatabase.getDatabase(app)
    private val repo = ComisionRepository(
        reporteComisionDao = db.reporteComisionDao(),
        ventaDao = db.ventaDao()
    )

    val modo: String = savedStateHandle["modo"] ?: "REPORTE"

    val vendedores: StateFlow<List<String>> = db.ventaDao()
        .obtenerVendedoresRegistrados()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _vendedorSeleccionado = MutableStateFlow("")
    val vendedorSeleccionado: StateFlow<String> = _vendedorSeleccionado.asStateFlow()

    private val _borradorReporte = MutableStateFlow<ReporteComisionEntity?>(null)
    val borradorReporte: StateFlow<ReporteComisionEntity?> = _borradorReporte.asStateFlow()

    private val _borradorDetalles = MutableStateFlow<List<ReporteComisionDetalleEntity>>(emptyList())
    val borradorDetalles: StateFlow<List<ReporteComisionDetalleEntity>> = _borradorDetalles.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val reportesHistoricos: StateFlow<List<ReporteComisionEntity>> = _vendedorSeleccionado
        .flatMapLatest { vendedor ->
            if (vendedor.isBlank()) {
                flowOf(emptyList())
            } else {
                repo.obtenerReportesPorVendedor(vendedor)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _mensajeEvento = MutableSharedFlow<String>()
    val mensajeEvento: SharedFlow<String> = _mensajeEvento.asSharedFlow()

    fun seleccionarVendedor(vendedor: String) {
        if (vendedor.isBlank()) return
        _vendedorSeleccionado.value = vendedor
        if (modo == "REPORTE") {
            cargarBorrador(vendedor)
        }
    }

    fun cargarBorrador(vendedor: String = _vendedorSeleccionado.value) {
        if (vendedor.isBlank()) {
            _borradorReporte.value = null
            _borradorDetalles.value = emptyList()
            return
        }
        viewModelScope.launch {
            val (reporte, detalles) = repo.prepararCalculoComision(vendedor)
            _borradorReporte.value = reporte
            _borradorDetalles.value = detalles
        }
    }

    fun emitirReporteActual() {
        val vendedor = _vendedorSeleccionado.value
        if (vendedor.isBlank()) {
            viewModelScope.launch {
                _mensajeEvento.emit("Selecciona un vendedor primero")
            }
            return
        }

        val reporteActual = _borradorReporte.value
        val detallesActuales = _borradorDetalles.value

        if (reporteActual == null || detallesActuales.isEmpty()) {
            viewModelScope.launch {
                _mensajeEvento.emit("No hay ventas aprobadas disponibles a la fecha para emitir el reporte")
            }
            return
        }

        viewModelScope.launch {
            val idGenerado = repo.emitirReporteComision(reporteActual, detallesActuales)
            if (idGenerado > 0L) {
                _mensajeEvento.emit("Reporte #$idGenerado emitido correctamente")
                // Refresca el borrador para dejarlo vacío y listo para futuros cortes
                cargarBorrador(vendedor)
            } else {
                _mensajeEvento.emit("No hay ventas disponibles para emitir el reporte")
            }
        }
    }
}