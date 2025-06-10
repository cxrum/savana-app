package com.savana.data.network.services.user

import com.savana.data.network.model.user.LoginRequest
import com.savana.data.network.model.user.LoginResponse
import com.savana.data.network.model.user.RegisterRequest
import com.savana.data.network.model.user.RegisterResponse
import com.savana.data.network.model.user.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserService {

    @GET("{userId}")
    suspend fun getInfo(
        @Path("userId") userId: Int
    ): Response<UserResponse>

    @POST("register")
    suspend fun register(
        @Body body: RegisterRequest
    ): Response<RegisterResponse>

    @POST("login")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<LoginResponse>

}