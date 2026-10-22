package com.example.casosemestral.model.data.venta

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "venta")
data class VentaEntity(
    // LOS PARAMETROS INICIALIZADOS EN NULL SON NO OBLIGATORIOS AL MOMENTO DE CREAR EL REGISTRO
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fecha_y_hora: String,
    val vendedor_responsable: String,
    val cliente: String,
    val convenio_o_servicio: String,
    val prestador_asociado: String,
    val monto: Double,
    val codigo_autorizacion: Long? = null,
    val respaldo_fotografico: String,
    val estado: String = "Borrador",
    val fecha_envio: String,
    val fecha_revision: String? = null,
    val gestor: String? = null,
    val observaciones: String? = null,
    val motivo_rechazo: String? = null,
    val fecha_de_ultima_actualizacion: String
){
    // FUNCION PARA OBTENER LA FECHA DE SISTEMA PARA AUTOMATIZAR PARAMETROS COMO LA FECHA Y HORA DE LA VENTA O DE LAS ACTUALIZACIONES DE REGISTROS
    companion object{
        fun obtenerFechaActual(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return sdf.format(Date())
        }
    }
}