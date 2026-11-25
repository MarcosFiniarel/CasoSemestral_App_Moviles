package com.example.casosemestral.model.sucursalesAdmins.remote

import com.example.casosemestral.model.sucursalesAdmins.SucursalAdmin
import retrofit2.http.GET

interface ApiService {
    @GET("/users")
    suspend fun getSucursalAdmins(): List<SucursalAdmin>
}