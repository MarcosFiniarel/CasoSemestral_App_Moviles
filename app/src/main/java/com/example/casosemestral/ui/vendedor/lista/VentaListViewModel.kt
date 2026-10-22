package com.example.casosemestral.ui.vendedor.lista

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.casosemestral.model.data.local.AppDatabase
import com.example.casosemestral.model.data.repository.VentaRepository
import com.example.casosemestral.model.data.venta.VentaEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
}