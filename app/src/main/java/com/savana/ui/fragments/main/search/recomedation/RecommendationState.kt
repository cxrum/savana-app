package com.savana.ui.fragments.main.search.recomedation


data class RecommendationState(
    val screen: RecommendationViewModel.ScreenState = RecommendationViewModel.ScreenState.Recommendations,
    val songImage: String? = null,
    val trackTitle: String? = null,
    val trackAuthor: String? = null,
    val isLoading: Boolean = false
)