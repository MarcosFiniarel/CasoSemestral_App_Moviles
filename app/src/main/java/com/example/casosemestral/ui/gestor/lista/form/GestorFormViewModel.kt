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

    // ESTADO INTERNO PARA ALMACENAR LA VENTA QUE SE VA A AUDITAR
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

    // FUNCION QUE GENERA LOS CARACTERES ALEATORIOS EN MAYUSCULAS Y NUMEROS
    private fun generarCadenaAleatoria(longitud: Int = 13): String {
        val caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..longitud)
            .map { caracteres.random() }
            .joinToString("")
    }

    // FUNCION CON BUCLE DE SEGURIDAD PARA GARANTIZAR UNICIDAD ABSOLUTA Y PREFIJO DINAMICO
    private suspend fun obtenerCodigoUnicoGarantizado(modo: String): String {
        // SE DEFINE EL PREFIJO SEGUN EL MODO DE AUDITORIA
        val prefijo = when (modo) {
            "APROBAR" -> "APR-"
            "RECHAZO" -> "REC-"
            "CORREGIR" -> "COR-"
            else -> "GEN-"
        }

        var codigoCandidato: String
        var existe: Int

        // BUCLE DE REINTENTO EN CASO DE ENCONTRAR LA MISMA CLAVE YA EXISTENTE EN LA PERSISTENCIA
        do {
            // SE GENERA EL CUERPO DE LA CADENA
            val cuerpoAleatorio = generarCadenaAleatoria(13)
            codigoCandidato = "$prefijo$cuerpoAleatorio"

            // SE VERIFICA LA EXISTENCIA O NO DE ESTA CADENA
            existe = repo.contarCodigo(codigoCandidato)
        } while (existe > 0)

        return codigoCandidato
    }

}