package com.example.casosemestral.ui.gestor.lista

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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GestorListaViewModel(
    app: Application,
    savedStateHandle: SavedStateHandle
): AndroidViewModel(app) {

    // SE INVOCA AL REPOSITORIO PARA UTILIZARLO PARA INSERTAR LOS DATOS DESPUES DE VALIDARLOS
    private val repo: VentaRepository = VentaRepository(
        AppDatabase.getDatabase(app).ventaDao()
    )

    // SE RECUPERA EL MODO QUE FUE ENVIADO POR LA RUTA DE NAVEGACION ("AUDITAR" o "HISTORICO")
    private val modo: String = savedStateHandle["modo"] ?: ""

    // LISTA REACTIVA QUE CAMBIA EL ORIGEN SEGUN EL BOTON QUE SE ACCIONE EN EL MENU
    val ventas: StateFlow<List<VentaEntity>> = when (modo) {
        "AUDITAR" -> repo.obtenerVentasEnviadas()
        "HISTORICO" -> repo.obtenerVentasAuditadas()
        else -> flowOf(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun marcarVistaGestor(){
        viewModelScope.launch{
            repo.marcarVistasPorGestor()
        }
    }

    fun marcarHistoricoVistoGestor() {
        viewModelScope.launch {
            repo.marcarHistoricoVistoPorGestor()
        }
    }

    private val _idsConExclamacion = MutableStateFlow<Set<Long>>(emptySet())
    val idsConExclamacion: StateFlow<Set<Long>> = _idsConExclamacion.asStateFlow()

    private var novedadesCapturadas = false

    fun cargarMarcasNovedad(ventasActuales: List<VentaEntity>) {
        if (novedadesCapturadas || ventasActuales.isEmpty()) return
        novedadesCapturadas = true

        _idsConExclamacion.value = when (modo) {
            "AUDITAR" -> ventasActuales
                .filter { it.estado == "Enviado" && !it.visto_gestor }
                .map { it.id }
                .toSet()

            "HISTORICO" -> ventasActuales
                .filter { !it.historico_visto_gestor }
                .map { it.id }
                .toSet()

            else -> emptySet()
        }
    }

    fun limpiarMarcasNovedad() {
        _idsConExclamacion.value = emptySet()
        novedadesCapturadas = false
    }
}