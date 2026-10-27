package com.example.casosemestral.model.data.proveedor

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProveedorDao {

    // FUNCION PARA INERTAR PROVEEDORES
    @Insert
    suspend fun insertarProveedor(proveedor: ProveedorEntity)

    // FUNCION PARA ACTUALIZAR PROVEEDORES
    @Update
    suspend fun actualizarProveedor(proveedor: ProveedorEntity)

    // FUNCION PARA ELIMINAR PROVEEDORES
    @Delete
    suspend fun eliminarProveedor(proveedor: ProveedorEntity)

    // FUNCION PARA LISTAR A TODOS LOS PROVEEDORES
    @Query("SELECT * FROM proveedor")
    fun listarProveedores(): Flow<List<ProveedorEntity>>

    // FUNCIONAR PARA LISTAR A LOS PROVEEDORES QUE PROPORCIONAN UN DETERMINADO SERVICIO
    @Query("""
        SELECT p.* FROM proveedor p
        INNER JOIN servicio_proveedor ref ON p.id = ref.proveedorId
        WHERE ref.servicioId = :servicioId
    """)
    fun obtenerProveedoresPorServicioId(servicioId: Long): Flow<List<ProveedorEntity>>
}