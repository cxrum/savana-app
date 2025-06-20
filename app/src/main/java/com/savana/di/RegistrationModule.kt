package com.savana.di

import com.savana.domain.usecases.authentication.GetAvatarsUseCase
import com.savana.domain.usecases.authentication.RegisterUseCase
import com.savana.ui.activities.registration.RegistrationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val registrationModule = module {

    single {
        GetAvatarsUseCase()
    }

    single {
        RegisterUseCase(
            get()
        )
    }

    viewModel {
        RegistrationViewModel(
            get(),
            get(),
            get()
        )
    }

}