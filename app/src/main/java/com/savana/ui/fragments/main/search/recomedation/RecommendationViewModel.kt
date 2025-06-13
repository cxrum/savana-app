package com.savana.ui.fragments.main.search.recomedation

import androidx.lifecycle.ViewModel
import com.savana.domain.models.RadarChartData
import com.savana.domain.models.RecommendationData
import com.savana.domain.models.TrackInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TrackRecommendationsState(
    val trackInfos: List<TrackInfo> = emptyList()
)

class RecommendationViewModel: ViewModel() {

    private val _charData = MutableStateFlow(RadarChartData())
    val charData: StateFlow<RadarChartData> = _charData.asStateFlow()

    private val _recommendationData = MutableStateFlow(TrackRecommendationsState())
    val recommendationData: StateFlow<TrackRecommendationsState> = _recommendationData.asStateFlow()

    private val _state = MutableStateFlow(RecommendationState())
    val state: StateFlow<RecommendationState> = _state.asStateFlow()

    fun processRecommendationData(data: RecommendationData) {
        _charData.value = RadarChartData(data.uploadedTrackData.chartData)
        _recommendationData.value = TrackRecommendationsState(
            trackInfos = data.trackInfos
        )
        _state.value = state.value.copy(
            isLoading = false,
            trackTitle = data.uploadedTrackData.title,
            trackAuthor = data.uploadedTrackData.artistName
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

    enum class ScreenState{
        Recommendations,
        Analytics,
    }
}