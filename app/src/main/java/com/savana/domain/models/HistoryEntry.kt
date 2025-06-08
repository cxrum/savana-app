package com.savana.domain.models


data class HistoryEntry(
    val id: Int,
    val label: String,
    val status: Status
)

enum class Status{
    Analyzing,
    Deny,
    Success
}