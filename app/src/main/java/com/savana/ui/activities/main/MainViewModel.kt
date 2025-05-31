package com.savana.ui.activities.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savana.data.network.model.recommendation.RecommendationResponse
import com.savana.domain.repository.recommendation.RecommendationRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val recommendationRepository: RecommendationRepository
): ViewModel() {

    private val _recommendationResult = MutableStateFlow<OperationState<RecommendationResponse>>(OperationState.Idle)
    val recommendationResult: StateFlow<OperationState<RecommendationResponse>> = _recommendationResult.asStateFlow()

    private val _mainState = MutableStateFlow<MainState>(MainState())
    val mainState: StateFlow<MainState> = _mainState.asStateFlow()

    fun setCaption(caption: String){
        _mainState.value = _mainState.value.copy(caption = caption)
    }

    fun startMusicAnalyzingProcess() {
        viewModelScope.launch {
            _recommendationResult.value = OperationState.Loading

            try {
                val result: RecommendationResponse = recommendationRepository.fetchData()!!
                delay(10*1000L)
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