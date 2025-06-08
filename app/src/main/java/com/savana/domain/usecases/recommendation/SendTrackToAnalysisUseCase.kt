package com.savana.domain.usecases.recommendation

import com.savana.domain.models.RecommendationData
import com.savana.domain.models.SelectedTrackGap
import kotlinx.coroutines.delay

class SendTrackToAnalysisUseCase {

    suspend operator fun invoke(gap: SelectedTrackGap): RecommendationData{
        delay(1000)
        return RecommendationData("Test")
    }

}