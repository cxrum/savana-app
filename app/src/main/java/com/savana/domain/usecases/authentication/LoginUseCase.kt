package com.savana.domain.usecases.authentication

import com.savana.domain.repository.authentication.AuthenticationRepository

class LoginUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(){

    }
}