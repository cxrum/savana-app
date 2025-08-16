package com.savana.data.network.model.user

import com.google.gson.annotations.SerializedName

data class TrackUploaded(
    @SerializedName("track_id")
    val trackId: Int
)
