package com.savana.ui.fragments.main.search.recomedation

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.savana.databinding.FragmentRecomedationsBinding
import com.savana.databinding.ViewSongPlayerBinding
import com.savana.domain.models.RecommendedTrack
import com.savana.ui.activities.main.MainViewModel
import com.savana.ui.fragments.main.search.main.RadarChartData
import com.savana.ui.fragments.main.search.main.charDataPlaceholder
import com.savana.ui.player.AudioPlayerViewModel
import com.savana.ui.view.songplayer.SongPlayerView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

@UnstableApi
class RecommendationsFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModel()
    private val recommendationViewModel: RecommendationViewModel by viewModel()
    private val songPlayerView: AudioPlayerViewModel by viewModel()

    private var _binding: FragmentRecomedationsBinding? = null
    private val binding get() = _binding!!

    private val activeSongPlayerViews = mutableMapOf<Int, SongPlayerView>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecomedationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupObservers()

        setChartData(charDataPlaceholder)
        recommendationViewModel.setUpList()
    }

    private fun setupListeners(){

    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    recommendationViewModel.charData.collect { data ->
                        setChartData(data)
                    }
                }

                launch {
                    recommendationViewModel.state.collect { state ->
                        val trackIdToPlay = state.currentPlayingTrackId
                        val currentTracks = recommendationViewModel.tracks.value

                        if (trackIdToPlay != null) {
                            val trackData = currentTracks[trackIdToPlay]
                            if (trackData?.bytesArray != null) {
                                songPlayerView.prepareAudio(trackData.bytesArray)
                                songPlayerView.seekToSec(trackData.currentSecond)
                                if (trackData.isPlaying) {
                                    songPlayerView.play()
                                } else {
                                    songPlayerView.pause()
                                }
                            } else {
                                songPlayerView.pause()
                            }
                        } else {
                            songPlayerView.pause()
                        }
                        updateAllPlayerViewsPlayingState(currentTracks.values.toList(), trackIdToPlay)
                    }
                }

                launch {
                    recommendationViewModel.tracks.collect { tracks ->
                        displayRecommendedTracks(tracks.values.toList(),)
                    }
                }

            }
        }
    }

    private fun displayRecommendedTracks(tracks: List<RecommendedTrack>) {
        binding.recommendationsContainer.removeAllViews()
        activeSongPlayerViews.clear()

        val currentlyPlayingTrackIdFromState = recommendationViewModel.state.value.currentPlayingTrackId

        tracks.forEach { track ->
            val playerView = SongPlayerView(requireContext())
            activeSongPlayerViews[track.id] = playerView

            playerView.apply {
                setSongName(track.trackTitle)
                setTotalDurationSeconds(track.totalDurationSeconds)
                setCurrentProgressSeconds(track.currentSecond)

                val isThisTrackActuallyPlaying = track.id == currentlyPlayingTrackIdFromState && track.isPlaying
                setPlayingState(isThisTrackActuallyPlaying)

                onPlayPauseToggleListener = { wantsToPlay ->
                    recommendationViewModel.setTrackPlayingState(track.id, wantsToPlay)
                }

                onSeekBarChangedByUserListener = { newProgressSeconds ->
                    val currentActiveTrackIdInVm = recommendationViewModel.state.value.currentPlayingTrackId

                    recommendationViewModel.setCurrentSecond(track.id, newProgressSeconds)
                    if (track.id != currentActiveTrackIdInVm) {
                        recommendationViewModel.setTrackPlayingState(track.id, true)
                    } else {
                        songPlayerView.seekToSec(newProgressSeconds)
                    }
                }
            }
            binding.recommendationsContainer.addView(playerView)
        }
    }

    private fun updateAllPlayerViewsPlayingState(tracks: List<RecommendedTrack>, currentlyPlayingTrackId: Int?) {
        tracks.forEach { track ->
            val playerView = activeSongPlayerViews[track.id]
            val isPlayingThisTrack = track.id == currentlyPlayingTrackId && track.isPlaying
            playerView?.setPlayingState(isPlayingThisTrack)
            if (isPlayingThisTrack) {
                playerView?.setCurrentProgressSeconds(track.currentSecond)
            } else if (playerView?.isActuallyPlaying == true) {
                playerView.setPlayingState(false)
            }
        }
    }

    private fun setupRadarChartAppearance(radarChart: RadarChart) {
        with(radarChart) {
            description.isEnabled = false
            webLineWidth = 1f
            webColor = Color.LTGRAY
            webLineWidthInner = 1f
            webColorInner = Color.LTGRAY
            webAlpha = 100

            val yAxis: YAxis = this.yAxis
            yAxis.axisMinimum = 0f
            yAxis.labelCount = 5
            yAxis.setDrawLabels(true)
            yAxis.textColor = Color.GRAY
            yAxis.textSize = 9f

            val xAxis: XAxis = this.xAxis
            xAxis.textSize = 11f
            xAxis.textColor = Color.DKGRAY

            legend.isEnabled = true
        }
    }

    private fun setupChartDataSetUI(dataSet: RadarDataSet) {
        with(dataSet) {
            color = Color.RED
            lineWidth = 2f

            fillColor = Color.RED
            fillAlpha = 100
            setDrawFilled(true)

            setDrawValues(true)
            valueTextColor = Color.BLACK
            valueTextSize = 10f

        }
    }

    private fun setChartData(customData: RadarChartData) {
        if (customData.chartData.isEmpty()) {
            binding.radarChart.clear()
            binding.radarChart.invalidate()
            return
        }

        setupRadarChartAppearance(binding.radarChart)

        val dataSet = RadarDataSet(customData.chartData, "")
        setupChartDataSetUI(dataSet)
        val radarData = RadarData()
        radarData.addDataSet(dataSet)

        val xAxisLabels = mutableListOf<String>()
        for (i in customData.chartData.indices) {
            val data = customData.chartData[i].data.toString()
            xAxisLabels.add(data)
        }
        
        binding.radarChart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)

        binding.radarChart.data = radarData
        binding.radarChart.invalidate()
    }

}