package com.savana.ui.fragments.main.search.recomedation

import com.savana.domain.models.RecommendedTrack

data class PlayerUiState(
    val tracks: List<RecommendedTrack> = emptyList(),
    val currentPlayingTrack: RecommendedTrack? = null,
    val isPlaying: Boolean = false,
    val currentPositionMillis: Long = 0L,
    val totalDurationMillis: Long = 0L,
    val isPreparingTrack: Boolean = false
)