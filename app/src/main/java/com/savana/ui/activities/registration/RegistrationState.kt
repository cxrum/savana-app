package com.savana.ui.activities.registration

data class RegistrationState(
    val email: String? = null,
    val nickname: String? = null,
    val password: String? = null,
    val avatarId: Int? = null,
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val currentStep: RegistrationViewModel.Companion.Steps? = RegistrationViewModel.Companion.Steps.EMAIL,
    val errorMessage: String? = null,
)
