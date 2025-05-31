package com.savana.domain.usecases.authentication

import com.savana.domain.repository.authentication.AuthenticationRepository

class RegisterUseCase(
    private val authenticationRepository: AuthenticationRepository
) {

    suspend operator fun invoke(){

    }

}