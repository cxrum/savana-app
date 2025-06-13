package com.savana.domain.models

import com.github.mikephil.charting.data.RadarEntry

data class UploadedTrackData(
    val id: Int,
    val title: String,
    val albumTitle: String? = null,
    val albumArtUrl: String? = null,
    val artistName: String,
    val chartData: List<RadarEntry> = emptyList()
)
