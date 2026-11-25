package com.example.casosemestral.model.sucursalesAdmins

data class SucursalAdmin(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val company: CompanySucursal
)

data class CompanySucursal(
    val name: String
)