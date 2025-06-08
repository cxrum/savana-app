package com.savana.domain.usecases.history

import com.savana.domain.models.HistoryEntry
import com.savana.domain.models.Status
import kotlinx.coroutines.delay

class GetHistoryUseCase {

    private val historyPlaceholder = HistoryEntry(
        id = 1,
        label = "Abpba 1",
        status = Status.Success
    )

    suspend operator fun invoke(): List<HistoryEntry>{
        delay(1000)
        return buildList {

            add(historyPlaceholder)
            add(historyPlaceholder)
            add(historyPlaceholder)
            add(historyPlaceholder)
            add(historyPlaceholder)
            add(historyPlaceholder)
            add(historyPlaceholder)
            add(historyPlaceholder)
            add(historyPlaceholder)
        }
    }

    suspend operator fun invoke(historyId: Int): HistoryEntry{
        delay(1000)

        return historyPlaceholder
    }

}