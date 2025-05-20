package com.savana.di

import com.savana.ui.activities.main.MainViewModel
import com.savana.ui.fragments.main.search.selection.GapSelectionViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appMainModule = module {

    viewModel {
        MainViewModel()
    }

    viewModel {
        GapSelectionViewModel()
    }

}