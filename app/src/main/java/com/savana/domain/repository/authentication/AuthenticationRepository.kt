package com.savana.domain.repository.authentication

interface AuthenticationRepository {

    suspend fun login()
    suspend fun register()

}