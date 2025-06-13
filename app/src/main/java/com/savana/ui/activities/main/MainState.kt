package com.savana.ui.activities.main

data class MainState(
    val caption: String? = null,
    val isLoading: Boolean = false,
    val canLeaveLoadingScreen: Boolean = false
)