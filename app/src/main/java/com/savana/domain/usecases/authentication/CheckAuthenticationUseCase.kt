package com.savana.domain.usecases.authentication

import com.savana.data.local.user.UserDao
import com.savana.domain.repository.user.UserRepository

class CheckAuthenticationUseCase(
    private val userDao: UserDao
) {

    suspend operator fun invoke(): Boolean{
        return userDao.getId() != null
    }

}