package com.example.casosemestral.model.data.repository

import com.example.casosemestral.model.data.cliente.ClienteDao
import com.example.casosemestral.model.data.cliente.ClienteEntity
import kotlinx.coroutines.flow.Flow

class ClienteRepository(
    private val clienteDao: ClienteDao
) {

    // FUNCION PARA LISTAR A TODOS LOS CLIENTES REGISTRADOS
    fun obtenerClientes(): Flow<List<ClienteEntity>>{
        return clienteDao.listarClientes()
    }

}