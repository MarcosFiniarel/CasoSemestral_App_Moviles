package com.example.casosemestral.model.reporteDeComisiones

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reporte_comision_detalle",
    foreignKeys = [
        ForeignKey(
            entity = ReporteComisionEntity::class,
            parentColumns = ["id"],
            childColumns = ["reporte_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["reporte_id"])]
)
data class ReporteComisionDetalleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reporte_id: Long,
    val venta_id: Long,
    val cliente: String,
    val convenio_o_servicio: String,
    val fecha_venta: String,
    val monto_venta: Double,
    val comision_venta: Double
)
