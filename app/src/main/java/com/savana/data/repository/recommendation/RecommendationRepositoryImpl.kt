package com.savana.data.repository.recommendation

import com.savana.data.network.model.recommendation.RecommendationResponse
import com.savana.domain.repository.recommendation.RecommendationRepository

class RecommendationRepositoryImpl:
    RecommendationRepository {

    override suspend fun fetchData(): RecommendationResponse?{
        return RecommendationResponse(data = "Aboba")
    }

}