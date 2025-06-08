package com.savana.ui.activities.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savana.core.utils.Event
import com.savana.domain.models.user.LoginData
import com.savana.domain.usecases.authentication.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthenticationViewModel(
    private val loginUseCase: LoginUseCase
): ViewModel() {

    private val _state: MutableStateFlow<AuthenticationState> = MutableStateFlow(AuthenticationState())
    val state: StateFlow<AuthenticationState> = _state.asStateFlow()

    private val _eventLiveData = MutableLiveData<Event<Unit>>()
    val eventLiveData: LiveData<Event<Unit>> = _eventLiveData

    fun goToRegistration() {
        _eventLiveData.value = Event(Unit)
    }

    fun login(){
        viewModelScope.launch {
            val email = state.value.email!!
            val password = state.value.password!!

            loginUseCase(
                LoginData(
                    email = email,
                    password = password
                )
            )
        }
    }
}