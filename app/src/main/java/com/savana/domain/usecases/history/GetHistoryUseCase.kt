package com.savana.domain.usecases.history

import com.savana.domain.models.HistoryEntry
import com.savana.domain.models.Status
import com.savana.domain.repository.track.TrackRepository
import com.savana.domain.repository.user.UserRepository

class GetHistoryUseCase(
    private val trackRepository: TrackRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(): Result<List<HistoryEntry>>{

        val res = userRepository.getHistory()

        return if (res.isSuccess){
            Result.success(res.getOrNull() ?: emptyList())
        }else{
            Result.failure(res.exceptionOrNull() ?: Exception())
        }
    }

    suspend operator fun invoke(trackId: Int): Result<HistoryEntry>{
        val status = trackRepository.trackStatus(trackId)

        if (status.isSuccess){
            val body = status.getOrNull()!!

            return when(body){
                Status.Analyzing ->{
                    Result.failure(Exception())
                }
                Status.Deny -> {
                    Result.failure(Exception())
                }
                Status.Success -> {
                    val trackData = trackRepository.trackInfo(trackId)

                    if (trackData.isSuccess){
                        val body = trackData.getOrNull()!!

                        Result.success(HistoryEntry(
                            id = trackId,
                            label = body.title,
                            status = Status.Success,
                            trackInfo = body
                            )
                        )
                    }else{
                        Result.failure(Exception())
                    }
                }

            }
        }

        return Result.failure(Exception())
    }

}