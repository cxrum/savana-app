package com.savana.domain.usecases.history

import com.savana.domain.models.HistoryEntry
import com.savana.domain.models.Status
import com.savana.domain.repository.track.TrackRepository
import com.savana.domain.repository.user.UserRepository
import kotlinx.coroutines.delay

class GetHistoryUseCase(
    private val trackRepository: TrackRepository
) {

    private val historySuccessPlaceholder = HistoryEntry(
        id = 76,
        label = "Allah",
        status = Status.Success
    )

    suspend operator fun invoke(): Result<List<HistoryEntry>>{
        delay(1000)
        return Result.success(buildList {
            add(historySuccessPlaceholder)
        })
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