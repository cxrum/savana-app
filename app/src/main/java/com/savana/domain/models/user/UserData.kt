package com.savana.domain.models.user

import com.savana.data.network.model.user.UserResponse

data class UserData(
    val id: Int,
    val nickname: String,
    val email: String,
    val avatar: String? = null
)

fun UserResponse.toDomain(): UserData {
    return UserData(
        id = this.id,
        nickname = this.nickname ?: "",
        email = this.email,
        avatar = this.avatar
    )
}