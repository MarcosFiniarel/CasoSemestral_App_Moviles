package com.example.casosemestral.metodosAux

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

// FUNCION AUXILIAR PARA QUE UN CAMPO DE TEXTO PARSEE A DOUBLE
@Composable
fun NumericOutlinedTextField(
    value: Double,
    onValueChange: (Double) -> Unit,
    label: String,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // SE MANEJA EL TEXTO VISUALMENTE
    val textValue = if (value == 0.0) "" else value.toString()

    OutlinedTextField(
        value = textValue,
        onValueChange = { input ->
            // DE SER UN NUMERO VALIDO SE CONVIERTE Y SI ESTA VACIO SE MANDA 0.0
            val parsed = input.toDoubleOrNull() ?: 0.0
            onValueChange(parsed)
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = isError,
        supportingText = supportingText,
        modifier = modifier
    )
}