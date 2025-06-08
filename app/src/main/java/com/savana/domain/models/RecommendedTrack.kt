package com.savana.domain.models

data class RecommendedTrack(
    val id: Int,
    val title: String,
    val albumTitle: String? = null,
    val albumArtUrl: String? = null,
    val artistName: String,
    val totalDurationSeconds: Int,
    var isPlaying: Boolean = false,
    var currentSecond: Int = 0,
    val streamUrl: String? = null,
    val bytesArray: ByteArray? = null,
)