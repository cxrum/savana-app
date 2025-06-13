package com.savana.ui.activities.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savana.domain.models.RecommendationData
import com.savana.domain.models.SelectedTrackGap
import com.savana.domain.usecases.history.GetHistoryUseCase
import com.savana.domain.usecases.recommendation.GetRecommendationsUseCase
import com.savana.domain.usecases.recommendation.SendTrackToAnalysisUseCase
import com.savana.domain.usecases.user.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val historyUseCase: GetHistoryUseCase,
    private val sendTrackToAnalysisUseCase: SendTrackToAnalysisUseCase,
    private val getRecommendations: GetRecommendationsUseCase,
    private val logoutUseCase: LogoutUseCase
): ViewModel() {

    private val _recommendationResult = MutableStateFlow<OperationState<RecommendationData>>(OperationState.Idle)
    val recommendationResult: StateFlow<OperationState<RecommendationData>> = _recommendationResult.asStateFlow()

    private val _mainState = MutableStateFlow<MainState>(MainState())
    val mainState: StateFlow<MainState> = _mainState.asStateFlow()

    private val _history = MutableStateFlow(HistoryState())
    val history = _history.asStateFlow()

    fun logout(){
        logoutUseCase.invoke()
    }

    fun setCaption(caption: String){
        _mainState.value = _mainState.value.copy(caption = caption)
    }

    fun historyForceUpdate(){
        viewModelScope.launch {
            _history.value = _history.value.copy(
                isLoading = true
            )

            val history = historyUseCase.invoke()
            if(history.isSuccess){
                _history.value = _history.value .copy(
                    history = history.getOrNull()!!,
                    isLoading = false
                )
            }else{
                _history.value = _history.value .copy(
                    history = emptyList()
                )
            }
        }
    }

    fun historyUpdate(){
        viewModelScope.launch {
            val fetchedHistory = historyUseCase.invoke()
            if (fetchedHistory.isSuccess){
                if (history.value.history != fetchedHistory){
                    _history.value = _history.value .copy(
                        history = fetchedHistory.getOrNull()!!
                    )
                }
            }else{
                _history.value = _history.value .copy(
                    history = emptyList()
                )
            }
        }
    }

    fun loadRecommendationFromHistory(historyId: Int) {
        viewModelScope.launch {
            _recommendationResult.value = OperationState.Loading
            val result = getRecommendations(historyId)

            if (result.isSuccess){
                _recommendationResult.value = OperationState.Success(result.getOrNull()!!)
            }else{
                _recommendationResult.value = OperationState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }

        }
    }

    fun startMusicAnalyzingProcess(gap: SelectedTrackGap) {
        _recommendationResult.value = OperationState.Loading

        viewModelScope.launch {
            val res = sendTrackToAnalysisUseCase.invoke(gap)

            if (res.isSuccess){
                _mainState.value = _mainState.value.copy(
                    canLeaveLoadingScreen = true
                )
            }else{
                val exception = res.exceptionOrNull()?.message ?: "Unknown error during analysis"
                _recommendationResult.value = OperationState.Error(exception)
            }

        }
    }

    fun operationHandled() {
        _recommendationResult.value = OperationState.Idle
    }

    fun operationLoading() {
        _recommendationResult.value = OperationState.Loading
    }
}