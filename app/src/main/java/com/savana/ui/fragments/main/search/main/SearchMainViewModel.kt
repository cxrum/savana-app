package com.savana.ui.fragments.main.search.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchMainViewModel: ViewModel() {

    private val _state = MutableStateFlow(SearchMainState())
    val state: StateFlow<SearchMainState> = _state.asStateFlow()

    fun setFile(uri: Uri?, fileName: String?){
        _state.value = state.value.copy(fileName = fileName, uri = uri)
    }



}