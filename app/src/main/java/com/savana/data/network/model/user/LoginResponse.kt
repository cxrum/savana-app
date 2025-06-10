package com.savana.data.network.model.user

data class LoginResponse(
    val status: String,
    val userResponse: UserResponse
)