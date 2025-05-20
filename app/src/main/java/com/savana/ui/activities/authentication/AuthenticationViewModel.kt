package com.savana.ui.activities.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.savana.ui.activities.registration.RegistrationState

class AuthenticationViewModel {

    val _state: MutableLiveData<AuthenticationState> = MutableLiveData(AuthenticationState())
    val state: LiveData<AuthenticationState> = _state



}