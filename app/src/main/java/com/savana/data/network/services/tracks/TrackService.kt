package com.savana.data.network.services.tracks

import com.savana.data.network.model.ApiResponse
import com.savana.data.network.model.tracks.TrackInfo
import com.savana.data.network.model.tracks.TrackStatus
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface TrackService {

    @GET("{trackId}/status")
    suspend fun trackStatus(
        @Path("trackId") trackId: Int
    ): Response<ApiResponse<TrackStatus>>

    @GET("{trackId}")
    suspend fun trackInfo(
        @Path("trackId") trackId: Int
    ): Response<ApiResponse<TrackInfo>>


    @GET("{trackId}")
    suspend fun downloadTrack(
        @Header("content-type") contentType: String = "audio/mpeg",
        @Path("trackId") trackId: Int
    ):Response<ResponseBody>
}