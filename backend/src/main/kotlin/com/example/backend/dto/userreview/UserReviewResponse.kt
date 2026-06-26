package com.example.backend.dto.userreview

data class UserReviewResponse(
    val memo: String? = "",
    val userId: Int? = 0,
    val userName: String? = "",
    val userRating: Int? = 0,
    val reviewId: Int? = 0
)
