package com.savana.data.repository.authentication

import com.savana.data.local.user.UserDao
import com.savana.domain.models.user.LoginData
import com.savana.domain.models.user.RegistrationData
import com.savana.domain.models.user.UserData
import com.savana.domain.repository.authentication.AuthenticationRepository

class AuthenticationRepositoryMockup(
    private val userDao: UserDao
): AuthenticationRepository {

    override suspend fun login(data: LoginData): Result<UserData> {
        return if (data.email == "test@example.com" && data.password == "password") {
            val user = UserData(
                id = 100,
                nickname = "TestUser",
                email = data.email,
                avatar = null
            )
            userDao.setId(user.id)
            Result.success(user)
        } else {
            Result.failure(Exception("Invalid email or password"))
        }
    }

    override suspend fun register(data: RegistrationData): Result<UserData> {
        return if (data.email.endsWith("@example.com")) {
            val user = UserData(
                id = 101,
                nickname = data.nickname,
                email = data.email,
                avatar = null
            )
            userDao.setId(user.id)
            Result.success(user)
        } else {
            Result.failure(Exception("Registration failed: unsupported domain"))
        }
    }
}