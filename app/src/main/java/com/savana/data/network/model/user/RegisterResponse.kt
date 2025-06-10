package com.savana.data.network.model.user

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    val status: String,
    @SerializedName("user_id")
    val userId: Int
)
