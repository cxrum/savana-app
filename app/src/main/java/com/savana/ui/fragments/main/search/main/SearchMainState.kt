package com.savana.ui.fragments.main.search.main

import android.net.Uri

data class SearchMainState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val fileName: String? = null,
    val uri: Uri? = null
)
