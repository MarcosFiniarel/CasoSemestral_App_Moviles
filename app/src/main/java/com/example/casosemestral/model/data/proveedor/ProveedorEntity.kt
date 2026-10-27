package com.example.casosemestral.model.data.proveedor

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "proveedor",
    indices = [Index(value = ["nombre"], unique = true)]
)
data class ProveedorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val detalle: String?
)
