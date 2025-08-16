package com.savana.ui.fragments.main.search.recomedation

import com.savana.domain.models.TrackInfo

data class PlayerUiState(
    val trackInfos: List<TrackInfo> = emptyList(),
    val currentPlayingTrackInfo: TrackInfo? = null,
    val isPlaying: Boolean = false,
    val currentPositionMillis: Long = 0L,
    val totalDurationMillis: Long = 0L,
    val isPreparingTrack: Boolean = false
)