package com.savana.domain.models

import com.github.mikephil.charting.data.RadarEntry

data class RadarChartData (
    val chartData: List<RadarEntry> = emptyList()
)