package com.example.casosemestral

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.example.casosemestral.model.data.repository.UserRepository
import com.example.casosemestral.model.user.UserDao
import com.example.casosemestral.model.user.UserEntity
import com.example.casosemestral.ui.administrador.usuarioHabilitado.PanelDeUsuarioScreen
import com.example.casosemestral.ui.administrador.usuarioHabilitado.PanelDeUsuarioViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class PanelDeUsuarioScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun crearUsuario(
        id: Long,
        nombre: String,
        perfil: String,
        zona: String,
        estadoCuenta: Boolean
    ) = UserEntity(
        id = id,
        nombre = nombre,
        clave = "123",
        perfil = perfil,
        zonaSucursal = zona,
        estadoCuenta = estadoCuenta,
        fechaCreacion = "2026-01-10 09:00:00",
        fechaActualizacion = "2026-01-10 09:00:00"
    )

    private class FakeUserDao(initial: List<UserEntity>) : UserDao {
        val usuarios = initial.toMutableList()
        private val flow = MutableStateFlow(usuarios.toList())
        var ultimoIdActualizado: Long? = null
        var ultimoEstado: Boolean? = null

        override suspend fun obtenerUsuarioPorNombreYPerfil(username: String, perfil: String) = null
        override fun listarUsuarios(): Flow<List<UserEntity>> = flow
        override suspend fun actualizarEstado(userId: Long, estado: Boolean, actualizacion: String) {
            ultimoIdActualizado = userId
            ultimoEstado = estado
            val idx = usuarios.indexOfFirst { it.id == userId }
            if (idx != -1) {
                usuarios[idx] = usuarios[idx].copy(estadoCuenta = estado, fechaActualizacion = actualizacion)
                flow.value = usuarios.toList()
            }
        }
    }

    @Test
    fun debe_mostrar_gestores_y_vendedores_con_su_estado() {
        val fakeDao = FakeUserDao(
            listOf(
                crearUsuario(1L, "Ana Soto", "Vendedor", "Puente Alto", estadoCuenta = true),
                crearUsuario(2L, "Carlos Pérez", "Gestor", "Santiago Centro", estadoCuenta = false)
            )
        )
        val vm = PanelDeUsuarioViewModel(Application(), UserRepository(fakeDao))

        composeRule.setContent {
            val navController = rememberNavController()
            PanelDeUsuarioScreen(
                navController = navController,
                vm = vm,
                username = "admin"
            )
        }

        composeRule.onNodeWithText("Gestión de Usuarios").assertIsDisplayed()
        composeRule.onNodeWithText("Gestores").assertIsDisplayed()
        composeRule.onNodeWithText("Vendedores").assertIsDisplayed()
        composeRule.onNodeWithText("Ana Soto").assertIsDisplayed()
        composeRule.onNodeWithText("Carlos Pérez").assertIsDisplayed()
        composeRule.onNodeWithText("Habilitado").assertIsDisplayed()
        composeRule.onNodeWithText("Deshabilitado").assertIsDisplayed()
    }

    @Test
    fun debe_actualizar_estado_al_pulsar_etiqueta_o_switch() {
        val fakeDao = FakeUserDao(
            listOf(crearUsuario(1L, "Ana Soto", "Vendedor", "Puente Alto", estadoCuenta = true))
        )
        val vm = PanelDeUsuarioViewModel(Application(), UserRepository(fakeDao))

        composeRule.setContent {
            val navController = rememberNavController()
            PanelDeUsuarioScreen(
                navController = navController,
                vm = vm,
                username = "admin"
            )
        }

        composeRule.onAllNodes(isToggleable())[0].performClick()

        composeRule.waitUntil(timeoutMillis = 3_000) {
            fakeDao.ultimoIdActualizado != null
        }

        assertEquals(1L, fakeDao.ultimoIdActualizado)
        assertFalse(fakeDao.ultimoEstado ?: true)
    }

    @Test
    fun debe_mostrar_mensajes_vacios_cuando_no_hay_usuarios() {
        val vm = PanelDeUsuarioViewModel(Application(), UserRepository(FakeUserDao(emptyList())))

        composeRule.setContent {
            val navController = rememberNavController()
            PanelDeUsuarioScreen(
                navController = navController,
                vm = vm,
                username = "admin"
            )
        }

        composeRule.onNodeWithText("No hay gestores registrados...").assertIsDisplayed()
        composeRule.onNodeWithText("No hay vendedores registrados...").assertIsDisplayed()
    }
}