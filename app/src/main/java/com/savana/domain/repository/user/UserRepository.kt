package com.savana.domain.repository.user

import com.savana.domain.models.user.UserData

interface UserRepository {

    suspend fun getInfo(): Result<UserData>

}