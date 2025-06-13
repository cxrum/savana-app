package com.savana.ui.fragments.main.search.recomedation.componets

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import com.savana.R
import com.savana.databinding.FragmentAnalyticsContentBinding
import com.savana.domain.models.RadarChartData
import com.savana.ui.fragments.main.search.recomedation.RecommendationViewModel
import kotlinx.coroutines.launch

class AnalyticsContentFragment : Fragment(R.layout.fragment_analytics_content) {

    private val recommendationViewModel: RecommendationViewModel by activityViewModel()

    private var _binding: FragmentAnalyticsContentBinding? = null
    private val binding: FragmentAnalyticsContentBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticsContentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
    }

    private fun setupObservers(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    recommendationViewModel.state.collect{ state ->
                        if (state.isLoading){
                            showLoading()
                        }else{
                            showContent()
                        }
                    }
                }

                launch {
                    recommendationViewModel.charData.collect{ data ->
                        setChartData(data)
                    }
                }
            }
        }

    }

    private fun setupRadarChartAppearance(radarChart: RadarChart) {
        val webColor = ContextCompat.getColor(requireContext(), R.color.black)
        val labelColor = ContextCompat.getColor(requireContext(), R.color.black)

        with(radarChart) {
            description.isEnabled = false
            setBackgroundColor(Color.TRANSPARENT)

            this.webLineWidth = 1f
            this.webColor = webColor
            this.webLineWidthInner = 1f
            this.webColorInner = webColor
            this.webAlpha = 200

            val yAxis: YAxis = this.yAxis
            yAxis.axisMinimum = 0f
            yAxis.labelCount = 5
            yAxis.setDrawLabels(false)

            val xAxis: XAxis = this.xAxis
            xAxis.textSize = 14f
            xAxis.textColor = labelColor
            xAxis.setAvoidFirstLastClipping(true)
            xAxis.disableAxisLineDashedLine()
            legend.isEnabled = false
        }
    }

    private fun setupChartDataSetUI(dataSet: RadarDataSet) {
        val _primaryAccentColor = ContextCompat.getColor(requireContext(), R.color.colorAccent)
        val _fillColor = ContextCompat.getColor(requireContext(), R.color.colorSecondary)
        val _valueTextColor = ContextCompat.getColor(requireContext(), R.color.colorAccent)

        with(dataSet) {
            color = _primaryAccentColor
            lineWidth = 2.5f

            fillColor = _fillColor
            setDrawFilled(true)
            fillAlpha = 200

            setDrawValues(true)
            valueTextColor = _valueTextColor
            valueTextSize = 14f
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

        val radarData = RadarData(dataSet)
        binding.radarChart.data = radarData

        val xAxisLabels = customData.chartData.map { it.data.toString() }
        binding.radarChart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)

        binding.radarChart.animateXY(1400, 1400)
        binding.radarChart.invalidate()
    }

    fun showLoading(){
        binding.plotContainer.visibility = View.GONE
        binding.loading.root.visibility = View.VISIBLE
    }

    fun showContent(){
        binding.plotContainer.visibility = View.VISIBLE
        binding.loading.root.visibility = View.GONE
    }


}