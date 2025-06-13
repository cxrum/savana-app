package com.savana.data.repository.recommendation

import com.savana.domain.models.Recommendation
import com.savana.domain.repository.recommendation.RecommendationRepository

class RecommendationRepositoryImpl:
    RecommendationRepository {
    override suspend fun recommendationTracks(): Result<Recommendation> {
        return Result.success(Recommendation(
            tracks = emptyList()
        ))
    }
}