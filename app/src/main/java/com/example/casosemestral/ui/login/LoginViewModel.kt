package com.example.casosemestral.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.casosemestral.model.data.local.AppDatabase
import com.example.casosemestral.model.data.repository.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel (app: Application): AndroidViewModel(app){

    // SE INVOCA AL REPOSITORIO PARA UTILIZARLO
    private val repo: LoginRepository = LoginRepository(
        AppDatabase.getDatabase(app).userDao()
    )

    // ESTADO INTERNO MUTABLE
    private val _estado = MutableStateFlow(LoginUiState())

    // ESTADO EXPUESTO PARA LA UI
    val estado: StateFlow<LoginUiState> = _estado

    // FUNCIONES DE MONITOREO DE CAMBIOS EN LOS CAMPOS DEL LOGIN

    fun onUsernameChange(value: String){
        _estado.update {
            it.copy(username = value, errors = it.errors.copy(username = null))
        }
    }

    fun onPasswordChange(value: String){
        _estado.update {
            it.copy(password = value, errors = it.errors.copy(password = null))
        }
    }

    fun onEsGestorChange(value: Boolean){
        _estado.update {
            it.copy(esGestor = value)
        }
    }

    // FUNCIÓN DE VALIDACIÓN LOCAL DEL FORMULARIO
    private fun validarForm(): Boolean {
        val actual = _estado.value
        val errores = LoginUiError(
            username = if (actual.username.isBlank()) "Campo Obligatorio" else null,
            password = if (actual.password.isBlank()) "Campo Obligatorio" else null
        )

        val hayErrores = listOfNotNull(errores.username, errores.password).isNotEmpty()

        _estado.update { it.copy(errors = errores) }

        return !hayErrores
    }

    // FUNCIÓN DE LOGIN QUE VALIDA Y LUEGO CONSUME EL REPO
    fun autenticar(perfilSeleccionado: String, onExito: (String) -> Unit, onError: (String) -> Unit) {

        if (!validarForm()) return

        viewModelScope.launch {

            _estado.update { it.copy(isLoading = true) }

            val actual = _estado.value

            // SE LLAMA AL REPOSITORY PARA USAR LA SEALED CLASS (LoginResult) PARA VALIDAR
            val resultado = repo.autenticarUsuario(
                username = actual.username,
                claveIngresada = actual.password,
                perfilSeleccionado = perfilSeleccionado
            )

            _estado.update { it.copy(isLoading = false) }

            // SE EVALUA EL RESULTADO DE LA AUTENTICACION
            when (resultado) {
                is LoginResult.Exito -> {
                    onExito(resultado.usuario.nombre)
                }
                is LoginResult.CredencialesInvalidas -> {
                    onError("Contraseña incorrecta")
                }
                is LoginResult.CuentaInhabilitada -> {
                    onError("Su cuenta se encuentra inhabilitada")
                }
                is LoginResult.UsuarioNoEncontrado -> {
                    onError("El usuario no existe para el perfil seleccionado")
                }
            }
        }
    }

}