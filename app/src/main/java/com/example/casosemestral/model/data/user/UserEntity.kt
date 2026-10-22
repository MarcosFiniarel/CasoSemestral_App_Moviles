package com.example.casosemestral.model.data.user

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(
    tableName = "user",
    indices = [Index(value = ["nombre","perfil"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val clave: String,
    val perfil: String,
    val zonaSucursal: String,
    val estadoCuenta: Boolean = true,
    val fechaCreacion: String,
    val fechaActualizacion: String
){
    // FUNCION PARA OBTENER LA FECHA DE SISTEMA PARA AUTOMATIZAR PARAMETROS COMO LA FECHA Y HORA DE LA VENTA O DE LAS ACTUALIZACIONES DE REGISTROS
    companion object{
        fun obtenerFechaActual(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return sdf.format(Date())
        }
    }
}