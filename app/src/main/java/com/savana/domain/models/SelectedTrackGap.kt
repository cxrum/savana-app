package com.savana.domain.models

data class SelectedTrackGap(
    val trackTitle: String,
    val gapStart: Long,
    val gapEnd: Long,
    val trackData: ByteArray
)