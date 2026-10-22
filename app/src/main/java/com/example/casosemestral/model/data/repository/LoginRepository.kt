package com.example.casosemestral.model.data.repository

import com.example.casosemestral.ui.login.LoginResult
import com.example.casosemestral.model.data.user.UserDao

class LoginRepository(
    private val userDao: UserDao
) {
    // FUNCION PARA AUTENTICAR USUARIO
    suspend fun autenticarUsuario(
        username: String,
        claveIngresada: String,
        perfilSeleccionado: String
    ): LoginResult {

        // SE BUSCA UN MATCH EN LA BD CON EL USERNAME Y SU ROL
        val usuario = userDao.obtenerUsuarioPorNombreYPerfil(username, perfilSeleccionado)
            ?: return LoginResult.UsuarioNoEncontrado

        // SE VERIFICA LA CONTRASEÑA
        if (usuario.clave != claveIngresada) {
            return LoginResult.CredencialesInvalidas
        }

        // SE VERIFICA QUE LA CUENTA ESTE ACTIVA
        if (!usuario.estadoCuenta) {
            return LoginResult.CuentaInhabilitada
        }

        // SI SE SUPERARON TODAS LAS VALIDACIONES SE RETORNA EL ESTADO DE EXITO
        return LoginResult.Exito(usuario)
    }
}