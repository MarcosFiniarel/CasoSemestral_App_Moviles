package com.example.casosemestral.ui.administrador.usuarioHabilitado

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.casosemestral.model.data.local.AppDatabase
import com.example.casosemestral.model.data.repository.UserRepository
import com.example.casosemestral.model.user.UserEntity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PanelDeUsuarioViewModel internal constructor(
    app: Application,
    private val repo: UserRepository
) : AndroidViewModel(app) {

    constructor(app: Application) : this(
        app = app,
        repo = UserRepository(
            userDao = AppDatabase.getDatabase(app).userDao()
        )
    )

    val usuarios: StateFlow<List<UserEntity>> = repo.listarUsuarios()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _mensajeEvento = MutableSharedFlow<String>()
    val mensajeEvento: SharedFlow<String> = _mensajeEvento.asSharedFlow()

    fun cambiarEstadoCuenta(usuario: UserEntity, nuevoEstado: Boolean) {
        viewModelScope.launch {
            val fechaActual = UserEntity.obtenerFechaActual()
            repo.actualizarEstado(
                idUser = usuario.id,
                estado = nuevoEstado,
                actualizacion = fechaActual
            )
            val estadoTexto = if (nuevoEstado) "habilitada" else "inhabilitada"
            _mensajeEvento.emit("Cuenta de ${usuario.nombre} (${usuario.perfil}) $estadoTexto")
        }
    }
}