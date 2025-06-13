package com.savana.di

import android.annotation.SuppressLint
import com.savana.domain.repository.recommendation.RecommendationRepository
import com.savana.data.repository.recommendation.RecommendationRepositoryImpl
import com.savana.data.repository.track.TracksRepositoryImpl
import com.savana.domain.repository.user.UserRepository
import com.savana.data.repository.user.UserRepositoryImpl
import com.savana.domain.repository.track.TrackRepository
import com.savana.domain.usecases.history.GetHistoryUseCase
import com.savana.domain.usecases.recommendation.GetRecommendationsUseCase
import com.savana.domain.usecases.recommendation.SendTrackToAnalysisUseCase
import com.savana.domain.usecases.user.LogoutUseCase
import com.savana.ui.activities.main.MainViewModel
import com.savana.ui.fragments.main.search.main.SearchMainViewModel
import com.savana.ui.fragments.main.search.recomedation.MusicPlayerViewModel
import com.savana.ui.fragments.main.search.recomedation.RecommendationViewModel
import com.savana.ui.fragments.main.search.selection.GapSelectionViewModel
import com.savana.ui.player.AudioPlayerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

@SuppressLint("UnsafeOptInUsageError")
val appMainModule = module {


    single<RecommendationRepository> {
        RecommendationRepositoryImpl()
    }

    single<UserRepository> {
        UserRepositoryImpl(
            get()
        )
    }

    single<TrackRepository> {
        TracksRepositoryImpl()
    }


    single {
        GetHistoryUseCase(
            get()
        )
    }

    single {
        GetRecommendationsUseCase(
            get(),
            get()
        )
    }

    single {
        SendTrackToAnalysisUseCase(
            get()
        )
    }

    single {
        LogoutUseCase(
            get()
        )
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

