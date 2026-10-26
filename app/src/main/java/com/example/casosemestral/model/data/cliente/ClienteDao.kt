package com.example.casosemestral.model.data.cliente

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {

    // FUNCION PARA INSERTAR CLIENTES
    @Insert
    suspend fun insertarCliente(cliente: ClienteEntity)

    // FUNCION PARA ACTUALIZAR CLIENTES
    @Update
    suspend fun actualizarCliente(cliente: ClienteEntity)

    // FUNCION PARA ELIMINAR CLIENTES
    @Delete
    suspend fun borrarCliente(cliente: ClienteEntity)

    // FUNCION PARA LISTAR TODOS LOS CLIENTES REGISTRADOS
    @Query("SELECT * FROM cliente")
    fun listarClientes(): Flow<List<ClienteEntity>>
}