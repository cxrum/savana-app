package com.savana.domain.repository.track

import com.savana.domain.models.Status
import com.savana.domain.models.TrackInfo

interface TrackRepository {

    suspend fun trackFile(trackId: Int): Result<ByteArray>

    suspend fun trackInfo(trackId: Int): Result<TrackInfo>

    suspend fun trackStatus(trackId: Int): Result<Status>

}