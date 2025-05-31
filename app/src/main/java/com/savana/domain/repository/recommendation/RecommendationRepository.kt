package com.savana.domain.repository.recommendation

import com.savana.data.network.model.recommendation.RecommendationResponse

interface RecommendationRepository {
    suspend fun fetchData(): RecommendationResponse?
}