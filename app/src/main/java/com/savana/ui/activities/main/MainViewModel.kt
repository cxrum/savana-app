package com.savana.ui.activities.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savana.domain.models.RecommendationData
import com.savana.domain.models.SelectedTrackGap
import com.savana.domain.usecases.history.GetHistoryUseCase
import com.savana.domain.usecases.recommendation.GetRecommendationsUseCase
import com.savana.domain.usecases.recommendation.SendTrackToAnalysisUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val historyUseCase: GetHistoryUseCase,
    private val sendTrackToAnalysisUseCase: SendTrackToAnalysisUseCase,
    private val getRecommendations: GetRecommendationsUseCase
): ViewModel() {

    private val _recommendationResult = MutableStateFlow<OperationState<RecommendationData>>(OperationState.Idle)
    val recommendationResult: StateFlow<OperationState<RecommendationData>> = _recommendationResult.asStateFlow()

    private val _mainState = MutableStateFlow<MainState>(MainState())
    val mainState: StateFlow<MainState> = _mainState.asStateFlow()

    private val _history = MutableStateFlow<HistoryState>(HistoryState())
    val history = _history.asStateFlow()

    fun setCaption(caption: String){
        _mainState.value = _mainState.value.copy(caption = caption)
    }

    fun historyUpdate(){
        viewModelScope.launch {

            _history.value = _history.value.copy(
                isLoading = true
            )

            _history.value = _history.value .copy(
                history = historyUseCase.invoke(),
                isLoading = false
            )
        }
    }

    fun loadRecommendationFromHistory(historyId: Int) {
        viewModelScope.launch {
            _recommendationResult.value = OperationState.Loading

            try {
                val result = getRecommendations(historyId)
                _recommendationResult.value = OperationState.Success(result)

            } catch (e: Exception) {
                _recommendationResult.value = OperationState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun startMusicAnalyzingProcess(gap: SelectedTrackGap) {
        viewModelScope.launch {

            try {
                val result: RecommendationData = sendTrackToAnalysisUseCase.invoke(gap)
                _recommendationResult.value = OperationState.Success(result)

            } catch (e: Exception) {

                _recommendationResult.value = OperationState.Error(e.message ?: "Unknown error during analysis")
            }
        }
    }

    fun operationHandled() {
        _recommendationResult.value = OperationState.Idle
    }
}