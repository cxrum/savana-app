package com.savana.ui.activities.main

data class HistoryState(
    val list: MutableList<String> = mutableListOf(),
    val isLoading: Boolean = false,
    val success: Boolean = false,
)
