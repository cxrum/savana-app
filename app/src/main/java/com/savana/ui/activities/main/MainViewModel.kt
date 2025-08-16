package com.savana.ui.activities.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savana.R
import com.savana.core.newtwork.ConnectivityObserver
import com.savana.core.newtwork.isEthernetConnected
import com.savana.domain.models.RecommendationData
import com.savana.domain.models.SelectedTrackGap
import com.savana.domain.models.Status
import com.savana.domain.models.user.UserData
import com.savana.domain.usecases.history.GetHistoryUseCase
import com.savana.domain.usecases.recommendation.GetRecommendationsUseCase
import com.savana.domain.usecases.recommendation.SendTrackToAnalysisUseCase
import com.savana.domain.usecases.user.GetUserInfoUseCase
import com.savana.domain.usecases.user.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val historyUseCase: GetHistoryUseCase,
    private val sendTrackToAnalysisUseCase: SendTrackToAnalysisUseCase,
    private val getRecommendations: GetRecommendationsUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val connectivityObserver: ConnectivityObserver
): ViewModel() {

    private val _recommendationResult = MutableStateFlow<OperationState<RecommendationData>>(OperationState.Idle)
    val recommendationResult: StateFlow<OperationState<RecommendationData>> = _recommendationResult.asStateFlow()

    private val _mainState = MutableStateFlow(MainState())
    val mainState = _mainState.asStateFlow()

    private val _history = MutableStateFlow(HistoryState())
    val history = _history.asStateFlow()

    private val _userData: MutableLiveData<UserData> = MutableLiveData()
    val userData: LiveData<UserData> = _userData

    fun userDataUpdate(context: Context){
        viewModelScope.launch {
            val userData = getUserInfoUseCase()

            if (userData.isSuccess){
                _userData.value = userData.getOrNull()!!
            }
        }
    }

    fun logout(){
        logoutUseCase.invoke()
    }

    fun setCaption(caption: String){
        if (_mainState.value.caption != caption) {
            _mainState.value = _mainState.value.copy(caption = caption)
        }
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
                    history = emptyList(),
                    isLoading = false
                )
            }
        }
    }

    fun historyUpdate(context: Context){
        viewModelScope.launch {
            if (!isEthernetConnected(connectivityObserver)){
                operationError(context.getString(R.string.no_internet_connection))
                return@launch
            }

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

    fun loadRecommendation(context: Context, historyId: Int) {
        viewModelScope.launch {
            if (!isEthernetConnected(connectivityObserver)){
                operationError(context.getString(R.string.no_internet_connection))
                return@launch
            }

            operationLoading(canLeaveScreen = false)

            val result = getRecommendations(historyId)

            if (result.isSuccess){
                _recommendationResult.value = OperationState.Success(result.getOrNull()!!)
            }else{
                operationError(result.exceptionOrNull()?.message ?: "Unknown error")
            }

        }
    }


    fun startMusicAnalyzingProcess(context: Context, gap: SelectedTrackGap) {
        viewModelScope.launch {
            if (!isEthernetConnected(connectivityObserver)){
                operationError(context.getString(R.string.no_internet_connection))
                return@launch
            }
            operationLoading(canLeaveScreen = false)

            val res = sendTrackToAnalysisUseCase.invoke(gap)

            if (res.isSuccess){
                _mainState.value = _mainState.value.copy(
                    canLeaveLoadingScreen = true
                )
                val history_id = res.getOrNull()

//                if (history_id != null){
//                    val recommendationData = historyUseCase(history_id)
//                    if (recommendationData.isSuccess){
//                        val historyEntry = recommendationData.getOrNull()
//
//                        if (historyEntry?.status == Status.Success){
//                            loadRecommendation(context, history_id)
//                        }
//                    }
//                }
            }else{
                val exception = res.exceptionOrNull()?.message ?: "Unknown error during analysis"
                _recommendationResult.value = OperationState.Error(exception)
            }
        }
    }

    fun operationHandled() {
        _recommendationResult.value = OperationState.Idle
    }

    fun operationLoading(msg: String? = null, canLeaveScreen: Boolean = true) {
        _recommendationResult.value = OperationState.Loading(msg, canLeaveScreen)
        _mainState.value = _mainState.value.copy(
            canLeaveLoadingScreen = canLeaveScreen
        )
    }

    fun operationError(msg: String){
        _recommendationResult.value = OperationState.Error(msg)
    }

    fun postError(msg: String){
        _mainState.value = _mainState.value.copy(
            error = msg
        )
    }

    fun clearError(){
        _mainState.value = _mainState.value.copy(
            error = null
        )
    }
}