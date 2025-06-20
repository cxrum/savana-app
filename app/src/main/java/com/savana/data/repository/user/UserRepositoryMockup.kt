package com.savana.data.repository.user

import com.savana.data.local.user.UserDao
import com.savana.domain.models.HistoryEntry
import com.savana.domain.models.SelectedTrackGap
import com.savana.domain.models.Status
import com.savana.domain.models.user.UserData
import com.savana.domain.repository.user.UserRepository
import kotlinx.coroutines.delay

class UserRepositoryMockup(
    private val userDao: UserDao
) : UserRepository {
    override suspend fun getInfo(): Result<UserData> {
        delay(100)
        return Result.success(
            UserData(
                id = 1,
                nickname = "MockUser",
                email = "mockuser@example.com",
                avatar = "https://example.com/avatar.jpg"
            )
        )
    }

    override suspend fun sendTrackToAnalyze(track: SelectedTrackGap): Result<Int> {
        delay(200)
        return if (track.trackTitle.contains("fail", ignoreCase = true)) {
            Result.failure(Exception("Track analysis failed"))
        } else {
            Result.success(1)
        }
    }

    override suspend fun getHistory(): Result<List<HistoryEntry>> {
        delay(150)
        return Result.success(
            listOf(
                HistoryEntry(
                    id = 1,
                    label = "Track One",
                    status = Status.Success
                ),
                HistoryEntry(
                    id = 2,
                    label = "Track Two",
                    status = Status.Analyzing
                ),
                HistoryEntry(
                    id = 3,
                    label = "Track Three",
                    status = Status.Deny
                )
            )
        )
    }
}
