package com.savana.ui.fragments.main.search.main

import com.github.mikephil.charting.data.RadarEntry

data class RadarChartData (
    val chartData: List<RadarEntry> = emptyList()
)

val charDataPlaceholder = RadarChartData(
    chartData = buildList {
        val entry = RadarEntry(20f, "Goi")
        add(entry)
        add(entry)
        add(entry)
        add(entry)
        add(entry)
    }
)