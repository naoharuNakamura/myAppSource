package com.example.backend.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.time.LocalDateTime

data class UserReview(
    @JsonProperty("reviewId")
    var reviewId: Int = 0,
    
    @JsonProperty("userId")
    var userId: Int = 0,
    
    @JsonProperty("restaurantId")
    var restaurantId: Int = 0,
    
    @JsonProperty("userRating")
    var userRating: Int? = 0,
    
    @JsonProperty("userMemo")
    var userMemo: String? = null,
    
    @JsonProperty("createdAt")
    var createdAt: String? = null 
) : Serializable