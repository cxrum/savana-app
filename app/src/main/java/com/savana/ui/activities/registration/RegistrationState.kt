package com.savana.ui.activities.registration

data class RegistrationState(
    val email: String? = null,
    val name: String? = null,
    val password: String? = null,
    val avatarId: Int? = null,
    val isLoading: Boolean = false,
    val success: Boolean = false,
)
