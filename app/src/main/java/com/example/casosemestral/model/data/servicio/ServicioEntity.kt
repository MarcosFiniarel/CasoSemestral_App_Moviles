package com.example.casosemestral.model.data.servicio

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "servicio",
    indices = [Index(value = ["nombre"], unique = true)]
)
data class ServicioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val detalle: String?
)
