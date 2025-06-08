package com.savana.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savana.domain.usecases.authentication.CheckAuthenticationUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class StartupViewModel(
    private val checkAuthenticationUseCase: CheckAuthenticationUseCase
) : ViewModel() {

    private val _navigationTarget = MutableStateFlow<NavigationTarget>(NavigationTarget.Loading)
    val navigationTarget = _navigationTarget.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            delay(1500L)

            if (checkAuthenticationUseCase()) {
                _navigationTarget.value = NavigationTarget.LoggedIn
            } else {
                _navigationTarget.value = NavigationTarget.NotLoggedIn
            }
        }
    }
}