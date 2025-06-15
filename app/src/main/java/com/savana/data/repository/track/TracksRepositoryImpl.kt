package com.savana.data.repository.track

import com.savana.core.exeption.AuthenticationException
import com.savana.di.api
import com.savana.domain.models.Status
import com.savana.domain.models.TrackInfo
import com.savana.domain.models.toDomainModel
import com.savana.domain.repository.track.TrackRepository
import java.net.UnknownHostException

class TracksRepositoryImpl: TrackRepository {
    override suspend fun trackFile(trackId: Int): Result<ByteArray> {
        return try {
            val response = api.trackService.downloadTrack(trackId = trackId)

            if (response.isSuccessful) {
                val bytes = response.body()?.bytes()
                if (bytes != null) {
                    Result.success(bytes)
                } else {
                    Result.failure(Exception("Empty track file response"))
                }
            } else {
                Result.failure(Exception("Failed to download track file: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun trackInfo(trackId: Int): Result<TrackInfo> {
        return try {
            val response = api.trackService.trackInfo(trackId)

            if (response.isSuccessful) {
                val body = response.body()
                val track = body?.data?.track
                if (track != null) {
                    val trackInfo = track.toDomainModel()
                    Result.success(trackInfo)
                } else {
                    Result.failure(Exception("Track data is null"))
                }
            } else {
                Result.failure(Exception("Failed to load track info: ${response.code()}"))
            }
        }catch (e: UnknownHostException){
            Result.failure(AuthenticationException(null))
        } finally {
            Result.failure<Exception>(Exception())
        }
    }

    override suspend fun trackStatus(trackId: Int): Result<Status> {
        return try {
            val response = api.trackService.trackStatus(trackId)

            if (response.isSuccessful) {
                val body = response.body()
                val status = body?.data?.trackStatus
                if (status != null) {
                    val trackStatus = Status.getStatus(status)
                    Result.success(trackStatus)
                } else {
                    Result.failure(Exception("Track data is null"))
                }
            } else {
                Result.failure(Exception("Failed to load track info: ${response.code()}"))
            }
        }catch (e: UnknownHostException){
            Result.failure(AuthenticationException(null))
        } finally {
            Result.failure<Exception>(Exception())
        }
    }
}