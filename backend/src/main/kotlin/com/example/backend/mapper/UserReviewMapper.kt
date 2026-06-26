package com.example.backend.mapper

import com.example.backend.model.UserReview
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface UserReviewMapper {
    fun findByRestaurantId(restaurantId: Int): List<UserReview>?
        
    fun findByReviewId(reviewId: Int): UserReview?

    fun insertReview(review: UserReview)

    fun deleteByReviewIdByUserId(@Param("reviewId") reviewId: Int, @Param("userId") userId: Int)

    fun caluclateAverageRating(restaurantId: Int): Double?
}