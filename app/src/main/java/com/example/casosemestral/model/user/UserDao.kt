package com.example.casosemestral.model.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // FUNCION PARA BUSCAR UN USUARIO POR USERNAME Y PERFIL (VENDEDOR / GESTOR)
    @Query("SELECT * FROM user WHERE nombre = :username AND perfil = :perfil")
    suspend fun obtenerUsuarioPorNombreYPerfil(username: String, perfil: String): UserEntity?

    @Query("SELECT * FROM user WHERE perfil IN ('Gestor','Vendedor') ORDER BY nombre ASC, perfil ASC")
    fun listarUsuarios(): Flow<List<UserEntity>>

    @Query("UPDATE user SET estadoCuenta = :estado, fechaActualizacion = :actualizacion WHERE id = :userId")
    suspend fun actualizarEstado(userId: Long, estado: Boolean, actualizacion: String)

}