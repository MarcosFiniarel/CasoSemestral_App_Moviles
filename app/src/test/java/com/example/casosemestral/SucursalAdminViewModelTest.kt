package com.example.casosemestral

import com.example.casosemestral.model.data.repository.SucursalAdminRepository
import com.example.casosemestral.model.sucursalesAdmins.CompanySucursal
import com.example.casosemestral.model.sucursalesAdmins.SucursalAdmin
import com.example.casosemestral.ui.administrador.adminSucursal.SucursalAdminViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class SucursalAdminViewModelTest : StringSpec({

    val testDispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    "adminList debe contener los administradores obtenidos desde el repositorio" {
        val fakeAdmins = listOf(
            SucursalAdmin(
                id = 1,
                name = "Leanne Graham",
                username = "Bret",
                email = "Sincere@april.biz",
                company = CompanySucursal(name = "Romaguera-Crona")
            ),
            SucursalAdmin(
                id = 2,
                name = "Ervin Howell",
                username = "Antonette",
                email = "Shanna@melissa.tv",
                company = CompanySucursal(name = "Deckow-Crist")
            )
        )

        val mockRepo = mockk<SucursalAdminRepository>()
        coEvery { mockRepo.getSucursalAdmins() } returns fakeAdmins

        runTest(testDispatcher) {
            val vm = SucursalAdminViewModel(repository = mockRepo)
            advanceUntilIdle()

            coVerify(exactly = 1) { mockRepo.getSucursalAdmins() }
            vm.adminList.value shouldContainExactly fakeAdmins
        }
    }

    "adminList debe permanecer vacia cuando el repositorio lanza excepcion" {
        val mockRepo = mockk<SucursalAdminRepository>()
        coEvery { mockRepo.getSucursalAdmins() } throws IOException("Fallo de red")

        runTest(testDispatcher) {
            val vm = SucursalAdminViewModel(repository = mockRepo)
            advanceUntilIdle()

            vm.adminList.value.shouldBeEmpty()
        }
    }
})