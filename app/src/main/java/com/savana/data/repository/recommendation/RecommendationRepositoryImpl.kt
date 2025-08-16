package com.savana.data.repository.recommendation

import com.savana.core.exeption.AuthenticationException
import com.savana.core.exeption.NoTempedUserException
import com.savana.data.local.user.UserDao
import com.savana.di.api
import com.savana.domain.models.Recommendation
import com.savana.domain.models.TrackInfo
import com.savana.domain.repository.recommendation.RecommendationRepository
import kotlinx.coroutines.delay
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class RecommendationRepositoryImpl(
    private val userDao: UserDao
): RecommendationRepository {
    override suspend fun recommendationTracks(
        trackId: Int
    ): Result<Recommendation> {
        return try {
            delay(500)
            val userId = userDao.getId()
                ?: return Result.failure(NoTempedUserException())

            val response = api.userService.recommendation(
                userId = userId,
                trackId = trackId
            )

            if (response.isSuccessful) {
                val body = response.body()
                val recommendations = body?.data?.recommendations

                if (recommendations?.isEmpty() == true){
                    return Result.failure(Exception("Track recommendation is empty"))
                }

                val result = Recommendation(
                    tracks = buildList {
                        recommendations?.forEach {
                            add(TrackInfo(
                                id = it.id,
                                title = it.name,
                                artistName = it.artist ?: "Unknown",
                                totalDurationSeconds = 30,
                            ))
                        }
                    }
                )

                Result.success(result)

            } else {
                Result.failure(Exception("Failed to get track recommendation: ${response.code()}"))
            }
        } catch (e: UnknownHostException) {
            Result.failure(AuthenticationException("No internet connection"))
        } catch (e: SocketTimeoutException) {
            Result.failure(AuthenticationException("Request timed out"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}