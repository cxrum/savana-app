package com.savana.domain.models.user

data class RegistrationData(
    val nickname: String,
    val email: String,
    val password: String,
    val avatarId: Int
)
