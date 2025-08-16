package com.savana.data.network.model.user

data class LoginRequest(
    val email: String,
    val password: String
)