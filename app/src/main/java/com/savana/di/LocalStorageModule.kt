package com.savana.di

import com.savana.data.local.user.UserDao
import com.savana.data.local.user.UserDaoImpl
import org.koin.dsl.module

val localStorageModule = module {

    single<UserDao> {UserDaoImpl()}

}