package com.savana.domain.models

import com.github.mikephil.charting.data.RadarEntry
import com.savana.data.network.model.tracks.Track

data class TrackInfo(
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
    val chartData: List<RadarEntry> = emptyList()
)

fun Track.toDomainModel(): TrackInfo {
    return TrackInfo(
        id = this.id,
        title = this.name,
        artistName = this.artist ?: "Unknown",
        totalDurationSeconds = 0,
        chartData = this.criteria.map {
            RadarEntry(it.value.toFloat())
        }
    )
}