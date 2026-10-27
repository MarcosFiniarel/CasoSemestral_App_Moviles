package com.example.casosemestral.model.data.servicio

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ServicioDao {

    // FUNCION PARA INSERTAR SERVICIOS O CONVENIOS
    @Insert
    suspend fun insertarServicio(servicio: ServicioEntity)

    // FUNCION PARA ACTUALIZAR CONVENIOS O SERVICIOS
    @Update
    suspend fun actualizarServicio(servicio: ServicioEntity)

    // FUNCION PARA ELIMINAR CONVENIOS O SERVICIOS
    @Delete
    suspend fun eliminarServicio(servicio: ServicioEntity)

    // FUNCION PARA LISTAR TODOS LOS CONVENIOS O SERVICIOS
    @Query("SELECT * FROM servicio")
    fun listarServicios(): Flow<List<ServicioEntity>>
}