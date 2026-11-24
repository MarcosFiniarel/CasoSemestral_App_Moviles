package com.example.casosemestral.model.data.repository

import com.example.casosemestral.model.user.UserDao
import com.example.casosemestral.model.user.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userDao: UserDao
) {

    fun listarUsuarios(): Flow<List<UserEntity>>{
        return userDao.listarUsuarios()
    }

    suspend fun actualizarEstado(idUser: Long, estado: Boolean, actualizacion: String){
        userDao.actualizarEstado(idUser,estado,actualizacion)
    }

}