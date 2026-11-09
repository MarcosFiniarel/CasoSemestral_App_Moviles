package com.example.casosemestral.model.data.repository

import com.example.casosemestral.model.proveedor.ProveedorDao
import com.example.casosemestral.model.proveedor.ProveedorEntity
import kotlinx.coroutines.flow.Flow

class ProveedorRepository(
    private val proveedorDao: ProveedorDao
) {

    // FUNCION PARA OBTENER A TODOS LOS PROVEEDORES QUE PROVEEN UN DETERMINADO SERVICIO
    fun obtenerProveedoresDeServicio(servicioId: Long): Flow<List<ProveedorEntity>>{
        return proveedorDao.obtenerProveedoresPorServicioId(servicioId)
    }
}