package com.savana.ui.fragments.main.search.recomedation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savana.domain.models.RadarChartData
import com.savana.domain.models.RecommendedTrack
import com.savana.domain.models.charDataPlaceholder
import com.savana.domain.usecases.recommendation.GetRecommendationsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TrackRecommendationsState(
    val tracks: List<RecommendedTrack> = emptyList()
)

class RecommendationViewModel(
    var application: Application,
    private val getRecommendations: GetRecommendationsUseCase
): ViewModel() {

    private val _charData = MutableStateFlow(RadarChartData())
    val charData: StateFlow<RadarChartData> = _charData.asStateFlow()

    private val _recommendationData = MutableStateFlow(TrackRecommendationsState())
    val recommendationData: StateFlow<TrackRecommendationsState> = _recommendationData.asStateFlow()

    private val _state = MutableStateFlow(RecommendationState())
    val state: StateFlow<RecommendationState> = _state.asStateFlow()

    init {
        updateRecommendation()
        songData()
    }

    private fun songData(){
        _state.value = state.value.copy(
            trackTitle = "Example fetched song title title title title title title title title",
            trackAuthor = "Example fetched song author"
        )
    }

    fun analyticsScreen(){
        _state.value = state.value.copy(
            screen = ScreenState.Analytics
        )
    }

    fun recommendationsScreen(){
        _state.value = state.value.copy(
            screen = ScreenState.Recommendations
        )
    }

    fun updateRecommendation(){
        viewModelScope.launch {
            val recommendation = getRecommendations.invoke()

            _charData.value = recommendation.chartData
            _recommendationData.value = TrackRecommendationsState(
                tracks = recommendation.recommendedTracks
            )
        }

    }

    enum class ScreenState{
        Recommendations,
        Analytics,
    }
}