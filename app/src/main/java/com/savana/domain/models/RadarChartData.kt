package com.savana.domain.models

import com.github.mikephil.charting.data.RadarEntry

data class RadarChartData (
    val chartData: List<RadarEntry> = emptyList()
)

val charDataPlaceholder = RadarChartData(
    chartData = listOf(
        RadarEntry(45f, "Танцевальность"),
        RadarEntry(88f, "Энергия"),
        RadarEntry(15f, "Акустика"),
        RadarEntry(76f, "Позитив"),
        RadarEntry(92f, "Популярность")
    )
)