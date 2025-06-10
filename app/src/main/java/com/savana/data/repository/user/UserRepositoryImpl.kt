package com.savana.data.repository.user

import com.savana.core.exeption.NoTempedUserException
import com.savana.core.exeption.UserException
import com.savana.data.local.user.UserDao
import com.savana.di.api
import com.savana.domain.models.user.UserData
import com.savana.domain.models.user.toDomain
import com.savana.domain.repository.user.UserRepository

class UserRepositoryImpl(
    private val userDao: UserDao
):
    UserRepository
{

    override suspend fun getInfo(): Result<UserData> {
        return try {
            val userId = userDao.getId()
                ?: return Result.failure(NoTempedUserException())

            val response = api.userService.getInfo(userId)

            if (response.isSuccessful && response.body() != null) {
                val userResponse = response.body()!!
                Result.success(userResponse.toDomain())
            } else {
                Result.failure(UserException("Failed to get user info with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}