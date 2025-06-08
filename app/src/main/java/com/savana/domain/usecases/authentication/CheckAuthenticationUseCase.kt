package com.savana.domain.usecases.authentication

import com.savana.domain.repository.user.UserRepositories

class CheckAuthenticationUseCase(
    private val userRepositories: UserRepositories
) {

    suspend operator fun invoke(): Boolean{
        return true
    }

}