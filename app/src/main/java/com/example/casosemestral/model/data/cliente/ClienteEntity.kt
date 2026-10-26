package com.example.casosemestral.model.data.cliente

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cliente")
data class ClienteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val identificadorTributario: String,
    val correo: String,
    val telefono: String,
    val nombreMascosta: String?,
    val consentimiento: Boolean
)
