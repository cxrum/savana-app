package com.savana.di

import com.savana.ui.activities.registration.RegistrationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val registrationModule = module {

    viewModel {
        RegistrationViewModel()
    }

}