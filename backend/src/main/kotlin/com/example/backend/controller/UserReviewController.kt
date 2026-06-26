package com.example.backend.controller

import com.example.backend.constants.ApiEndpoints
import com.example.backend.dto.userreview.UserReviewRequest
import com.example.backend.dto.userreview.UserReviewResponse
import com.example.backend.model.UserReview
import com.example.backend.service.UserReviewService
import com.example.backend.model.UserPrincipal
import com.example.backend.service.RestaurantService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiEndpoints.Review.ROOT) 
class UserReviewController(
    private val userReviewService: UserReviewService,
    private val restaurantService: RestaurantService
) {
    @PutMapping(ApiEndpoints.Review.REVIEW)
    fun addReview(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable restaurantId: Int,
        @RequestBody request: UserReviewRequest
    )= userReviewService.addReviewAndSync(user.userId, restaurantId, request.rating, request.memo)

    @GetMapping(ApiEndpoints.Review.REVIEW)
    fun getAllReview( @PathVariable restaurantId: Int): List<UserReviewResponse>? = userReviewService.getAllReview(restaurantId)
            
    
    @PostMapping(ApiEndpoints.Review.DELETE)
    fun deleteReview(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable reviewId: Int
    ): ResponseEntity<*> = ResponseEntity.ok(userReviewService.deleteReview(reviewId,user.userId)) 
}