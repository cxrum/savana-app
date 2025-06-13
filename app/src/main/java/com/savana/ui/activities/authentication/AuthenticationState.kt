package com.savana.ui.activities.authentication

data class AuthenticationState(
    val email: String? = null,
    val password: String? = null,
    val isLoading: Boolean = false,
    val success: Boolean? = null,
    val errorMessage: String? = null
)
