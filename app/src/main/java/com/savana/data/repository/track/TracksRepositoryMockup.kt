package com.savana.data.repository.track

import com.github.mikephil.charting.data.RadarEntry
import com.savana.domain.models.Status
import com.savana.domain.models.TrackInfo
import com.savana.domain.repository.track.TrackRepository
import kotlinx.coroutines.delay

class TracksRepositoryMockup : TrackRepository {

    override suspend fun trackFile(trackId: Int): Result<ByteArray> {
        delay(100)
        return Result.failure(Exception("Track not found"))
    }

    override suspend fun trackInfo(trackId: Int): Result<TrackInfo> {
        delay(100)
        return Result.success(
            TrackInfo(
                id = trackId,
                title = "Mock Title $trackId",
                albumTitle = "Mock Album",
                albumArtUrl = "https://upload.wikimedia.org/wikipedia/en/thumb/9/9f/Bohemian_Rhapsody.png/220px-Bohemian_Rhapsody.png",
                artistName = "Mock Artist",
                totalDurationSeconds = 354,
                isPlaying = false,
                currentSecond = 0,
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                bytesArray = null,
                chartData = listOf(
                    RadarEntry(120f,"BPM"),
                    RadarEntry(60f,"Camelot"),
                    RadarEntry(80f,"Energy"),
                    RadarEntry(20f,"Danceability"),
                )
            )
        )
    }

    override suspend fun trackStatus(trackId: Int): Result<Status> {
        delay(50)
        return when (trackId) {
            1 -> Result.success(Status.Analyzing)
            2 -> Result.success(Status.Success)
            3 -> Result.success(Status.Deny)
            else -> Result.success(Status.Deny)
        }
    }
}