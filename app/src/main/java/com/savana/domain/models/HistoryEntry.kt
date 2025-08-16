package com.savana.domain.models


data class HistoryEntry(
    val id: Int,
    val label: String,
    val status: Status,
    val trackInfo: TrackInfo? = null
)

enum class Status{
    Analyzing,
    Deny,
    Success;

    companion object{
        fun getStatus(statusCode: Int): Status{
            return when(statusCode){
                200 -> Success
                202 -> Analyzing
                102 -> Analyzing
                else -> Deny
            }
        }
    }
}