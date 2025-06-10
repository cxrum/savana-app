package com.savana.domain.models

import com.github.mikephil.charting.data.RadarEntry

data class RadarChartData (
    val chartData: List<RadarEntry> = emptyList()
)

val charDataPlaceholder = RadarChartData(
    chartData = listOf(
        RadarEntry(45f, "A"),
        RadarEntry(88f, "B"),
        RadarEntry(15f, "C"),
        RadarEntry(76f, "D"),
        RadarEntry(92f, "E")
    )
)