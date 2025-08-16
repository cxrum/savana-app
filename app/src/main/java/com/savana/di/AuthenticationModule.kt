package com.savana.di

import com.savana.core.newtwork.ConnectivityObserver
import com.savana.core.newtwork.NetworkConnectivityObserver
import com.savana.data.repository.authentication.AuthenticationRepositoryImpl
import com.savana.domain.repository.authentication.AuthenticationRepository
import com.savana.domain.usecases.authentication.CheckAuthenticationUseCase
import com.savana.domain.usecases.authentication.LoginUseCase
import com.savana.ui.activities.authentication.AuthenticationViewModel
import com.savana.ui.splash.StartupViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authenticationModule = module {

    single<ConnectivityObserver> {
        NetworkConnectivityObserver(
            get()
        )
    }

    single<AuthenticationRepository>{
        AuthenticationRepositoryImpl(
            get()
        )
    }

    single {
        LoginUseCase(
            get()
        )
    }

    viewModel {
        AuthenticationViewModel(
            get(),
            get()
        )
    }

    viewModel {
        StartupViewModel(
            checkAuthenticationUseCase = CheckAuthenticationUseCase(
                get()
            )
        )
    }

}