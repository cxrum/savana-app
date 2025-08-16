package com.savana.domain.usecases.user

import com.savana.domain.models.user.UserData
import com.savana.domain.repository.user.UserRepository

class UploadTrackUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(): Result<UserData>{
        return userRepository.getInfo()
    }

}