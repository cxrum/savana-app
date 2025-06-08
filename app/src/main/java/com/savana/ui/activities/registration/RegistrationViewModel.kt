package com.savana.ui.activities.registration

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savana.R
import com.savana.core.utils.Event
import com.savana.domain.models.AvatarData
import com.savana.domain.models.user.RegistrationData
import com.savana.domain.usecases.authentication.GetAvatarsUseCase
import com.savana.domain.usecases.authentication.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URL
import kotlin.random.Random

class RegistrationViewModel(
    private val getAvatarsUseCase: GetAvatarsUseCase,
    private val registerUseCase: RegisterUseCase
): ViewModel() {

    private val _state: MutableStateFlow<RegistrationState> = MutableStateFlow(RegistrationState())
    val state: StateFlow<RegistrationState> = _state.asStateFlow()

    private val _authEventLiveData = MutableLiveData<Event<Unit>>()
    val authEventLiveData: LiveData<Event<Unit>> = _authEventLiveData

    private val _currentStepLiveData = MutableStateFlow<Steps?>(Steps.EMAIL)
    val currentStepLiveData: StateFlow<Steps?> = _currentStepLiveData.asStateFlow()

    private val _navigateToStepEvent = MutableStateFlow<Event<Steps>?>(null)
    val navigateToStepEvent: StateFlow<Event<Steps>?> = _navigateToStepEvent.asStateFlow()

    private val _avatars: MutableLiveData<List<AvatarData>> =  MutableLiveData<List<AvatarData>>(
        emptyList())
    val avatars: LiveData<List<AvatarData>> = _avatars

    init {
        updateAvatars()
    }

    fun register(){
        viewModelScope.launch {
            val email = _state.value.email!!
            val password = _state.value.password!!
            val username = _state.value.nickname!!
            val avatarId = _state.value.avatarId!!

            registerUseCase.invoke(
                RegistrationData(
                    email = email,
                    username = username,
                    password = password,
                    avatarId = avatarId
                )
            )
        }
    }

    fun userRequestsNextStep() {
        _currentStepLiveData.value?.next()?.let { nextStep ->
            _navigateToStepEvent.value = Event(nextStep)
        }
    }

    fun userRequestsPreviousStep() {
        _currentStepLiveData.value?.previous()?.let { previousStep ->
            _navigateToStepEvent.value = Event(previousStep)
        } ?: goToAuthentication()
    }

    fun stepChanged(newStep: Steps) {
        if (_currentStepLiveData.value != newStep) {
            _currentStepLiveData.value = newStep
        }
        if (_navigateToStepEvent.value?.peekContent() == newStep) {
            _navigateToStepEvent.value = null
        }
    }

    fun goToAuthentication() {
        _authEventLiveData.value = Event(Unit)
    }

    fun updateAvatars(){

        viewModelScope.launch {
            _avatars.value = getAvatarsUseCase()
        }
    }

    fun getRandomAvatar(): AvatarData?{
        return avatars.value?.get(Random.nextInt() % (avatars.value?.size ?: 1))
    }

    fun onEmailChanged(email: String, context: Context){
        if (validateEmail(email)){
            setEmail(email)
            setError(null)
        }else{
            setError(context.getString(R.string.email_is_not_correct))
        }
    }

    fun onAvatarIdChanged(id: Int, context: Context){
        setAvatarId(id)
        setError(null)
    }

    fun onNicknameChanged(email: String, context: Context){
        setNickname(email)
        setError(null)
    }

    fun onPasswordChanged(password: String, context: Context){
        when(validatePassword(password)){
            PasswordErrors.NONE -> {
                setPassword(password)
                setError(null)
            }
            PasswordErrors.TOO_SHORT -> {
                setError(context.getString(R.string.password_too_short))
            }
            PasswordErrors.TOO_LONG -> {
                setError(context.getString(R.string.password_too_long))
            }
            PasswordErrors.MUST_HAVE_ONE_UPPER_LETTER -> {
                setError(context.getString(R.string.password_must_contest_one_upper_letter))
            }
            PasswordErrors.MUST_HAVE_ONE_LOWER_LETTER -> {
                setError(context.getString(R.string.password_must_contest_one_lower_letter))
            }
            PasswordErrors.MUST_HAVE_ONE_DIGIT -> {
                setError(context.getString(R.string.password_must_contest_one_digit))
            }
            PasswordErrors.MUST_HAVE_ONE_SPEC_SYMBOL -> {
                setError(context.getString(R.string.password_must_contest_one_spec_symbol))
            }
        }
    }

    fun setError(error: String?){
        _state.value = state.value.copy(errorMessage = error)
    }

    private fun setEmail(email: String){
        _state.value = state.value.copy(email = email.trim())
    }

    private fun setPassword(email: String){
        _state.value = state.value.copy(password = email.trim())
    }

    private fun setAvatarId(id: Int){
        _state.value = state.value.copy(avatarId = id)
    }

    private fun setNickname(nickname: String){
        _state.value = state.value.copy(nickname = nickname.trim())
    }

    private fun validateEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    }

    private fun validatePassword(password: String): PasswordErrors {
        val trimmedPassword = password.trim()

        if (trimmedPassword.length < 8) {
            return PasswordErrors.TOO_SHORT
        }
        if (trimmedPassword.length > 16) {
            return PasswordErrors.TOO_LONG
        }

        if (!trimmedPassword.contains(Regex("[A-Z]"))) {
            return PasswordErrors.MUST_HAVE_ONE_UPPER_LETTER
        }

        if (!trimmedPassword.contains(Regex("[a-z]"))) {
            return PasswordErrors.MUST_HAVE_ONE_LOWER_LETTER
        }

        if (!trimmedPassword.contains(Regex("[0-9]"))) {
            return PasswordErrors.MUST_HAVE_ONE_DIGIT
        }

        if (!trimmedPassword.contains(Regex("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?~`]"))) {
            return PasswordErrors.MUST_HAVE_ONE_SPEC_SYMBOL
        }

        return PasswordErrors.NONE
    }

    companion object {
        enum class Steps(val stepOrder: Int) {
            EMAIL(0),
            NAME(1),
            PASSWORD(2),
            AVATAR(3),
            WELCOME(4),
            REGISTERED(5);


            fun next(): Steps? {
                val currentIndex = this.ordinal
                return if (currentIndex < entries.size - 1) {
                    entries[currentIndex + 1]
                } else {
                    null
                }
            }

            fun previous(): Steps? {
                val currentIndex = this.ordinal
                return if (currentIndex > 0) {
                    entries[currentIndex - 1]
                } else {
                    null
                }
            }
        }

        enum class PasswordErrors {
            NONE,
            TOO_SHORT,
            TOO_LONG,
            MUST_HAVE_ONE_UPPER_LETTER,
            MUST_HAVE_ONE_LOWER_LETTER,
            MUST_HAVE_ONE_DIGIT,
            MUST_HAVE_ONE_SPEC_SYMBOL
        }
    }
}