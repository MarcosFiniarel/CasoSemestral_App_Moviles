package com.example.casosemestral.ui.vendedor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.casosemestral.model.data.local.AppDatabase
import com.example.casosemestral.model.data.repository.VentaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MenuViewModel(app: Application): AndroidViewModel(app) {

    private val repo = VentaRepository(
        AppDatabase.getDatabase(app).ventaDao()
    )

    private val _vendedorActual = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val cantidadNuevasVentas: StateFlow<Int> = _vendedorActual
        .flatMapLatest { vendedor ->
            if (vendedor.isBlank()) {
                flowOf(0)
            } else {
                repo.contarNovedadesParaVendedor(vendedor)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    fun setVendedor(vendedor: String) {
        if (_vendedorActual.value != vendedor) {
            _vendedorActual.value = vendedor
        }
    }

}