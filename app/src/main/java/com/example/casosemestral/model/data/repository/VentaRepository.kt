package com.example.casosemestral.model.data.repository

import com.example.casosemestral.model.data.venta.VentaDao
import com.example.casosemestral.model.data.venta.VentaEntity
import kotlinx.coroutines.flow.Flow
import kotlin.String

// EL REPOSITORIO DE VENTAS ESTA DESTINADO A TENER LA LOGICA DE INSERCION EN LA BASE DE DATOS HACIENDO USO DEL DAO Y SIENDO INVOCADA EN EL VIEWMODEL DE VENTAS PARA FUNGIR DE INTERMEDIARIO ENTRE ESTAS
class VentaRepository (
    private val ventaDao: VentaDao
){
    // FUNCION PARA GUARDAR LAS VENTAS EN LA BD
    suspend fun guardarVenta(
        vendedorResponsable: String,
        cliente: String,
        convenioOServicio: String,
        prestadorAsociado: String,
        monto: Double,
        respaldoFoto: String,
        estado: String,
        fechaEnvio: String
    ){
        // SE OBTIENE LA FECHA ACTUAL VIA SISTEMA CON LA FUNCION IMPLANTADA EN VENTAENTITY
        val fechaActual = VentaEntity.obtenerFechaActual()

        // SE CONTRUYE EL REGISTRO CON TODOS LOS PARAMETROS OBLIGATORIOS YA REVISADOS U OBTENIDOS VIA SISTEMA PARA ASEGURAR SU INTEGRIDAD
        val nuevaVenta = VentaEntity(
            fecha_y_hora = fechaActual,
            vendedor_responsable = vendedorResponsable,
            cliente = cliente,
            convenio_o_servicio = convenioOServicio,
            prestador_asociado = prestadorAsociado,
            monto = monto,
            respaldo_fotografico = respaldoFoto,
            estado = estado,
            fecha_de_ultima_actualizacion = fechaActual,
            fecha_envio = fechaEnvio
        )

        // SE HACE USO DEL METODO DE INSERCION CONTRUIDO EN EL DAO DE VENTAS
        ventaDao.insertar(nuevaVenta)
    }

    // FUNCION PARA ACTUALIZAR LAS VENTAS EN LA BD POR EL VENDEDOR
    suspend fun actualizarVentaVendedor(
        venta: VentaEntity,
        convenioNuevo: String,
        prestadorNuevo: String,
        montoNuevo: Double,
        respaldoNuevo: String,
        estadoNuevo: String,
        fechaEnvioNuevo: String
    ){
        val ventaActualizada = venta.copy(
            fecha_de_ultima_actualizacion = VentaEntity.obtenerFechaActual(),
            convenio_o_servicio = convenioNuevo,
            prestador_asociado = prestadorNuevo,
            monto = montoNuevo,
            respaldo_fotografico = respaldoNuevo,
            estado = estadoNuevo,
            fecha_envio = fechaEnvioNuevo
        )

        ventaDao.actualizar(ventaActualizada)
    }

    // FUNCION PARA BORRAR LAS VENTAS EN LA BD
    suspend fun eliminarVenta(venta: VentaEntity){
        ventaDao.borrar(venta)
    }

    // FUNCION PARA TRAER LAS VENTAS DE UN VENDEDOR ESPECIFICO Y RETORNARLAS
    fun obtenerVentasPorVendedor(vendedorResponsable: String): Flow<List<VentaEntity>>{
        return ventaDao.obtenerVentasPorVendedor(vendedorResponsable)
    }

    // BUSCAR VENTA POR ID
    suspend fun obtenerVentaPorId(ventaId:Long): VentaEntity?{
        return ventaDao.obtenerVentaPorId(ventaId)
    }
}