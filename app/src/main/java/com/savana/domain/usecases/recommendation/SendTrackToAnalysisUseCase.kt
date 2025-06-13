package com.savana.domain.usecases.recommendation

import com.savana.domain.models.SelectedTrackGap
import com.savana.domain.repository.user.UserRepository

class SendTrackToAnalysisUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(gap: SelectedTrackGap): Result<Int>{


        val result = userRepository.sendTrackToAnalyze(gap)
        return result
    }

}