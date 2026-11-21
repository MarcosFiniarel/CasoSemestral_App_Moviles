package com.example.casosemestral.ui.gestor.lista.form

data class GestorFormUiState(
    val observacion: String = "",
    val motivoRechazo: String = "",
    val errors: GestorFormUiError = GestorFormUiError()
)
