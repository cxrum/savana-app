package com.savana.data.repository.authentication

import com.savana.core.exeption.LoginFailedException
import com.savana.core.exeption.RegistrationFailedException
import com.savana.data.local.user.UserDao
import com.savana.data.network.model.user.LoginRequest
import com.savana.data.network.model.user.RegisterRequest
import com.savana.di.api
import com.savana.domain.models.user.LoginData
import com.savana.domain.models.user.RegistrationData
import com.savana.domain.models.user.UserData
import com.savana.domain.models.user.toDomain
import com.savana.domain.repository.authentication.AuthenticationRepository

class AuthenticationRepositoryImpl(
    private val userDao: UserDao
):
    AuthenticationRepository
{

    override suspend fun login(data: LoginData): Result<UserData> {
        return try {
            val response = api.userService.login(
                LoginRequest(
                    email = data.email,
                    password = data.password
                )
            )

            val body = response.body()

            if (response.isSuccessful && body != null) {
                if (body.status == "success" && body.data != null) {
                    val userResponse = body.data.user
                    userDao.setId(userResponse.id)
                    Result.success(userResponse.toDomain())
                } else {
                    val msg = body.message ?: "Unknown error"
                    Result.failure(LoginFailedException(msg))
                }
            } else {
                val msg = when(response.code()){
                    401 -> "Invalid email or password"
                    else -> "Login failed with code: ${response.code()}"
                }
                Result.failure(LoginFailedException(msg))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(data: RegistrationData): Result<UserData> {
        return try {
            val response = api.userService.register(
                RegisterRequest(
                    email = data.email,
                    password = data.password,
                    avatar = data.avatarId,
                    nickname = data.nickname
                )
            )

            if (response.isSuccessful && response.body() != null) {
                val newUserId = response.body()!!.data!!.userId
                userDao.setId(newUserId)

                val userData = UserData(
                    id = newUserId,
                    nickname = data.nickname,
                    email = data.email,
                    avatar = null
                )
                Result.success(userData)
            } else {
                Result.failure(RegistrationFailedException("Registration failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}