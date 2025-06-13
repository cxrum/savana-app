package com.savana.domain.usecases.user

import com.savana.data.local.user.UserDao
import com.savana.domain.models.user.UserData

class LogoutUseCase(
    private val userDao: UserDao
) {
    operator fun invoke(){
        userDao.setId(-1)
    }
}