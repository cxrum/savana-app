package com.savana.ui.fragments.main.search.recomedation

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.util.UnstableApi
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.savana.databinding.FragmentRecomedationsBinding
import com.savana.ui.activities.main.MainViewModel
import com.savana.ui.fragments.main.search.main.RadarChartData
import com.savana.ui.fragments.main.search.main.charDataPlaceholder
import com.savana.ui.fragments.main.search.recomedation.componets.RecommendationListAdapter
import com.savana.ui.player.AudioPlayerViewModel
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

    private lateinit var adapter: RecommendationListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecomedationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupListeners()
        setupObservers()

        setChartData(charDataPlaceholder)
    }

    private fun setupAdapter(){
        adapter = RecommendationListAdapter(
            onPlayPauseClicked = { track, position, wantsToPlay ->  

            },
            onItemClicked = { track, position ->  

            },
            onSeekOccurred = { track, position, newProgressSeconds ->
                
            }
        )
        binding.songList.adapter = adapter
    }

    private fun setupListeners(){

    }

    private fun setupObservers(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){

                launch {
                    recommendationViewModel.charData.collect{ data ->
                        setChartData(data)
                    }
                }

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