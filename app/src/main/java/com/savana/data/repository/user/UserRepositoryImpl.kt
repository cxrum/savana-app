package com.savana.data.repository.user

import com.savana.core.exeption.AuthenticationException
import com.savana.core.exeption.NoTempedUserException
import com.savana.core.exeption.UserException
import com.savana.data.local.user.UserDao
import com.savana.di.api
import com.savana.domain.models.HistoryEntry
import com.savana.domain.models.SelectedTrackGap
import com.savana.domain.models.Status
import com.savana.domain.models.user.UserData
import com.savana.domain.models.user.toDomain
import com.savana.domain.repository.user.UserRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.UnknownHostException

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
                Result.success(userResponse.data!!.user.toDomain())
            } else {
                Result.failure(UserException("Failed to get user info with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun sanitizeFileName(input: String): String {
        val nameWithoutExtension = input.substringBeforeLast('.', input)
        return nameWithoutExtension.filter { it.code in 32..126 }
    }

    override suspend fun sendTrackToAnalyze(track: SelectedTrackGap): Result<Int> {
        return try {
            val userId = userDao.getId()
                ?: return Result.failure(NoTempedUserException())

            val requestBody = track.trackData.toRequestBody("audio/mpeg".toMediaTypeOrNull())

            val response = api.userService.uploadTrack(
                contentType = "audio/mpeg",
                contentDescription = "attachment; filename=\"${sanitizeFileName(track.trackTitle)}\"",
                contentLength = track.trackData.size.toLong(),
                userId = userId,
                body = requestBody
            )

            if (response.isSuccessful) {
                val body = response.body()
                val trackId = body?.data?.trackId
                if (trackId != null) {
                    Result.success(trackId)
                } else {
                    Result.failure(Exception("trackId is null"))
                }
            } else {
                Result.failure(Exception("Upload failed with code: ${response.code()}"))
            }

        } catch (e: UnknownHostException){
            Result.failure(AuthenticationException(null))
        } finally {
            Result.failure<Exception>(Exception())
        }
    }

    override suspend fun getHistory(): Result<List<HistoryEntry>> {
        return try {
            val userId = userDao.getId()
                ?: return Result.failure(NoTempedUserException())

            val response = api.userService.history(userId)

            if (response.isSuccessful){
                var result = buildList{
                    response.body()!!.data!!.history.map {
                        add(
                            HistoryEntry(
                                id = it.userTrack.id,
                                label = it.userTrack.name,
                                status = Status.getStatus(it.userTrack.status),
                            )
                        )
                    }
                }
                Result.success(result)
            }else{
                Result.failure(Exception())
            }
        }catch (e: UnknownHostException){
            Result.failure(AuthenticationException(null))
        } finally {
            Result.failure<Exception>(Exception())
        }
    }

}