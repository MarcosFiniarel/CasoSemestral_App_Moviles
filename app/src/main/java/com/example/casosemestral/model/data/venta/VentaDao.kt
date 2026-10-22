package com.example.casosemestral.model.data.venta

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VentaDao {
    // FUNCION PARA INSERTAR VENTAS
    @Insert
    suspend fun insertar(venta: VentaEntity)

    // FUNCION PARA ACTUALIZAR VENTAS
    @Update
    suspend fun actualizar(venta: VentaEntity)

    // FUNCION PARA ACTUALIZAR VENTAS
    @Delete
    suspend fun borrar(venta: VentaEntity)

    // FUNCION PARA BUSCAR UNA VENTA POR ID
    @Query("SELECT * FROM venta WHERE id = :ventaId")
    suspend fun obtenerVentaPorId(ventaId: Long): VentaEntity?

    // FUNCION PARA TRAER LA LISTA DE VENTAS DE UN VENDEDOR DETERMINADO
    @Query("SELECT * FROM venta WHERE vendedor_responsable = :nombreVendedor ORDER BY id DESC")
    fun obtenerVentasPorVendedor(nombreVendedor: String): Flow<List<VentaEntity>>

    // FUNCION PARA TRAER LA LISTA DE TODAS LAS VENTAS QUE SE ENCUENTREN REGISTRADAS
    @Query("SELECT * FROM venta ORDER BY id DESC")
    fun obtenerVentas(): Flow<List<VentaEntity>>

}