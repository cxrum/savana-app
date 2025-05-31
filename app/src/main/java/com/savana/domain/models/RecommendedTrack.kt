package com.savana.domain.models

data class RecommendedTrack(
    val id: String,
    val trackTitle: String,
    val artistName: String,
    val totalDurationSeconds: Int,
    var currentProgressSeconds: Int = 0,
    var isPlaying: Boolean = false,
    val streamUrl: String? = null
)