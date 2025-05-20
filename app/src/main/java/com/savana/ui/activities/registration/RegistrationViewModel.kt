package com.savana.ui.activities.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegistrationViewModel: ViewModel() {

    val _state: MutableLiveData<RegistrationState> = MutableLiveData(RegistrationState())
    val state: LiveData<RegistrationState> = _state

}