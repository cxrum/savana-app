package com.savana.domain.usecases.authentication

import com.savana.domain.repository.user.UserRepository

class CheckAuthenticationUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(): Boolean{
        return true
    }

}