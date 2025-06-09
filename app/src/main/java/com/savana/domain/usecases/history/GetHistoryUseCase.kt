package com.savana.domain.usecases.history

import com.savana.domain.models.HistoryEntry
import com.savana.domain.models.Status
import kotlinx.coroutines.delay

class GetHistoryUseCase {

    private val historySuccessPlaceholder = HistoryEntry(
        id = 1,
        label = "Abpba 1",
        status = Status.Success
    )

    private val historyAnalyzingPlaceholder = HistoryEntry(
        id = 1,
        label = "Abpba 1",
        status = Status.Analyzing
    )

    private val historyDenyPlaceholder = HistoryEntry(
        id = 1,
        label = "Abpba 1",
        status = Status.Deny
    )

    suspend operator fun invoke(): List<HistoryEntry>{
        delay(1000)
        return buildList {

            add(historyAnalyzingPlaceholder)
            add(historySuccessPlaceholder)
            add(historyDenyPlaceholder)
            add(historyAnalyzingPlaceholder)
            add(historySuccessPlaceholder)
            add(historyDenyPlaceholder)
            add(historyAnalyzingPlaceholder)
            add(historySuccessPlaceholder)
            add(historyDenyPlaceholder)
            add(historyAnalyzingPlaceholder)
            add(historySuccessPlaceholder)
            add(historyDenyPlaceholder)
            add(historyAnalyzingPlaceholder)
            add(historySuccessPlaceholder)
            add(historyDenyPlaceholder)
            add(historyAnalyzingPlaceholder)
            add(historySuccessPlaceholder)
            add(historyDenyPlaceholder)
            add(historyAnalyzingPlaceholder)
            add(historySuccessPlaceholder)
            add(historyDenyPlaceholder)
            add(historyAnalyzingPlaceholder)
            add(historySuccessPlaceholder)
            add(historyDenyPlaceholder)
        }
    }

    suspend operator fun invoke(historyId: Int): HistoryEntry{
        delay(1000)

        return historyAnalyzingPlaceholder
    }

}