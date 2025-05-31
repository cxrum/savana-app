package com.savana.domain.repository.recommendation

import com.savana.data.network.model.recommendation.RecommendationResponse

class RecommendationRepositoryImpl:
    RecommendationRepository {

    override suspend fun fetchData(): RecommendationResponse?{
        return RecommendationResponse(data = "Aboba")
    }

}