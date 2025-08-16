package com.savana.data.network.model.tracks

import com.google.gson.annotations.SerializedName

data class TrackStatus(
    @SerializedName("track_status")
    val trackStatus: Int
)
