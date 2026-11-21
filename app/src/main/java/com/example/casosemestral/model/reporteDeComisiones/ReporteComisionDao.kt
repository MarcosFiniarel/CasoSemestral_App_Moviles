package com.example.casosemestral.model.reporteDeComisiones

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ReporteComisionDao {

    @Insert
    suspend fun insertarReporte(reporte: ReporteComisionEntity): Long

    @Insert
    suspend fun insertarDetalles(detalles: List<ReporteComisionDetalleEntity>)

    // Inserta el encabezado del reporte y todas sus líneas de detalle de forma atómica
    @Transaction
    suspend fun emitirReporteConDetalles(
        reporte: ReporteComisionEntity,
        detalles: List<ReporteComisionDetalleEntity>
    ): Long {
        val reporteId = insertarReporte(reporte)
        val detallesConId = detalles.map { it.copy(reporte_id = reporteId) }
        insertarDetalles(detallesConId)
        return reporteId
    }

    // Último reporte emitido para un vendedor (útil para determinar fecha_inicio automática)
    @Query(
        """
        SELECT * FROM reporte_comision
        WHERE vendedor = :nombreVendedor
        ORDER BY fecha_corte DESC, id DESC
        LIMIT 1
        """
    )
    suspend fun obtenerUltimoReporteDeVendedor(nombreVendedor: String): ReporteComisionEntity?

    // Historial general de reportes emitidos (o filtrar por vendedor si lo necesitas)
    @Query("SELECT * FROM reporte_comision ORDER BY fecha_emision DESC, id DESC")
    fun obtenerTodosLosReportes(): Flow<List<ReporteComisionEntity>>

    @Query("SELECT * FROM reporte_comision WHERE vendedor = :nombreVendedor ORDER BY fecha_emision DESC, id DESC")
    fun obtenerReportesPorVendedor(nombreVendedor: String): Flow<List<ReporteComisionEntity>>

    // Detalle de ventas liquidadas dentro de un reporte específico
    @Query("SELECT * FROM reporte_comision_detalle WHERE reporte_id = :reporteId ORDER BY id ASC")
    fun obtenerDetallesDeReporte(reporteId: Long): Flow<List<ReporteComisionDetalleEntity>>
}