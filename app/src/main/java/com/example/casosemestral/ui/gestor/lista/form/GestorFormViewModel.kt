package com.example.casosemestral.ui.gestor.lista.form

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.casosemestral.model.data.local.AppDatabase
import com.example.casosemestral.model.data.repository.VentaRepository
import com.example.casosemestral.model.venta.VentaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GestorFormViewModel(
    app: Application,
    savedStateHandle: SavedStateHandle
): AndroidViewModel(app) {

    // SE INVOCA AL REPOSITORIO PARA UTILIZARLO PARA INSERTAR LOS DATOS DESPUES DE VALIDARLOS
    private val repo: VentaRepository = VentaRepository(
        AppDatabase.getDatabase(app).ventaDao()
    )

    // SE RECUPERA EL MODO QUE FUE ENVIADO POR LA RUTA DE NAVEGACION ("APROBAR", "RECHAZO" o "CORREGIR")
    private val modo: String = savedStateHandle["modo"] ?: ""

    // ESTADO INTERNO MUTABLE
    private val _estado = MutableStateFlow(GestorFormUiState())

    // ESTADO EXPUESTO PARA LA UI
    val estado: StateFlow<GestorFormUiState> = _estado

    // Estado interno para almacenar la venta que vamos a auditar
    private val _venta = MutableStateFlow<VentaEntity?>(null)
    val venta: StateFlow<VentaEntity?> = _venta

    fun cargarVenta(id: Long) {
        viewModelScope.launch {
            _venta.value = repo.obtenerVentaPorId(id)
        }
    }

    // FUNCIONES PARA VALIDAR CAMPOS CUANDO SEAN NECESARIOS
    fun onObservacionChange(value: String){
        _estado.update {
            it.copy(observacion = value, errors = it.errors.copy(observacion = null))
        }
    }

    fun onMotivoRechazoChange(value: String){
        _estado.update {
            it.copy(motivoRechazo = value, errors = it.errors.copy(motivoRechazo = null))
        }
    }

    // VALIDAR FORM CONDICIONADO A LA ACCION QUE SE ESTA REALIZANDO
    fun validarForm(): Boolean{

        val estadoActual = _estado.value

        val errores = GestorFormUiError(
            if (modo == "CORREGIR" && estadoActual.observacion.isBlank()){"Motivo de correción obligatorio"}else{null},
            if (modo == "RECHAZO" && estadoActual.motivoRechazo.isBlank()){"Motivo del rechazo obligatorio"}else{null}
        )

        val hayError = listOfNotNull(
            errores.observacion,
            errores.motivoRechazo
        ).isNotEmpty()

        _estado.update { it.copy(errors = errores) }

        return !hayError

    }

    fun actualizarVenta(venta: VentaEntity, gestor: String, onSuccess: () -> Unit){

        if (!validarForm()){return}

        val estadoActual = _estado.value

        viewModelScope.launch {

            val codigoUnico = obtenerCodigoUnicoGarantizado(modo)

            repo.actualizarVentaGestor(
                gestor,
                venta,
                estadoActual.observacion.ifBlank { "Sin Observaciones" },
                estadoActual.motivoRechazo.ifBlank { "N/A" },
                codigoUnico,
                when(modo){
                    "APROBAR" -> "Aprobada"
                    "RECHAZO" -> "Rechazada"
                    "CORREGIR" -> "Corregir"
                    else -> "Enviado"
                }
            )

            onSuccess()

        }

    }

    // Función que genera los caracteres aleatorios en mayúsculas y números (longitud deseada)
    private fun generarCadenaAleatoria(longitud: Int = 13): String {
        val caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..longitud)
            .map { caracteres.random() }
            .joinToString("")
    }

    // Función con bucle de seguridad para garantizar unicidad absoluta y prefijo dinámico
    private suspend fun obtenerCodigoUnicoGarantizado(modo: String): String {
        // 1. Definir el prefijo según el modo de auditoría
        val prefijo = when (modo) {
            "APROBAR" -> "APR-"
            "RECHAZO" -> "REC-"
            "CORREGIR" -> "COR-"
            else -> "GEN-"
        }

        var codigoCandidato: String
        var existe: Int

        // 2. Bucle de reintento controlado (evita colisiones con el índice UNIQUE de la BD)
        do {
            // Generamos el cuerpo aleatorio de 13 caracteres (Prefijo 4 + 13 = 16 caracteres totales aprox)
            val cuerpoAleatorio = generarCadenaAleatoria(13)
            codigoCandidato = "$prefijo$cuerpoAleatorio"

            // Consultamos a la base de datos si este código ya existe
            existe = repo.contarCodigo(codigoCandidato)
        } while (existe > 0) // Si ya existe (> 0), el bucle repite la iteración al instante

        return codigoCandidato
    }

}