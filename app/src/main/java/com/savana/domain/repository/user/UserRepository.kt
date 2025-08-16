package com.savana.domain.repository.user

import com.savana.domain.models.HistoryEntry
import com.savana.domain.models.SelectedTrackGap
import com.savana.domain.models.user.UserData

interface UserRepository {

    suspend fun getInfo(): Result<UserData>

    suspend fun sendTrackToAnalyze(track: SelectedTrackGap): Result<Int>

    suspend fun getHistory(): Result<List<HistoryEntry>>
}