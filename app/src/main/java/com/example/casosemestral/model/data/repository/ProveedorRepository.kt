package com.example.casosemestral.model.data.repository

import com.example.casosemestral.model.data.proveedor.ProveedorDao
import com.example.casosemestral.model.data.proveedor.ProveedorEntity
import kotlinx.coroutines.flow.Flow

class ProveedorRepository(
    private val proveedorDao: ProveedorDao
) {

    // FUNCION PARA OBTENER A TODOS LOS PROVEEDORES QUE PROVEEN UN DETERMINADO SERVICIO
    fun obtenerProveedoresDeServicio(servicioId: Long): Flow<List<ProveedorEntity>>{
        return proveedorDao.obtenerProveedoresPorServicioId(servicioId)
    }
}