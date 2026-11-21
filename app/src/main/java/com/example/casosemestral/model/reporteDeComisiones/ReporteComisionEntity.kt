package com.example.casosemestral.model.reporteDeComisiones

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "reporte_comision")
data class ReporteComisionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vendedor: String,
    val fecha_inicio: String,
    val fecha_corte: String,
    val fecha_emision: String,
    val porcentaje: Double = 0.05,
    val cantidad_ventas: Int = 0,
    val total_ventas: Double = 0.0,
    val total_comision: Double = 0.0
) {
    companion object {
        fun obtenerFechaActual(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return sdf.format(Date())
        }
    }
}