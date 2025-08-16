package com.savana.domain.usecases.recommendation

import com.savana.domain.models.RecommendationData
import com.savana.domain.models.SelectedTrackGap
import com.savana.domain.models.TrackInfo
import com.savana.domain.models.UploadedTrackData
import com.savana.domain.repository.recommendation.RecommendationRepository
import com.savana.domain.repository.track.TrackRepository
import com.savana.domain.repository.user.UserRepository

class GetRecommendationsUseCase(
    private val trackRepository: TrackRepository,
    private val recommendationRepository: RecommendationRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(trackId: Int): Result<RecommendationData> {
        val uploadedTrackResult = trackRepository.trackInfo(trackId)
        if (uploadedTrackResult.isFailure) {
            return Result.failure(uploadedTrackResult.exceptionOrNull()!!)
        }
        val uploadedTrack = uploadedTrackResult.getOrThrow()

        val recommendationsResult = recommendationRepository.recommendationTracks(trackId)
        if (recommendationsResult.isFailure) {
            return Result.failure(recommendationsResult.exceptionOrNull()!!)
        }
        val recommendations = recommendationsResult.getOrThrow()

        val recommendationsData = mutableListOf<TrackInfo>()

        for (track in recommendations.tracks) {
            val fileResult = trackRepository.trackFile(track.id)

            if (fileResult.isFailure ) {
                continue
            }

            val file = fileResult.getOrThrow()
            recommendationsData.add(track.copy(bytesArray = file))
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

    suspend operator fun invoke(
        gap: SelectedTrackGap
    ): Result<RecommendationData>{

        val result = userRepository.sendTrackToAnalyze(gap)

        if (result.isSuccess){
            return this.invoke(result.getOrThrow())
        }

        return Result.failure(Exception())
    }

}