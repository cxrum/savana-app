package com.savana.ui.activities.authentication

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savana.R
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

    fun login() {
        viewModelScope.launch {
            val email = state.value.email
            val password = state.value.password

            if (email.isNullOrBlank() || password.isNullOrBlank()) {
                _state.value = _state.value.copy(
                    errorMessage = "Email or password cannot be empty"
                )
                return@launch
            }

            startLoading()

            val result = loginUseCase(
                LoginData(
                    email = email,
                    password = password
                )
            )

            _state.value = _state.value.copy(
                success = result.isSuccess,
                errorMessage = if (result.isFailure) result.exceptionOrNull()?.message else null
            )
            stopLoading()
        }
    }

    fun startLoading(){
        _state.value = _state.value.copy(
            isLoading = true
        )
    }

    fun setEmail(email: String){
        _state.value = state.value.copy(email = email.trim())
    }

    fun setPassword(email: String){
        _state.value = state.value.copy(password = email.trim())
    }

    fun stopLoading(){
        _state.value = _state.value.copy(
            isLoading = false
        )
    }

    fun clearErrorMsg(){
        _state.value = _state.value.copy(
            errorMessage = null
        )
    }
}