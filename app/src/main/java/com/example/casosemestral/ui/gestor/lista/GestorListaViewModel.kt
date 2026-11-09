package com.example.casosemestral.ui.gestor.lista

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.casosemestral.model.data.local.AppDatabase
import com.example.casosemestral.model.data.repository.VentaRepository
import com.example.casosemestral.model.venta.VentaEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

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
}