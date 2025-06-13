package com.savana.data.network.services.user

import com.savana.data.network.model.ApiResponse
import com.savana.data.network.model.user.LoginRequest
import com.savana.data.network.model.user.RegisterRequest
import com.savana.data.network.model.user.RegisterResponseData
import com.savana.data.network.model.user.TrackUploaded
import com.savana.data.network.model.user.UserData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {

    @GET("{userId}")
    suspend fun getInfo(
        @Path("userId") userId: Int
    ): Response<ApiResponse<UserData>>

    @POST("register")
    suspend fun register(
        @Body body: RegisterRequest
    ): Response<ApiResponse<RegisterResponseData>>

    @POST("login")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<ApiResponse<UserData>>

    @POST("{userId}/tracks")
    suspend fun uploadTrack(
        @Header("Content-Type") contentType: String = "audio/mpeg",
        @Header("Content-Description") contentDescription: String,
        @Header("Content-Length") contentLength: Long,
        @Path("userId") userId: Int,
        @Body body: RequestBody
    ): Response<ApiResponse<TrackUploaded>>

}