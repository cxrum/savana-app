package com.savana.domain.models

data class RecommendationData(
    val uploadedTrackData: UploadedTrackData,
    val trackInfos: List<TrackInfo>,
)
