package com.savana.di

import com.savana.ui.activities.authentication.AuthenticationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authenticationModule = module {

    viewModel {
        AuthenticationViewModel()
    }

}