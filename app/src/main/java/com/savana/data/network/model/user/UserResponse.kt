package com.savana.data.network.model.user

data class UserResponse(
    val id: Int,
    val email: String,
    val avatar: String? = null,
    val nickname: String? = null
)
