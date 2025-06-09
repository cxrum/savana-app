package com.savana.di

import android.annotation.SuppressLint
import com.savana.domain.repository.recommendation.RecommendationRepository
import com.savana.domain.repository.recommendation.RecommendationRepositoryImpl
import com.savana.domain.repository.user.UserRepositories
import com.savana.domain.repository.user.UserRepositoriesImpl
import com.savana.domain.usecases.history.GetHistoryUseCase
import com.savana.domain.usecases.recommendation.GetRecommendationsUseCase
import com.savana.domain.usecases.recommendation.SendTrackToAnalysisUseCase
import com.savana.ui.activities.main.MainViewModel
import com.savana.ui.fragments.main.search.main.SearchMainViewModel
import com.savana.ui.fragments.main.search.recomedation.MusicPlayerViewModel
import com.savana.ui.fragments.main.search.recomedation.RecommendationViewModel
import com.savana.ui.fragments.main.search.selection.GapSelectionViewModel
import com.savana.ui.player.AudioPlayerViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

@SuppressLint("UnsafeOptInUsageError")
val appMainModule = module {

    single {
        GetHistoryUseCase()
    }

    single {
        GetRecommendationsUseCase()
    }

    single {
        SendTrackToAnalysisUseCase()
    }

    single<RecommendationRepository> {
        RecommendationRepositoryImpl()
    }

    single<UserRepositories> {
        UserRepositoriesImpl()
    }

    viewModel {
        AudioPlayerViewModel(
            get()
        )
    }

    viewModel {
        RecommendationViewModel(
        )
    }
    viewModel {
        MusicPlayerViewModel(
            get()
        )
    }

    viewModel {
        MainViewModel(
            get(),
            get(),
            get()
        )
    }

    viewModel {
        GapSelectionViewModel()
    }

    viewModel {
        SearchMainViewModel()
    }
}

