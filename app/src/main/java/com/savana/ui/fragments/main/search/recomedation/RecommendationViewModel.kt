package com.savana.ui.fragments.main.search.recomedation

import androidx.lifecycle.ViewModel
import com.savana.ui.fragments.main.search.main.RadarChartData
import com.savana.ui.fragments.main.search.main.charDataPlaceholder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RecommendationViewModel(): ViewModel() {

    private val _state = MutableStateFlow(RecommendationState())
    val state: StateFlow<RecommendationState> = _state.asStateFlow()

    private val _charData = MutableStateFlow(RadarChartData())
    val charData: StateFlow<RadarChartData> = _charData.asStateFlow()

    init {
        setChartData(charDataPlaceholder)
    }

    fun setChartData(data: RadarChartData){
        _charData.value = data
    }


}