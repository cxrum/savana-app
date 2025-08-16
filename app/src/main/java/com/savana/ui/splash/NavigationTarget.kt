package com.savana.ui.splash

sealed class NavigationTarget {
    data object Loading : NavigationTarget()
    data object LoggedIn : NavigationTarget()
    data object NotLoggedIn : NavigationTarget()
}