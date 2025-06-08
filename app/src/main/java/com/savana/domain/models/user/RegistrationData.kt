package com.savana.domain.models.user

data class RegistrationData(
    val username: String,
    val email: String,
    val password: String,
    val avatarId: Int
)
