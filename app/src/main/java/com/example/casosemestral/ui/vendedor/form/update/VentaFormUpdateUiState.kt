package com.example.casosemestral.ui.vendedor.form.update

import com.example.casosemestral.model.data.venta.VentaEntity

data class VentaFormUpdateUiState(
    val convenioNuevo: String = "",
    val prestadorNuevo: String = "",
    val montoNuevo: Double = 0.0,
    val respaldoNuevo: String = "",
    val estadoNuevo: String = "Borrador",
    val fechaEnvioNuevo: String = "",
    val errors: VentaFormUpdateUiError = VentaFormUpdateUiError()
){
    // SE AÑADE UN CONSTRUCTOR CON DATOS PARA PODER PRECARGAR EL FORM DE UPDATE CON LOS DATOS
    constructor(venta: VentaEntity): this(
        convenioNuevo = venta.convenio_o_servicio,
        prestadorNuevo = venta.prestador_asociado,
        montoNuevo = venta.monto,
        respaldoNuevo = venta.respaldo_fotografico,
        estadoNuevo = venta.estado,
        fechaEnvioNuevo = venta.fecha_envio
    )
}