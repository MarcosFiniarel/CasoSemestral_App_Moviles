package com.example.casosemestral

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.example.casosemestral.model.data.repository.SucursalAdminRepository
import com.example.casosemestral.model.sucursalesAdmins.CompanySucursal
import com.example.casosemestral.model.sucursalesAdmins.SucursalAdmin
import com.example.casosemestral.ui.administrador.adminSucursal.SucursalAdminScreen
import com.example.casosemestral.ui.administrador.adminSucursal.SucursalAdminViewModel
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SucursalAdminScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private class FakeSucursalRepo(
        private val lista: List<SucursalAdmin>
    ) : SucursalAdminRepository() {
        override suspend fun getSucursalAdmins(): List<SucursalAdmin> = lista
    }

    @Test
    fun debe_mostrar_datos_de_administradores_de_sucursales() {
        val adminsFake = listOf(
            SucursalAdmin(
                id = 1,
                name = "Leanne Graham",
                username = "Bret",
                email = "Sincere@april.biz",
                company = CompanySucursal(name = "Romaguera-Crona")
            )
        )
        val vm = SucursalAdminViewModel(FakeSucursalRepo(adminsFake))

        composeRule.setContent {
            val navController = rememberNavController()
            SucursalAdminScreen(
                navController = navController,
                vm = vm,
                username = "admin"
            )
        }

        composeRule.onNodeWithText("Administradores de Otras Sucursales").assertIsDisplayed()
        composeRule.onNodeWithText("Admin #1 · Leanne Graham").assertIsDisplayed()
        composeRule.onNodeWithText("Usuario: Bret").assertIsDisplayed()
        composeRule.onNodeWithText("Correo: Sincere@april.biz").assertIsDisplayed()
        composeRule.onNodeWithText("Compañía a su cargo: Romaguera-Crona").assertIsDisplayed()
    }

    @Test
    fun debe_mostrar_mensaje_vacio_cuando_no_hay_administradores() {
        val vm = SucursalAdminViewModel(FakeSucursalRepo(emptyList()))

        composeRule.setContent {
            val navController = rememberNavController()
            SucursalAdminScreen(
                navController = navController,
                vm = vm,
                username = "admin"
            )
        }

        composeRule.onNodeWithText("No hay administradores externos disponibles...").assertIsDisplayed()
    }

    @Test
    fun debe_invocar_onVolverMenu_al_pulsar_boton_volver() {
        val vm = SucursalAdminViewModel(FakeSucursalRepo(emptyList()))
        var volvioAlMenu = false

        composeRule.setContent {
            val navController = rememberNavController()
            SucursalAdminScreen(
                navController = navController,
                vm = vm,
                username = "admin",
                onVolverMenu = { volvioAlMenu = true }
            )
        }

        composeRule.onNodeWithText("Volver al Menú").performClick()

        assertTrue(volvioAlMenu)
    }
}