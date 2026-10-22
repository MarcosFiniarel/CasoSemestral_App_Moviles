package com.example.casosemestral.ui.navigation

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.casosemestral.ui.camera.QrScannerScreen
import com.example.casosemestral.ui.camera.QrViewModel
import com.example.casosemestral.ui.login.LoginScreen
import com.example.casosemestral.ui.login.LoginViewModel
import com.example.casosemestral.ui.vendedor.MenuScreen
import com.example.casosemestral.ui.vendedor.form.insert.VentaFormScreen
import com.example.casosemestral.ui.vendedor.form.insert.VentaFormInsertViewModel
import com.example.casosemestral.ui.vendedor.form.update.VentaFormUpdateScreen
import com.example.casosemestral.ui.vendedor.form.update.VentaFormUpdateViewModel
import com.example.casosemestral.ui.vendedor.lista.ListaVentasScreen
import com.example.casosemestral.ui.vendedor.lista.VentaDetailScreen
import com.example.casosemestral.ui.vendedor.lista.VentaListViewModel
import com.example.casosemestral.utils.CameraPermissionHelper

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNav(){

    val navController = rememberNavController()

    NavHost(
        // SE DEFINE LA PANTALLA DE LOGIN COMO EL PUNTO DE ACCESO DE LA APP
        navController = navController, startDestination = "login_screen"
    ) {
        // RUTA DE LA PANTALLA DE LOGIN
        composable(route = "login_screen"){
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(navController,loginViewModel)
        }

        // RUTA AL MENU DE OPCIONES DEL VENDEDOR
        composable(route = "vendedor_screen/{username}?mensaje={mensaje}",
            arguments = listOf(
                navArgument(name = "username"){
                    type = NavType.StringType
                },
                navArgument("mensaje") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        )
        {
                backStackEntry ->
            val username = backStackEntry.arguments?.getString("username").orEmpty()
            val mensaje = backStackEntry.arguments?.getString("mensaje")
            MenuScreen(navController,username,mensaje)
        }

        // RUTA AL FORM DE REGISTRO DE VENTAS DEL VENDEDOR
        composable(route = "vendedor_form_screen/{username}",
                   arguments = listOf(
                       navArgument(name = "username"){
                           type = NavType.StringType
                       }
                   )
                   )
        {
            backStackEntry ->
            val username = backStackEntry.arguments?.getString("username").orEmpty()
            val ventaFormInsertViewModel: VentaFormInsertViewModel = viewModel()
            VentaFormScreen(navController,ventaFormInsertViewModel,username)
        }

        // RUTA A LA LISTA DE VENTAS DEL VENDEDOR
        composable(route = "vendedor_lista_screen/{username}?mensaje={mensaje}",
            arguments = listOf(
                navArgument(name = "username"){
                    type = NavType.StringType
                },
                navArgument("mensaje") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        )
        {
                backStackEntry ->
            val username = backStackEntry.arguments?.getString("username").orEmpty()
            val mensaje = backStackEntry.arguments?.getString("mensaje")
            val ventaListViewModel: VentaListViewModel = viewModel()
            ListaVentasScreen(navController,ventaListViewModel,username,mensaje)
        }

        // RUTA AL DETALLE EXTENDIDO DE LA VENTA
        composable(
            route = "vendedor_detail_screen/{username}/{ventaId}",
            arguments = listOf(
                navArgument(name = "username") { type = NavType.StringType },
                navArgument(name = "ventaId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username").orEmpty()
            val ventaId = backStackEntry.arguments?.getLong("ventaId") ?: 0L
            val ventaListViewModel: VentaListViewModel = viewModel()

            VentaDetailScreen(
                navController = navController,
                vm = ventaListViewModel,
                ventaId = ventaId,
                username = username
            )
        }

        // RUTA AL FORM PARA ACTUALIZAR UNA VENTA
        composable(route = "vendedor_update_screen/{username}/{ventaId}",
            arguments = listOf(
                navArgument(name = "username"){
                    type = NavType.StringType
                },
                navArgument(name = "ventaId"){
                    type = NavType.LongType
                }
            )
        )
        {
                backStackEntry ->
            val username = backStackEntry.arguments?.getString("username").orEmpty()
            val ventaId = backStackEntry.arguments?.getLong("ventaId")
            val ventaFormUpdateViewModel: VentaFormUpdateViewModel = viewModel()
            VentaFormUpdateScreen(navController,ventaFormUpdateViewModel,ventaId,username)
        }

        // FUTURA RUTA A LA PANTALLA DEL GESTOR, SE MANTIENE DIRECCIONANDO A LOGIN PARA PRUEBAS
        composable(route = "gestor_screen"){
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(navController,loginViewModel)
        }

        // RUTA PARA EL ESCANER QR
        composable(route = "qr_scanner_screen/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username").orEmpty()
            val context = LocalContext.current
            val qrViewModel: QrViewModel = viewModel()

            // ESTADO Y LOGICA DE PERMISOS DE CAMARA
            var hasCameraPermission by remember {
                mutableStateOf(CameraPermissionHelper.hasCameraPermission(context))
            }

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                hasCameraPermission = isGranted
            }

            // SE OBSERVA SI SE DETECTO EL QR PARA GUARDARLO EN EL SAVEDSTATEHANDLE Y VOLVER ATRAS
            val qrResult by qrViewModel.qrResult.observeAsState()

            LaunchedEffect(qrResult) {
                qrResult?.let { result ->
                    // SE GUARDA EL CONTENIDO DEL QR EN EL SVAEDSTATEHANDLE DEL NAVCONTROLLER ANTERIOR
                    navController.previousBackStackEntry?.savedStateHandle?.set("qr_result_key", result.content)
                    // DE VUELTA AL FORMULARIO DE VENTA
                    navController.popBackStack()
                }
            }

            QrScannerScreen(
                viewModel = qrViewModel,
                hasCameraPermission = hasCameraPermission,
                onRequestPermission = {
                    launcher.launch(Manifest.permission.CAMERA)
                }
            )
        }

    }
}