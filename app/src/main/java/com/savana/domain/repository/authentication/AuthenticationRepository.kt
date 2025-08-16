package com.savana.domain.repository.authentication

import com.savana.domain.models.user.LoginData
import com.savana.domain.models.user.RegistrationData
import com.savana.domain.models.user.UserData

interface AuthenticationRepository {

    suspend fun login(data: LoginData): Result<UserData>
    suspend fun register(data: RegistrationData): Result<UserData>

}