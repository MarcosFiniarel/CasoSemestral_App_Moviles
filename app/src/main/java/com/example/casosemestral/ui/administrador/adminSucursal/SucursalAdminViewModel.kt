package com.example.casosemestral.ui.administrador.adminSucursal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.casosemestral.model.data.repository.SucursalAdminRepository
import com.example.casosemestral.model.sucursalesAdmins.SucursalAdmin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SucursalAdminViewModel internal constructor(
    private val repository: SucursalAdminRepository
) : ViewModel() {

    constructor() : this(
        repository = SucursalAdminRepository()
    )

    private val _adminList = MutableStateFlow<List<SucursalAdmin>>(emptyList())
    val adminList: StateFlow<List<SucursalAdmin>> = _adminList

    init {
        fetchSucursalAdmins()
    }

    private fun fetchSucursalAdmins() {
        viewModelScope.launch {
            try {
                _adminList.value = repository.getSucursalAdmins()
            } catch (e: Exception) {
                println("Error al obtener datos: ${e.localizedMessage}")
            }
        }
    }
}