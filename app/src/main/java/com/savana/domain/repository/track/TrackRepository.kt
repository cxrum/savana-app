package com.savana.domain.repository.track

import com.savana.domain.models.TrackInfo

interface TrackRepository {

    suspend fun trackFile(trackId: Int): Result<ByteArray>

    suspend fun trackInfo(trackId: Int): Result<TrackInfo>

}