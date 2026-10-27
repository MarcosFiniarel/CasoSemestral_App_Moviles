package com.example.casosemestral.model.data.servicio_proveedor

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.casosemestral.model.data.servicio.ServicioEntity
import com.example.casosemestral.model.data.proveedor.ProveedorEntity

@Entity(
    tableName = "servicio_proveedor",
    primaryKeys = ["servicioId", "proveedorId"],
    indices = [
        Index(value = ["servicioId"]),
        Index(value = ["proveedorId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = ServicioEntity::class,
            parentColumns = ["id"],
            childColumns = ["servicioId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProveedorEntity::class,
            parentColumns = ["id"],
            childColumns = ["proveedorId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class ServicioProveedorEntity(
    val servicioId: Long,
    val proveedorId: Long
)
