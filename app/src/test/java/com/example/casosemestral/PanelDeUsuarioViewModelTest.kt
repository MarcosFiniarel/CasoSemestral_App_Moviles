package com.example.casosemestral

import android.app.Application
import com.example.casosemestral.model.data.repository.UserRepository
import com.example.casosemestral.model.user.UserEntity
import com.example.casosemestral.ui.administrador.usuarioHabilitado.PanelDeUsuarioViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class PanelDeUsuarioViewModelTest : StringSpec({

    val testDispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
        mockkObject(UserEntity.Companion)
        every { UserEntity.obtenerFechaActual() } returns "2026-11-24 21:00:00"
    }

    afterSpec {
        unmockkObject(UserEntity.Companion)
        Dispatchers.resetMain()
    }

    "usuarios debe reflejar la lista emitida por el repositorio" {
        val fakeUsers = listOf(
            UserEntity(
                id = 1L,
                nombre = "Ana Soto",
                clave = "123",
                perfil = "Vendedor",
                zonaSucursal = "Puente Alto",
                estadoCuenta = true,
                fechaCreacion = "2026-01-10 09:00:00",
                fechaActualizacion = "2026-01-10 09:00:00"
            ),
            UserEntity(
                id = 2L,
                nombre = "Carlos Pérez",
                clave = "456",
                perfil = "Gestor",
                zonaSucursal = "Santiago Centro",
                estadoCuenta = true,
                fechaCreacion = "2026-01-12 10:00:00",
                fechaActualizacion = "2026-01-12 10:00:00"
            )
        )

        val flujoUsuarios = MutableStateFlow(fakeUsers)
        val mockRepo = mockk<UserRepository>()
        every { mockRepo.listarUsuarios() } returns flujoUsuarios

        val app = mockk<Application>(relaxed = true)
        val vm = PanelDeUsuarioViewModel(app = app, repo = mockRepo)

        runTest(testDispatcher) {
            val job = launch(testDispatcher) { vm.usuarios.collect {} }
            advanceUntilIdle()

            vm.usuarios.value shouldContainExactly fakeUsers
            job.cancel()
        }
    }

    "cambiarEstadoCuenta debe actualizar en el repositorio y emitir mensaje de inhabilitada" {
        val usuario = UserEntity(
            id = 1L,
            nombre = "Ana Soto",
            clave = "123",
            perfil = "Vendedor",
            zonaSucursal = "Puente Alto",
            estadoCuenta = true,
            fechaCreacion = "2026-01-10 09:00:00",
            fechaActualizacion = "2026-01-10 09:00:00"
        )

        val mockRepo = mockk<UserRepository>()
        every { mockRepo.listarUsuarios() } returns MutableStateFlow(listOf(usuario))
        coEvery { mockRepo.actualizarEstado(1L, false, "2026-11-24 21:00:00") } returns Unit

        val app = mockk<Application>(relaxed = true)
        val vm = PanelDeUsuarioViewModel(app = app, repo = mockRepo)

        runTest(testDispatcher) {
            val mensajeDeferred = async(testDispatcher) { vm.mensajeEvento.first() }

            vm.cambiarEstadoCuenta(usuario = usuario, nuevoEstado = false)
            advanceUntilIdle()

            coVerify(exactly = 1) {
                mockRepo.actualizarEstado(1L, false, "2026-11-24 21:00:00")
            }
            mensajeDeferred.await() shouldBe "Cuenta de Ana Soto (Vendedor) inhabilitada"
        }
    }

    "cambiarEstadoCuenta debe actualizar en el repositorio y emitir mensaje de habilitada" {
        val usuario = UserEntity(
            id = 2L,
            nombre = "Carlos Pérez",
            clave = "456",
            perfil = "Gestor",
            zonaSucursal = "Santiago Centro",
            estadoCuenta = false,
            fechaCreacion = "2026-01-12 10:00:00",
            fechaActualizacion = "2026-01-12 10:00:00"
        )

        val mockRepo = mockk<UserRepository>()
        every { mockRepo.listarUsuarios() } returns MutableStateFlow(listOf(usuario))
        coEvery { mockRepo.actualizarEstado(2L, true, "2026-11-24 21:00:00") } returns Unit

        val app = mockk<Application>(relaxed = true)
        val vm = PanelDeUsuarioViewModel(app = app, repo = mockRepo)

        runTest(testDispatcher) {
            val mensajeDeferred = async(testDispatcher) { vm.mensajeEvento.first() }

            vm.cambiarEstadoCuenta(usuario = usuario, nuevoEstado = true)
            advanceUntilIdle()

            coVerify(exactly = 1) {
                mockRepo.actualizarEstado(2L, true, "2026-11-24 21:00:00")
            }
            mensajeDeferred.await() shouldBe "Cuenta de Carlos Pérez (Gestor) habilitada"
        }
    }
})