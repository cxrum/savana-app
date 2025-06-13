package com.savana.domain.repository.recommendation

import com.savana.domain.models.Recommendation

interface RecommendationRepository {
    suspend fun recommendationTracks(): Result<Recommendation>
}