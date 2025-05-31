package com.savana.ui.activities.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.savana.core.utils.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthenticationViewModel: ViewModel() {

    private val _state: MutableStateFlow<AuthenticationState> = MutableStateFlow(AuthenticationState())
    val state: StateFlow<AuthenticationState> = _state.asStateFlow()

    private val _eventLiveData = MutableLiveData<Event<Unit>>()
    val eventLiveData: LiveData<Event<Unit>> = _eventLiveData

    fun goToRegistration() {
        _eventLiveData.value = Event(Unit)
    }
}