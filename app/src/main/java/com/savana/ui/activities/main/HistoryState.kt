package com.savana.ui.activities.main

import com.savana.domain.models.HistoryEntry

data class HistoryState(
    val history: List<HistoryEntry> = emptyList(),
    val isLoading: Boolean = false
)