package com.example.casosemestral.model.data.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    // FUNCION PARA BUSCAR UN USUARIO POR USERNAME Y PERFIL (VENDEDOR / GESTOR)
    @Query("SELECT * FROM user WHERE nombre = :username AND perfil = :perfil")
    suspend fun obtenerUsuarioPorNombreYPerfil(username: String, perfil: String): UserEntity?

}