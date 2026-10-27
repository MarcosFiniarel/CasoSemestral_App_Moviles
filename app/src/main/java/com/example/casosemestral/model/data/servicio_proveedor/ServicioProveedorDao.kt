package com.example.casosemestral.model.data.servicio_proveedor

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.casosemestral.model.data.servicio.ServicioEntity

@Dao
interface ServicioProveedorDao {

    @Insert
    suspend fun insertarRelacionesSP(ref: List<ServicioProveedorEntity>)

    @Query("DELETE FROM servicio_proveedor WHERE servicioId = :servicioId AND proveedorId = :proveedorId")
    suspend fun eliminarRelacionSP(servicioId: Long, proveedorId: Long)
}