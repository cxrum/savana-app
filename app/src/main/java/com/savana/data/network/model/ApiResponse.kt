package com.savana.data.network.model

data class ApiResponse<T>(
    val message: String? = null,
    val data: T?,
    val status: String
)