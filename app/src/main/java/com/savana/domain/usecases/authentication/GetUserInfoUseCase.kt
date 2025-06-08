package com.savana.domain.usecases.authentication

import com.savana.domain.models.user.UserData
import com.savana.domain.repository.user.UserRepositories

class GetUserInfoUseCase(
    private val userRepositories: UserRepositories
) {

    suspend operator fun invoke(): UserData{
        return UserData("Test")
    }

}