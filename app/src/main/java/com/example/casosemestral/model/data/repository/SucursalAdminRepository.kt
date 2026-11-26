package com.example.casosemestral.model.data.repository

import com.example.casosemestral.model.sucursalesAdmins.SucursalAdmin
import com.example.casosemestral.model.sucursalesAdmins.remote.RetrofitInstance

open class SucursalAdminRepository {
    open suspend fun getSucursalAdmins(): List<SucursalAdmin> {
        return RetrofitInstance.api.getSucursalAdmins()
    }
}