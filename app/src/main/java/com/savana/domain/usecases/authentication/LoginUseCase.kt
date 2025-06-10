package com.savana.domain.usecases.authentication

import com.savana.domain.models.user.LoginData
import com.savana.domain.models.user.UserData
import com.savana.domain.repository.authentication.AuthenticationRepository

class LoginUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(data: LoginData): Result<UserData>{
        return authenticationRepository.login(data)
    }
}