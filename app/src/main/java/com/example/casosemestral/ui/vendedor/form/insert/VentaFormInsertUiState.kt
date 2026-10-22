package com.example.casosemestral.ui.vendedor.form.insert

/* SOLO ESTOS PARAMETROS IRAN AL FORM Y POR LO TANTO DEBERAN SER MONITOREADOS SUS ESTADOS YA QUE EL RESTO SON:
    - NO OBLIGATORIOS AL REGISTRO
    - DE REGISTRO AUTOMATIZADO VIA SISTEMA
*/
data class VentaFormInsertUiState (
    val cliente: String = "",
    val convenioOServicio: String = "",
    val prestadorAsociado: String = "",
    val monto: Double = 0.0,
    val respaldoFoto: String = "",
    val estado: String = "",
    val fechaEnvio: String = "",
    val errors: VentaFormInsertUiError = VentaFormInsertUiError()
)