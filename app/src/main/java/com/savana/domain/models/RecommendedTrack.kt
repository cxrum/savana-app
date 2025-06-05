package com.savana.domain.models

data class RecommendedTrack(
    val id: Int,
    val trackTitle: String,
    val artistName: String,
    val totalDurationSeconds: Int,
    var isPlaying: Boolean = false,
    var currentSecond: Int = 0,
    val streamUrl: String? = null,
    val bytesArray: ByteArray? = null,
)