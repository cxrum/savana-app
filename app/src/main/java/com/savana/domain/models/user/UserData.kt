package com.savana.domain.models.user

import android.os.Parcelable
import com.savana.data.network.model.user.UserResponse
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserData(
    val id: Int,
    val nickname: String,
    val email: String,
    val avatar: String? = null
): Parcelable

fun UserResponse.toDomain(): UserData {
    return UserData(
        id = this.id,
        nickname = this.nickname ?: "",
        email = this.email,
        avatar = this.avatar
    )
}