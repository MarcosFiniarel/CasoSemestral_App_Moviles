package com.example.casosemestral.ui.vendedor.form.insert

// SOLO SE MANEJARAN LOS ERRORES DE LOS PARAMETROS A LOS QUE SE MANEJARAN LOS ESTADOS
data class VentaFormInsertUiError(
    val cliente: String? = null,
    val convenioOServicio: String? = null,
    val prestadorAsociado: String? = null,
    val monto: String? = null,
    val respaldoFoto: String? = null,
    val estado: String? = null,
    val fechaEnvio: String? = null
)