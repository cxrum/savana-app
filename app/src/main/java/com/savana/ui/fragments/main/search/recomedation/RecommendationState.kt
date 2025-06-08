package com.savana.ui.fragments.main.search.recomedation

import android.graphics.Bitmap

data class RecommendationState(
    val screen: RecommendationViewModel.ScreenState = RecommendationViewModel.ScreenState.Recommendations,
    val songImage: Bitmap? = null,
    val trackTitle: String? = null,
    val trackAuthor: String? = null,
    val isLoading: Boolean = false
)