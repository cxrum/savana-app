package com.savana.data.network.model.user

data class RegisterRequest(
    val email: String,
    val password: String,
    val nickname: String,
    val avatar: Int
)
