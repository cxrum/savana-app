package com.savana.domain.usecases.recommendation

import com.savana.domain.models.RecommendationData
import com.savana.domain.models.RecommendedTrack
import com.savana.domain.models.SelectedTrackGap
import com.savana.domain.models.charDataPlaceholder
import kotlinx.coroutines.delay

class GetRecommendationsUseCase {


    suspend operator fun invoke(historyId: Int): RecommendationData{
        delay(1000)
        return placeholder
    }

    suspend operator fun invoke(): RecommendationData{
        delay(1000)
        return placeholder
    }

    companion object {
        val placeholder = RecommendationData(
            data = "Test",
            recommendedTracks = listOf(
                RecommendedTrack(
                    id = 1,
                    title = "Bohemian Rhapsody",
                    albumTitle = "Bohemian Rhapsody",
                    albumArtUrl = "https://upload.wikimedia.org/wikipedia/en/thumb/9/9f/Bohemian_Rhapsody.png/220px-Bohemian_Rhapsody.png",
                    artistName = "Queen",
                    totalDurationSeconds = 354,
                    streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                ),
                RecommendedTrack(
                    id = 2,
                    title = "Stairway to Heaven",
                    albumTitle = "Stairway to Heaven",
                    albumArtUrl = "https://upload.wikimedia.org/wikipedia/en/thumb/5/57/Stairway_to_Heaven_Accoustic.jpg/220px-Stairway_to_Heaven_Accoustic.jpg",
                    artistName = "Led Zeppelin",
                    totalDurationSeconds = 482,
                    streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
                )
            ),
            chartData = charDataPlaceholder
        )
    }

}