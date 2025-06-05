package com.savana.ui.fragments.main.search.recomedation

import android.app.Application
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savana.domain.models.RecommendedTrack
import com.savana.ui.fragments.main.search.main.RadarChartData
import com.savana.ui.fragments.main.search.main.charDataPlaceholder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

class RecommendationViewModel(
    var application: Application
): ViewModel() {

    private val _tracks = MutableStateFlow(mapOf<Int, RecommendedTrack>())
    val tracks: StateFlow<Map<Int, RecommendedTrack>> = _tracks.asStateFlow()

    private val _state = MutableStateFlow(RecommendationState())
    val state: StateFlow<RecommendationState> = _state.asStateFlow()

    private val _charData = MutableStateFlow(RadarChartData())
    val charData: StateFlow<RadarChartData> = _charData.asStateFlow()

    init {
        setChartData(charDataPlaceholder)

    }

    fun setUpList(){
        _tracks.value = mapOf(
            Pair(1, RecommendedTrack(
                id = 1,
                trackTitle = "AbobaSong 1",
                artistName = "Boba",
                totalDurationSeconds = 101,
            )),Pair(2, RecommendedTrack(
                id = 2,
                trackTitle = "AbobaSong 2",
                artistName = "Boba",
                totalDurationSeconds = 101,
            )),Pair(5, RecommendedTrack(
                id = 5,
                trackTitle = "AbobaSong 5",
                artistName = "Boba",
                totalDurationSeconds = 101,
            )),Pair(3, RecommendedTrack(
                id = 3,
                trackTitle = "AbobaSong 3",
                artistName = "Boba",
                totalDurationSeconds = 101,
            )),Pair(12, RecommendedTrack(
                id = 12,
                trackTitle = "AbobaSong 12",
                artistName = "Boba",
                totalDurationSeconds = 101,
            )),Pair(6, RecommendedTrack(
                id = 66,
                trackTitle = "AbobaSong 66",
                artistName = "Boba",
                totalDurationSeconds = 101,
            )),
        )
    }

    fun setChartData(data: RadarChartData){
        _charData.value = data
    }

    fun setTrackPlayingState(trackId: Int, state: Boolean){
        changeCurrentTrack(trackId)
        _tracks.value[trackId]?.isPlaying = false
        setPlayingState(state)
    }

    fun setCurrentSecond(trackId: Int, second: Int){
        _tracks.value[trackId]?.currentSecond = second
    }

    private fun setPlayingState(state: Boolean){
        _state.value = _state.value.copy(
            isPlaying = state
        )
    }

    private fun changeCurrentTrack(id: Int){
        val currentTrackId = _state.value.currentPlayingTrackId
        _tracks.value[currentTrackId]?.currentSecond = 0
        _state.value = _state.value.copy(
            currentPlayingTrackId = id
        )
    }
}