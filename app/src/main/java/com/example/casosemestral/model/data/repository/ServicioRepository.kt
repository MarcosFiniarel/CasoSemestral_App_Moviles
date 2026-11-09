package com.example.casosemestral.model.data.repository

import com.example.casosemestral.model.servicio.ServicioDao
import com.example.casosemestral.model.servicio.ServicioEntity
import kotlinx.coroutines.flow.Flow

class ServicioRepository(
    private val servicioDao: ServicioDao
) {

    //  FUNCION PARA LISTAR TODOS LOS SERVICIOS REGISTRADOS
    fun obtenerServicios(): Flow<List<ServicioEntity>> {
        return servicioDao.listarServicios()
    }

}