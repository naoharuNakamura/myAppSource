package com.example.backend.service

import com.example.backend.dto.userreview.UserReviewResponse
import com.example.backend.exception.DomainException
import com.example.backend.mapper.RestaurantMapper
import com.example.backend.mapper.UserReviewMapper
import com.example.backend.model.UserReview
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserReviewService(
    val userreviewMapper: UserReviewMapper,
    val restaurantService: RestaurantService,
    val userService: UserService,
) {
    @Transactional
    fun addReviewAndSync(userId: Int, restaurantId: Int, rating: Int?, memo: String?) {
        if (rating == null || rating !in 1..5) throw DomainException.InvalidInput()
        val newReview = UserReview(
            userId = userId,
            restaurantId = restaurantId,
            userRating = rating,
            userMemo = memo
        )
        userreviewMapper.insertReview(newReview)
        restaurantService.updateRestaurant(restaurantId)
    }

    fun getAllReview(restaurantId: Int): List<UserReviewResponse>? {
        if (!restaurantService.checkRestaurantExist(restaurantId)) throw DomainException.ResourceNotFound()
        val reviews = userreviewMapper.findByRestaurantId(restaurantId)
        return reviews
            ?.filter { !it.userMemo.isNullOrBlank() }
            ?.map {
                val username = userService.getUserName(it.userId)
                UserReviewResponse(
                    memo = it.userMemo,
                    userId = it.userId,
                    userName = username,
                    userRating = it.userRating,
                    reviewId = it.reviewId
                )
            } ?: emptyList()
    }

    @Transactional
    fun calculateAverageRating(restaurantId: Int): Double = userreviewMapper.caluclateAverageRating(restaurantId) ?: 0.0

    @Transactional
    fun deleteReview(reviewId: Int, userId: Int) {
        val review = userreviewMapper.findByReviewId(reviewId) ?:  throw DomainException.ResourceNotFound()
        if (review.userId != userId) throw DomainException.Review.Forbidden()
        userreviewMapper.deleteByReviewIdByUserId(reviewId, userId)
    }
}