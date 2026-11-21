package com.example.casosemestral.ui.vendedor.lista

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.casosemestral.model.data.local.AppDatabase
import com.example.casosemestral.model.data.repository.VentaRepository
import com.example.casosemestral.model.venta.VentaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VentaListViewModel (
    app: Application,
    savedStateHandle: SavedStateHandle
): AndroidViewModel(app) {

    // SE INVOCA AL REPOSITORIO PARA UTILIZARLO PARA INSERTAR LOS DATOS DESPUES DE VALIDARLOS
    private val repo: VentaRepository = VentaRepository(
        AppDatabase.getDatabase(app).ventaDao()
    )

    // SE RECUPERA EL USERNAME DEL VENDEDOR DESDE LOS DATOS DE NAVEGACION
    private val username: String = savedStateHandle["username"] ?: ""

    // SE OBTIENE LA LISTA DE VENTAS COMO UN ESTADO
    val ventasState: StateFlow<List<VentaEntity>> = repo.obtenerVentasPorVendedor(username)
                                                        .stateIn(
                                                            scope = viewModelScope,
                                                            started = SharingStarted.WhileSubscribed(5000),
                                                            initialValue = emptyList()
                                                        )

    // FUNCION PARA ELIMINAR UNA VENTA DE LA BD
    fun eliminarVenta(venta: VentaEntity){
        viewModelScope.launch {
            repo.eliminarVenta(venta)
        }
    }

    private val _vendedorActual = MutableStateFlow("")

    fun setVendedor(vendedor: String) {
        if (_vendedorActual.value != vendedor) {
            _vendedorActual.value = vendedor
        }
    }

    fun marcarNovedadesVistas() {
        val vendedor = _vendedorActual.value
        if (vendedor.isNotBlank()) {
            viewModelScope.launch {
                repo.marcarVistasPorVendedor(vendedor)
            }
        }
    }

    // Dentro de VentaListViewModel:
    private val _idsConExclamacion = MutableStateFlow<Set<Long>>(emptySet())
    val idsConExclamacion: StateFlow<Set<Long>> = _idsConExclamacion.asStateFlow()

    private var novedadesCapturadas = false
    private val estadosAuditados = setOf("Aprobada", "Rechazada", "Corregir")

    fun cargarMarcasNovedad(ventasActuales: List<VentaEntity>) {
        if (novedadesCapturadas || ventasActuales.isEmpty()) return
        novedadesCapturadas = true

        _idsConExclamacion.value = ventasActuales
            .filter { it.estado in estadosAuditados && !it.visto_vendedor }
            .map { it.id }
            .toSet()
    }

    fun limpiarMarcasNovedad() {
        _idsConExclamacion.value = emptySet()
        novedadesCapturadas = false
    }

}