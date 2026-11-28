package com.example.casosemestral.model.data.repository

import com.example.casosemestral.model.reporteDeComisiones.ReporteComisionDao
import com.example.casosemestral.model.reporteDeComisiones.ReporteComisionDetalleEntity
import com.example.casosemestral.model.reporteDeComisiones.ReporteComisionEntity
import com.example.casosemestral.model.venta.VentaDao
import kotlinx.coroutines.flow.Flow

class ComisionRepository(
    private val reporteComisionDao: ReporteComisionDao,
    private val ventaDao: VentaDao
) {

    companion object {
        const val PORCENTAJE_COMISION_DEFECTO = 0.05 // 5%
    }

    // PREPARA LA CABECERA Y EL DETALLE DEL REPORTE SOLO EN LO VISUAL
    suspend fun prepararCalculoComision(
        nombreVendedor: String,
        porcentaje: Double = PORCENTAJE_COMISION_DEFECTO
    ): Pair<ReporteComisionEntity, List<ReporteComisionDetalleEntity>> {
        val fechaCorte = ReporteComisionEntity.obtenerFechaActual()
        val ultimoReporte = reporteComisionDao.obtenerUltimoReporteDeVendedor(nombreVendedor)
        val ventasPendientes = ventaDao.obtenerVentasAprobadasNoComisionadas(nombreVendedor)

        val fechaInicio = when {
            ultimoReporte != null -> ultimoReporte.fecha_corte
            ventasPendientes.isNotEmpty() -> ventasPendientes.first().fecha_y_hora
            else -> fechaCorte
        }

        val detalles = ventasPendientes.map { venta ->
            ReporteComisionDetalleEntity(
                reporte_id = 0L,
                venta_id = venta.id,
                cliente = venta.cliente,
                convenio_o_servicio = venta.convenio_o_servicio,
                fecha_venta = venta.fecha_y_hora,
                monto_venta = venta.monto,
                comision_venta = venta.monto * porcentaje
            )
        }

        val reporteCalculado = ReporteComisionEntity(
            vendedor = nombreVendedor,
            fecha_inicio = fechaInicio,
            fecha_corte = fechaCorte,
            fecha_emision = fechaCorte,
            porcentaje = porcentaje,
            cantidad_ventas = detalles.size,
            total_ventas = detalles.sumOf { it.monto_venta },
            total_comision = detalles.sumOf { it.comision_venta }
        )

        return reporteCalculado to detalles
    }

    // SE EMITE EL REPORTE MOSTRADO EN PANTALLA Y SE GUARDA EN PERSISTENCIA Y MARCA LAS VENTAS EN EL COMO CONTADAS EN UNA COMISION PARA EVITAR DUPLICIDAD
    suspend fun emitirReporteComision(
        reporte: ReporteComisionEntity,
        detalles: List<ReporteComisionDetalleEntity>
    ): Long {
        if (detalles.isEmpty()) return -1L

        val reporteConFechaEmision = reporte.copy(
            fecha_emision = ReporteComisionEntity.obtenerFechaActual()
        )

        val reporteId = reporteComisionDao.emitirReporteConDetalles(
            reporte = reporteConFechaEmision,
            detalles = detalles
        )

        val idsVentas = detalles.map { it.venta_id }
        ventaDao.marcarVentasComoComisionadas(idsVentas)

        return reporteId
    }

    fun obtenerTodosLosReportes(): Flow<List<ReporteComisionEntity>> {
        return reporteComisionDao.obtenerTodosLosReportes()
    }

    fun obtenerReportesPorVendedor(nombreVendedor: String): Flow<List<ReporteComisionEntity>> {
        return reporteComisionDao.obtenerReportesPorVendedor(nombreVendedor)
    }

    fun obtenerDetallesDeReporte(reporteId: Long): Flow<List<ReporteComisionDetalleEntity>> {
        return reporteComisionDao.obtenerDetallesDeReporte(reporteId)
    }
}