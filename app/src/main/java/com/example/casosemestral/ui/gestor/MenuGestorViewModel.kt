package com.example.casosemestral.ui.gestor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.casosemestral.model.data.local.AppDatabase
import com.example.casosemestral.model.data.repository.VentaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MenuGestorViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = VentaRepository(
        AppDatabase.getDatabase(app).ventaDao()
    )

    val cantidadNuevasVentas: StateFlow<Int> = repo.contarNuevasGestor()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val cantidadHistoricoNuevo: StateFlow<Int> = repo.contarHistoricoNuevoParaGestor()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

}