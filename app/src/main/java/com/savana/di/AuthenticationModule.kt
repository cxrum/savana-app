package com.savana.di

import com.savana.domain.usecases.authentication.CheckAuthenticationUseCase
import com.savana.ui.activities.authentication.AuthenticationViewModel
import com.savana.ui.splash.StartupViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authenticationModule = module {

    viewModel {
        AuthenticationViewModel()
    }

    viewModel {
        StartupViewModel(
            checkAuthenticationUseCase = CheckAuthenticationUseCase(
                userRepositories = get()
            )
        )
    }

}