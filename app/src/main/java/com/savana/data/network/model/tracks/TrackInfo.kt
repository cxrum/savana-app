package com.savana.data.network.model.tracks

import com.google.gson.annotations.SerializedName
import com.savana.data.network.model.user.UserResponse

data class TrackInfo(
    val track: Track
)

data class Track(
    val id: Int,
    val name: String,
    val status: Int,
    @SerializedName("uploaded_at")
    val uploadedAt: String,
    @SerializedName("uploaded_by")
    val uploadedBy: UserResponse,
    val artist: String? = null,
    val criteria: List<Criterion>
)

data class Criterion(
    val id: Int,
    val name: String,
    val value: Float
)
