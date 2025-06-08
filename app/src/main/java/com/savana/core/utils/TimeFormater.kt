package com.savana.core.utils

public fun formatTime(seconds: Int): String {
    if (seconds == 0) return "0:00"
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%d:%02d", minutes, remainingSeconds)
}