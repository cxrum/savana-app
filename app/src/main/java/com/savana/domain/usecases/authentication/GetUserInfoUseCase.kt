package com.savana.domain.usecases.authentication

import com.savana.domain.models.user.UserData
import com.savana.domain.repository.user.UserRepository

class GetUserInfoUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(): UserData{
        return UserData(
            nickname = "Test",
            id = 1,
            email = "test@email.com"
        )
    }

}