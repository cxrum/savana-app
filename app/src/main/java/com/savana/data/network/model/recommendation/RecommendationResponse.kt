package com.savana.data.network.model.recommendation

import com.google.gson.annotations.SerializedName

data class RecommendationResponse(
    val recommendations: List<RecommendationResponseEntry>
)

data class RecommendationResponseEntry(
    val id: Int,
    val artist: String? = null,
    val name: String,
    val status: Int,
    @SerializedName("uploaded_at")
    val uploaded: String
)