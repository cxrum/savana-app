package com.savana.data.network.model.user

import com.google.gson.annotations.SerializedName

data class HistoryResponse(
    val history: List<HistoryEntryResponse>
)

data class HistoryEntryResponse(
    val id: Int,
    @SerializedName("user_track")
    val userTrack: UserTrack
)

data class UserTrack(
    val id: Int,
    val artist: String?,
    val name: String,
    val status: Int,
)
