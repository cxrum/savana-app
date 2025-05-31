package com.savana.ui.fragments.main.search.selection

import android.graphics.Bitmap

data class GapSelectionState(
    val isLoading: Boolean = false,
    val title: String? = null,
    val fileName: String? = null,
    val isAudioLoaded: Boolean = false,
    val errorMessage: String? = null,
    val startSec: Int? = null,
    val endSec: Int? = null,
    val cover: Bitmap? = null
)