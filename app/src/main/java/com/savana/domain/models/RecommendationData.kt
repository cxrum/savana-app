package com.savana.domain.models

data class RecommendationData(
    //TODO()
    val data: String,
    val recommendedTracks: List<RecommendedTrack>,
    val chartData: RadarChartData
)
