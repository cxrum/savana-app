package com.savana.domain.usecases.recommendation

import com.savana.domain.models.RecommendationData
import com.savana.domain.models.TrackInfo
import com.savana.domain.models.UploadedTrackData
import com.savana.domain.repository.recommendation.RecommendationRepository
import com.savana.domain.repository.track.TrackRepository

class GetRecommendationsUseCase(
    private val trackRepository: TrackRepository,
    private val recommendationRepository: RecommendationRepository
) {


    suspend operator fun invoke(trackId: Int): Result<RecommendationData> {
        val uploadedTrackResult = trackRepository.trackInfo(trackId)
        if (uploadedTrackResult.isFailure) {
            return Result.failure(uploadedTrackResult.exceptionOrNull()!!)
        }
        val uploadedTrack = uploadedTrackResult.getOrThrow()

        val recommendationsResult = recommendationRepository.recommendationTracks()
        if (recommendationsResult.isFailure) {
            return Result.failure(recommendationsResult.exceptionOrNull()!!)
        }
        val recommendations = recommendationsResult.getOrThrow()

        val recommendationsData = mutableListOf<TrackInfo>()

        for (id in recommendations.tracks) {
            val infoResult = trackRepository.trackInfo(id)
            val fileResult = trackRepository.trackFile(id)

            if (infoResult.isFailure || fileResult.isFailure ) {
                val info = infoResult.getOrThrow()
                recommendationsData.add(info.copy(streamUrl = info.streamUrl))
                continue
            }

            val info = infoResult.getOrThrow()
            val file = fileResult.getOrThrow()

            recommendationsData.add(info.copy(bytesArray = file, streamUrl = info.streamUrl))
        }

        val recommendationData = RecommendationData(
            uploadedTrackData = UploadedTrackData(
                id = uploadedTrack.id,
                title = uploadedTrack.title,
                albumTitle = uploadedTrack.albumTitle,
                albumArtUrl = uploadedTrack.albumArtUrl,
                artistName = uploadedTrack.artistName,
                chartData = uploadedTrack.chartData
            ),
            trackInfos = recommendationsData
        )

        return Result.success(recommendationData)
    }

}