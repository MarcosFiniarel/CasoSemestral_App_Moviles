package com.example.casosemestral.ui.login

import com.example.casosemestral.model.data.user.UserEntity

// SEALED CLASS PARA CONTROLAR LOS ESTADOS DE VALIDACION DEL LOGIN
sealed class LoginResult {
    data class Exito(val usuario: UserEntity) : LoginResult()
    object CredencialesInvalidas : LoginResult()
    object CuentaInhabilitada : LoginResult()
    object UsuarioNoEncontrado : LoginResult()
}