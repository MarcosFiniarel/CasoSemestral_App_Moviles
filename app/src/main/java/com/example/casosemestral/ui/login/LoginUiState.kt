package com.example.casosemestral.ui.login

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val esGestor: Boolean = false,
    val isLoading: Boolean = false,
    val errors: LoginUiError = LoginUiError()
)